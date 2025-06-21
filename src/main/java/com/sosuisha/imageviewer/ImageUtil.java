package com.sosuisha.imageviewer;

import java.io.File;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtil {
    public static String[] availableImageFormats = {
        "gif",
        "bmp",
        "png",
        "jpeg", "jpg", "jfif"
    };
    
    public static boolean isImageFile(File file) {
        return isImageFile(file.getName());
    }

    public static boolean isImageFile(String fileName) {
        var lowerCaseName = fileName.toLowerCase();
        return Arrays.stream(availableImageFormats).anyMatch(lowerCaseName::endsWith);
    }

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

}
