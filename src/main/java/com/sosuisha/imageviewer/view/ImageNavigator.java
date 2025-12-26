package com.sosuisha.imageviewer.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.view.jfxbuilder.TextInputDialogBuilder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class ImageNavigator {
    public static final int CROSSFADE_DURATION_MILLIS = 300;
    
    private List<File> files = null;
    private ObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);
    private ObservableList<File> markedImages = FXCollections.observableArrayList();
    private BooleanProperty slideshowMode = new SimpleBooleanProperty(false);
    private BooleanProperty isCurrentImageMarked = new SimpleBooleanProperty(false);
    private BooleanProperty canStartSlideShow = new SimpleBooleanProperty(false);
    private Timeline slideshowTimer = null;
    private double slideshowInterval = 0.0;
    
    private java.util.function.BiConsumer<File, Boolean> onImageChange;
    private Runnable onApplyAspectRatioSize;
    
    public ImageNavigator(java.util.function.BiConsumer<File, Boolean> onImageChange, Runnable onApplyAspectRatioSize) {
        this.onImageChange = onImageChange;
        this.onApplyAspectRatioSize = onApplyAspectRatioSize;
        isCurrentImageMarked.bind(Bindings.createBooleanBinding(() -> markedImages.contains(currentFile.get()), currentFile, markedImages));
        canStartSlideShow.bind(Bindings.createBooleanBinding(() -> !markedImages.isEmpty(), markedImages));
    }
    
    public void setCurrentFile(File file) {
        this.currentFile.set(file);
    }
    
    public File getCurrentFile() {
        return currentFile.get();
    }
    
    public ObjectProperty<File> getCurrentFileProperty() {
        return currentFile;
    }

    public boolean getCanStartSlideShow() {
        return canStartSlideShow.get();
    }
    
    public void updateFileList(File file) {
        if (files != null) {
            return;
        }
        var folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            var imageFiles = folder.listFiles((dir, name) -> ImageService.isImageFile(name));
            Arrays.sort(imageFiles);
            files = new ArrayList<>(Arrays.asList(imageFiles));
        } else {
            files = new ArrayList<>(Arrays.asList(file));
        }
    }

    /**
     * Deletes the current image file from the file system and navigates to the next available image.
     * @return true if there are still images available after deletion, false if no images remain
     */
    public boolean deleteCurrentFile() {
        if (currentFile.get() == null || files == null || files.isEmpty()) {
            return false;
        }

        File fileToDelete = currentFile.get();
        int currentIndex = files.indexOf(fileToDelete);

        if (currentIndex == -1) {
            return false;
        }

        // Try to delete the actual file first
        boolean deleted = fileToDelete.delete();

        if (!deleted) {
            // Deletion failed, return true since files still exist
            return true;
        }

        // Deletion succeeded, now update the internal state
        // Remove from marked images if it's marked
        markedImages.remove(fileToDelete);

        // Remove from the file list
        files.remove(currentIndex);

        // Determine the next file to show
        if (files.isEmpty()) {
            // No more files
            currentFile.set(null);
            return false;
        }

        // Navigate to the next file (prefer same index, or previous if at end)
        int nextIndex = currentIndex < files.size() ? currentIndex : currentIndex - 1;
        File nextFile = files.get(nextIndex);

        currentFile.set(nextFile);
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();

        return true;
    }

    public void getPreviousImage() {
        updateFileList(currentFile.get());
        var index = files.indexOf(currentFile.get());
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        File nextFile = files.get(nextIndex);
        currentFile.set(nextFile);
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void getNextImage() {
        updateFileList(currentFile.get());
        var index = files.indexOf(currentFile.get());
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        File nextFile = files.get(nextIndex);
        currentFile.set(nextFile);
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void toggleMarkOnCurrentImage() {
        if (currentFile.get() != null) {
            if (markedImages.contains(currentFile.get())) {
                markedImages.remove(currentFile.get());
            } else {
                markedImages.add(currentFile.get());
            }
        }
    }
    
    public BooleanProperty isCurrentImageMarked() {
        return isCurrentImageMarked;
    }
    
    public String getMarkedImagePosition() {
        if (currentFile.get() == null || !markedImages.contains(currentFile.get())) {
            return "";
        }
        
        int position = markedImages.indexOf(currentFile.get()) + 1;
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
        
        int currentIndex = markedImages.indexOf(currentFile.get());
        int nextIndex = currentIndex > 0 ? currentIndex - 1 : markedImages.size() - 1;
        File nextFile = markedImages.get(nextIndex);
        currentFile.set(nextFile);
        boolean animate = shouldUseCrossFade();
        onImageChange.accept(nextFile, animate);
        onApplyAspectRatioSize.run();
    }
    
    public void navigateToNextMarked() {
        if (markedImages.isEmpty()) {
            return;
        }
        
        int currentIndex = markedImages.indexOf(currentFile.get());
        int nextIndex = currentIndex < markedImages.size() - 1 ? currentIndex + 1 : 0;
        File nextFile = markedImages.get(nextIndex);
        currentFile.set(nextFile);
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