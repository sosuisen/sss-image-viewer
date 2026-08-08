package com.sosuisha.imageviewer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

/**
 * Comprehensive image service that handles both image utilities and persistent caching.
 * Combines the functionality of ImageUtil and ImageCache into a single service class.
 * 
 * This singleton class manages:
 * - Image format validation
 * - Image rotation operations
 * - Persistent app-wide image caching
 * - User preferences (rotation memory, aspect ratio sizes)
 */
public class ImageService {
    
    // Image format support
    public static final String[] AVAILABLE_IMAGE_FORMATS = {
        "gif",
        "bmp",
        "png",
        "jpeg", "jpg", "jfif",
        "webp"
    };
    
    // Aspect ratio enum
    public enum AspectRatio {
        LANDSCAPE, PORTRAIT
    }
    
    private static ImageService instance = null;
    
    // Persistent caches shared across all windows
    // Use canonical path strings as keys to avoid File object equality issues
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lastModifiedCache = new ConcurrentHashMap<>();
    private final Map<String, Double> rotationMemory = new ConcurrentHashMap<>();
    private final Map<AspectRatio, Dimension2D> aspectRatioSizes = new ConcurrentHashMap<>();

    private record CachedThumbnail(Image image, long lastModified) {
    }

    private static final int THUMBNAIL_CACHE_LIMIT = 1000;

