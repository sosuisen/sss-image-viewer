package com.sosuisha.imageviewer;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        "jpeg", "jpg", "jfif"
    };
    
    // Aspect ratio enum
    public enum AspectRatio {
        LANDSCAPE, PORTRAIT
    }
    
    private static ImageService instance = null;
    
    // Persistent caches shared across all windows
    // Use canonical path strings as keys to avoid File object equality issues
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private final Map<String, Double> rotationMemory = new ConcurrentHashMap<>();
    private final Map<AspectRatio, Dimension2D> aspectRatioSizes = new ConcurrentHashMap<>();
    
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
        return imageCache.computeIfAbsent(path, p -> new Image(file.toURI().toString()));
    }
    
    public void clearImageCache() {
        imageCache.clear();
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
        clearRotationMemory();
        clearAspectRatioSizes();
    }
}