package com.sosuisha.imageviewer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sosuisha.imageviewer.jfxbuilder.TextInputDialogBuilder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class ImageNavigator {
    public static final int CROSSFADE_DURATION_MILLIS = 300;
    
    private List<File> files = null;
    private File currentFile = null;
    private ObservableList<File> markedImages = FXCollections.observableArrayList();
    private BooleanProperty slideshowMode = new SimpleBooleanProperty(false);
    private Timeline slideshowTimer = null;
    private double slideshowInterval = 0.0;
    
    private java.util.function.BiConsumer<File, Boolean> onImageChange;
    private Runnable onApplyAspectRatioSize;
    
    public ImageNavigator(java.util.function.BiConsumer<File, Boolean> onImageChange, Runnable onApplyAspectRatioSize) {
        this.onImageChange = onImageChange;
        this.onApplyAspectRatioSize = onApplyAspectRatioSize;
    }
    
    public void setCurrentFile(File file) {
        this.currentFile = file;
    }
    
    public File getCurrentFile() {
        return currentFile;
    }
    
    public void updateFileList(File file) {
        if (files != null) {
            return;
        }
        var folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            var imageFiles = folder.listFiles((dir, name) -> ImageUtil.isImageFile(name));
            Arrays.sort(imageFiles);
            files = List.of(imageFiles);
        } else {
            files = List.of(file);
        }
    }
    
    public void getPreviousImage() {
        updateFileList(currentFile);
        var index = files.indexOf(currentFile);
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        File nextFile = files.get(nextIndex);
        currentFile = nextFile;
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void getNextImage() {
        updateFileList(currentFile);
        var index = files.indexOf(currentFile);
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        File nextFile = files.get(nextIndex);
        currentFile = nextFile;
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void toggleMarkOnCurrentImage() {
        if (currentFile != null) {
            if (markedImages.contains(currentFile)) {
                markedImages.remove(currentFile);
            } else {
                markedImages.add(currentFile);
            }
        }
    }
    
    public boolean isCurrentImageMarked() {
        return currentFile != null && markedImages.contains(currentFile);
    }
    
    public String getMarkedImagePosition() {
        if (currentFile == null || !markedImages.contains(currentFile)) {
            return "";
        }
        
        int position = markedImages.indexOf(currentFile) + 1;
        return position + "/" + markedImages.size();
    }
    
    public void toggleSlideshow() {
        if (slideshowMode.get()) {
            stopSlideshow();
        } else {
            startSlideshow();
        }
    }
    
    private void startSlideshow() {
        if (markedImages.isEmpty()) {
            return;
        }
        
        var dialog = TextInputDialogBuilder.create(String.valueOf(slideshowInterval))
                .title("Slideshow Settings")
                .headerText("Enter slideshow interval")
                .contentText("Seconds between images (0 = manual navigation):")
                .build();
        
        var result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                double seconds = Double.parseDouble(result.get());
                slideshowInterval = seconds;
                slideshowMode.set(true);
                
                if (seconds > 0) {
                    slideshowTimer = new Timeline(new KeyFrame(Duration.seconds(seconds), _ -> {
                        navigateToNextMarked();
                    }));
                    slideshowTimer.setCycleCount(Timeline.INDEFINITE);
                    slideshowTimer.play();
                }
            } catch (NumberFormatException e) {
                // Invalid input, do nothing
            }
        }
    }
    
    private void stopSlideshow() {
        slideshowMode.set(false);
        if (slideshowTimer != null) {
            slideshowTimer.stop();
            slideshowTimer = null;
        }
    }
       
    public void navigateToPreviousMarked() {
        if (markedImages.isEmpty()) {
            return;
        }
        
        int currentIndex = markedImages.indexOf(currentFile);
        int nextIndex = currentIndex > 0 ? currentIndex - 1 : markedImages.size() - 1;
        File nextFile = markedImages.get(nextIndex);
        currentFile = nextFile;
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void navigateToNextMarked() {
        if (markedImages.isEmpty()) {
            return;
        }
        
        int currentIndex = markedImages.indexOf(currentFile);
        int nextIndex = currentIndex < markedImages.size() - 1 ? currentIndex + 1 : 0;
        File nextFile = markedImages.get(nextIndex);
        currentFile = nextFile;
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public File getFirstMarkedImage() {
        return markedImages.isEmpty() ? null : markedImages.get(0);
    }
    
    // Getters
    public BooleanProperty slideshowModeProperty() {
        return slideshowMode;
    }
    
    public ObservableList<File> getMarkedImages() {
        return markedImages;
    }
    
    public double getSlideshowInterval() {
        return slideshowInterval;
    }
    
    public boolean shouldUseCrossFade() {
        // Don't use cross-fade if slideshow is active and interval is too short
        if (slideshowMode.get() && slideshowInterval > 0 && 
            slideshowInterval * 1000 <= CROSSFADE_DURATION_MILLIS) {
            return false;
        }
        return true;
    }
    
}