    // LRU cache: access-ordered LinkedHashMap evicting the eldest entry over the limit
    private final Map<String, CachedThumbnail> thumbnailCache = Collections.synchronizedMap(
            new LinkedHashMap<String, CachedThumbnail>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CachedThumbnail> eldest) {
                    return size() > THUMBNAIL_CACHE_LIMIT;
                }
            });
    
    private ImageService() {
        // Private constructor for singleton
    }
    
    public static synchronized ImageService getInstance() {
        if (instance == null) {
            instance = new ImageService();
        }
        return instance;
    }
    
    // ========== Image Format Utilities ==========
    
    public static boolean isImageFile(File file) {
        return isImageFile(file.getName());
    }

    public static boolean isImageFile(String fileName) {
        var lowerCaseName = fileName.toLowerCase();
        return Arrays.stream(AVAILABLE_IMAGE_FORMATS).anyMatch(lowerCaseName::endsWith);
    }
    
    // ========== Image Rotation Utilities ==========
    
    public static Image createRotatedImage(Image originalImage, double rotation) {
        double normalizedRotation = ((rotation % 360) + 360) % 360;
        
        if (normalizedRotation == 0) {
            return originalImage;
        }
        
        // Use ImageView with rotation and take a snapshot
        ImageView tempImageView = new ImageView(originalImage);
        
        // Calculate bounds for rotated image
        javafx.geometry.Bounds bounds = tempImageView.getBoundsInLocal();
        tempImageView.getTransforms().add(new javafx.scene.transform.Rotate(normalizedRotation,
                originalImage.getWidth() / 2, originalImage.getHeight() / 2));
        bounds = tempImageView.getBoundsInParent();
        
        // Create canvas with proper size
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(bounds.getWidth(), bounds.getHeight());
        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane();
        pane.getChildren().addAll(canvas, tempImageView);
        
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        return pane.snapshot(params, null);
    }
    
    // ========== Cache Management ==========
    
    // Helper method to get canonical path safely
    private String getCanonicalPath(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (Exception e) {
            // Fallback to absolute path if canonical path fails
            return file.getAbsolutePath();
        }
    }
    
    // Image cache management
    public Image getImageFromFile(File file) {
        String path = getCanonicalPath(file);
        if (path == null) {
            return null;
        }
        long currentLastModified = file.lastModified();
        Long cachedLastModified = lastModifiedCache.get(path);
        if (cachedLastModified == null || cachedLastModified != currentLastModified) {
            Image image = new Image(file.toURI().toString());
            imageCache.put(path, image);
            lastModifiedCache.put(path, currentLastModified);
            return image;
        }
        return imageCache.get(path);
    }

    public void clearImageCache() {
        imageCache.clear();
        lastModifiedCache.clear();
    }

    /**
     * Returns a small thumbnail image for the given file, kept in an LRU
     * memory cache backed by a disk cache. A thumbnail generated from the
     * original image is saved to the disk cache in the background, so later
     * requests skip decoding the original image. Cached thumbnails are
     * discarded when the file's last modified time changes or when a
     * previous load ended with an error.
     *
     * @param file the image file (can be null)
     * @param size the maximum width and height of the thumbnail in pixels
     * @return the thumbnail image, or null if file is null
     * @throws IllegalArgumentException if size is 0 or less
     */
    public Image getThumbnailFromFile(File file, double size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive: " + size);
        }
        String path = getCanonicalPath(file);
        if (path == null) {
            return null;
        }
        String key = path + "@" + size;
        long lastModified = file.lastModified();
        CachedThumbnail cached = thumbnailCache.get(key);
        if (cached != null && cached.lastModified() == lastModified && !cached.image().isError()) {
            return cached.image();
        }

        Path thumbFile = thumbnailFileFor(path, size);
        Image fromDisk = loadThumbnailFromDisk(thumbFile, lastModified);
        if (fromDisk != null) {
            thumbnailCache.put(key, new CachedThumbnail(fromDisk, lastModified));
            return fromDisk;
        }

        Image thumbnail = new Image(file.toURI().toString(), size, size, true, true, true);
        thumbnailCache.put(key, new CachedThumbnail(thumbnail, lastModified));
        saveThumbnailWhenLoaded(thumbnail, thumbFile);
        return thumbnail;
    }

    public void clearThumbnailCache() {
        thumbnailCache.clear();
    }

    /**
     * Deletes cached thumbnails of the given file from both the memory cache
     * and the disk cache.
     *
     * @param file the image file whose thumbnails should be removed (can be null)
     */
    public void deleteThumbnailsForFile(File file) {
        String path = getCanonicalPath(file);
        if (path == null) {
            return;
        }
        var keyPrefix = path + "@";
        synchronized (thumbnailCache) {
            thumbnailCache.keySet().removeIf(k -> k.startsWith(keyPrefix));
        }
        var filePrefix = hashPath(path) + "_";
        try (var files = Files.list(getThumbnailDir())) {
            files.filter(p -> p.getFileName().toString().startsWith(filePrefix))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            System.err.println("Failed to delete thumbnail: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to list thumbnail directory: " + e.getMessage());
        }
    }

    // ========== Thumbnail Disk Cache ==========
    // File format (.thumb): 4-byte magic, int width, int height,
    // followed by zlib-compressed BGRA pixels. ImageIO is not used because
    // its GraalVM native image support is insufficient.

    private static final byte[] THUMBNAIL_MAGIC = { 'S', 'S', 'T', 'B' };
    private static final int THUMBNAIL_HEADER_SIZE = 12;

    private final ExecutorService thumbnailWriter = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "thumbnail-writer");
        thread.setDaemon(true);
        return thread;
    });

    private Path getThumbnailDir() {
        var dir = Path.of(System.getProperty("user.home"), ".sss-image-viewer", "thumbnails");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Failed to create thumbnail directory: " + e.getMessage());
        }
        return dir;
    }

    private Path thumbnailFileFor(String path, double size) {
        return getThumbnailDir().resolve(hashPath(path) + "_" + (int) size + ".thumb");
    }

    private String hashPath(String path) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var bytes = digest.digest(path.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private Image loadThumbnailFromDisk(Path thumbFile, long sourceLastModified) {
        try {
            if (!Files.exists(thumbFile)
                    || Files.getLastModifiedTime(thumbFile).toMillis() < sourceLastModified) {
                return null;
            }
            var bytes = Files.readAllBytes(thumbFile);
            if (bytes.length < THUMBNAIL_HEADER_SIZE) {
                return null;
            }
            var buffer = ByteBuffer.wrap(bytes);
            var magic = new byte[THUMBNAIL_MAGIC.length];
            buffer.get(magic);
            if (!Arrays.equals(magic, THUMBNAIL_MAGIC)) {
                return null;
            }
            int width = buffer.getInt();
            int height = buffer.getInt();
            if (width <= 0 || height <= 0 || width > 4096 || height > 4096) {
                return null;
            }
            var pixels = new byte[width * height * 4];
            try (var in = new InflaterInputStream(new ByteArrayInputStream(
                    bytes, THUMBNAIL_HEADER_SIZE, bytes.length - THUMBNAIL_HEADER_SIZE))) {
                if (in.readNBytes(pixels, 0, pixels.length) != pixels.length) {
                    return null;
                }
            }
            var image = new WritableImage(width, height);
            image.getPixelWriter().setPixels(0, 0, width, height,
                    PixelFormat.getByteBgraInstance(), pixels, 0, width * 4);
            return image;
        } catch (Exception e) {
            // Broken cache entry; regenerate from the original image
            return null;
        }
    }

    private void saveThumbnailWhenLoaded(Image thumbnail, Path thumbFile) {
        if (thumbnail.getProgress() >= 1.0) {
            if (!thumbnail.isError()) {
                saveThumbnailToDisk(thumbnail, thumbFile);
            }
            return;
        }
        // GraalVM Native Image may not support subscribe(); use addListener
        // with explicit removal instead
        ChangeListener<Number> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.doubleValue() >= 1.0) {
                    thumbnail.progressProperty().removeListener(this);
                    if (!thumbnail.isError()) {
                        saveThumbnailToDisk(thumbnail, thumbFile);
                    }
                }
            }
        };
        thumbnail.progressProperty().addListener(listener);
    }

    private void saveThumbnailToDisk(Image image, Path thumbFile) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        var reader = image.getPixelReader();
        if (width <= 0 || height <= 0 || reader == null) {
            return;
        }
        var pixels = new byte[width * height * 4];
        reader.getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), pixels, 0, width * 4);
        thumbnailWriter.execute(() -> {
            try {
                var out = new ByteArrayOutputStream();
                out.write(THUMBNAIL_MAGIC);
                out.write(ByteBuffer.allocate(8).putInt(width).putInt(height).array());
                try (var deflaterOut = new DeflaterOutputStream(out)) {
                    deflaterOut.write(pixels);
                }
                Files.write(thumbFile, out.toByteArray());
            } catch (IOException e) {
                System.err.println("Failed to save thumbnail: " + e.getMessage());
            }
        });
    }
    
    // Rotation memory management
    public Double getRotationForFile(File file) {
        String path = getCanonicalPath(file);
        return path != null ? rotationMemory.getOrDefault(path, 0.0) : 0.0;
    }
    
    public void setRotationForFile(File file, double rotation) {
        String path = getCanonicalPath(file);
        if (path != null) {
            rotationMemory.put(path, rotation);
        }
    }
    
    public void clearRotationMemory() {
        rotationMemory.clear();
    }
    
    // Aspect ratio size management
    public Dimension2D getAspectRatioSize(AspectRatio aspectRatio) {
        return aspectRatioSizes.get(aspectRatio);
    }
    
    public void setAspectRatioSize(AspectRatio aspectRatio, Dimension2D size) {
        aspectRatioSizes.put(aspectRatio, size);
    }
    
    public void clearAspectRatioSizes() {
        aspectRatioSizes.clear();
    }
    
    public AspectRatio getAspectRatio(double width, double height) {
        return width >= height ? AspectRatio.LANDSCAPE : AspectRatio.PORTRAIT;
    }
    
    // Clear all caches
    public void clearAllCaches() {
        clearImageCache();
        clearThumbnailCache();
        clearRotationMemory();
        clearAspectRatioSizes();
    }

}