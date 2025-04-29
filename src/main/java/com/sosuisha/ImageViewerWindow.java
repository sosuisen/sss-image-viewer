package com.sosuisha;

import com.sosuisha.jfxbuilder.SceneBuilder;
import com.sosuisha.jfxbuilder.ImageViewBuilder;
import com.sosuisha.jfxbuilder.BorderPaneBuilder;

import java.io.File;
import java.util.List;

import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {

    private double xOffset = 0;
    private double yOffset = 0;

    private List<File> files = null;
    private ImageView imageView = null;
    private Stage stage = null;
    private Scene scene = null;
    private File currentFile = null;

    public ImageViewerWindow(File file) {
        currentFile = file;

        stage = new Stage(StageStyle.UNDECORATED);

        imageView = ImageViewBuilder.create()
                .preserveRatio(true)
                .smooth(true)
                .build();

        scene = SceneBuilder.create()
                .root(BorderPaneBuilder.create()
                        .style("-fx-background-color: black")
                        .center(imageView)
                        .onScroll(event -> {
                            double delta = event.getDeltaY();
                            double scaleFactor = (delta > 0) ? 1.05 : 0.95;
                            stage.setWidth(stage.getWidth() * scaleFactor);
                            stage.setHeight(stage.getHeight() * scaleFactor);
                        })
                        .onMousePressed(event -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                        })
                        .onMouseDragged(event -> {
                            stage.setX(event.getScreenX() - xOffset);
                            stage.setY(event.getScreenY() - yOffset);
                        })
                        .build())
                .apply(myScene -> {
                    myScene.widthProperty().addListener((obs, oldVal, newVal) -> {

                        imageView.setFitWidth(newVal.doubleValue());
                    });
                    myScene.heightProperty().addListener((obs, oldVal, newVal) -> {
                        imageView.setFitHeight(newVal.doubleValue());
                    });
                })
                .onMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                })
                .onKeyPressed(event -> {
                    var keyCode = event.getCode();
                    switch (keyCode) {
                        case ESCAPE -> stage.close();
                        case F11 -> stage.setFullScreen(!stage.isFullScreen());
                        case LEFT -> {
                            getPreviousImage(currentFile);
                        }
                        case RIGHT -> {
                            getNextImage(currentFile);
                        }
                        default -> {
                        }
                    }
                })
                .build();

        setImage(currentFile);

        stage.setScene(scene);
        stage.setTitle("Image: " + currentFile.getName());
        stage.show();
    }

    private Image getImageFromFile(File file) {
        return new Image(file.toURI().toString());
    }

    private Dimension2D getWindowSizeFromImageSize() {
        var image = imageView.getImage();
        double maxDimension = 800;
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double scale = Math.min(maxDimension / imageWidth, maxDimension / imageHeight);
        double windowWidth = imageWidth * scale;
        double windowHeight = imageHeight * scale;
        return new Dimension2D(windowWidth, windowHeight);
    }

    private void resizeWindow(Dimension2D windowSize) {
        stage.setWidth(windowSize.getWidth());
        stage.setHeight(windowSize.getHeight());
    }

    private void setImage(File file) {
        currentFile = file;
        imageView.setImage(getImageFromFile(file));
        resizeWindow(getWindowSizeFromImageSize());
        stage.setTitle("Image: " + currentFile.getName());
    }

    private void updateFileList(File file) {
        if (files != null) {
            return;
        }
        File folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            files = List.of(folder.listFiles((dir, name) -> ImageUtil.isImageFile(name)));
        }
        else {
            files = List.of(file);
        }
    }

    private void getPreviousImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        setImage(files.get(nextIndex));
    }

    private void getNextImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        setImage(files.get(nextIndex));
    }
}