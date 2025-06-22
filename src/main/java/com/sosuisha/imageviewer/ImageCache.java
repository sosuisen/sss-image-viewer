package com.sosuisha.imageviewer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;

/**
 * Singleton class to manage persistent app-wide state that should survive
 * across window creation and destruction.
 * 
 * This class only manages truly shared state like caches and user preferences.
 * Per-window state like marked images and slideshow settings remain in ImageNavigator.
 */
public class ImageCache {
    
    // Aspect ratio enum
    public enum AspectRatio {
        LANDSCAPE, PORTRAIT
    }
    
    private static ImageCache instance = null;
    
    // Persistent caches shared across all windows
    // Use canonical path strings as keys to avoid File object equality issues
    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private final Map<String, Double> rotationMemory = new ConcurrentHashMap<>();
    private final Map<AspectRatio, Dimension2D> aspectRatioSizes = new ConcurrentHashMap<>();
    
    private ImageCache() {
        // Private constructor for singleton
    }
    
    public static synchronized ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }
    
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