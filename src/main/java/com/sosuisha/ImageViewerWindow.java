package com.sosuisha;

import com.sosuisha.jfxbuilder.LabelBuilder;

import com.sosuisha.jfxbuilder.SceneBuilder;
import com.sosuisha.jfxbuilder.ImageViewBuilder;
import com.sosuisha.jfxbuilder.BorderPaneBuilder;

import java.io.File;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {
    private static int STATUS_HEIGHT = 20;

    private double xOffset = 0;
    private double yOffset = 0;

    private List<File> files = null;
    private ImageView imageView = null;
    private Stage stage = null;
    private Scene scene = null;
    private File currentFile = null;

    public ImageViewerWindow(File file, boolean withFrame) {
        this(file, withFrame, null, null);
    }

    public ImageViewerWindow(File file, boolean withFrame, Dimension2D position, Dimension2D imageAreaSize) {
        currentFile = file;

        stage = new Stage(withFrame ? StageStyle.DECORATED : StageStyle.UNDECORATED);

        imageView = ImageViewBuilder.create()
                .preserveRatio(true)
                .smooth(true)
                .build();

        setImage(currentFile);

        scene = SceneBuilder.create()
                .root(BorderPaneBuilder.create()
                        .style("-fx-background-color: black")
                        .center(imageView)
                        .bottom(
                                withFrame ? LabelBuilder.create()
                                        .text((int) imageView.getImage().getWidth() + " x "
                                                + (int) imageView.getImage().getHeight())
                                        .style("-fx-text-fill: white")
                                        .padding(new Insets(1, 5, 1, 5))
                                        .prefHeight(STATUS_HEIGHT)
                                        .minHeight(STATUS_HEIGHT)
                                        .maxHeight(STATUS_HEIGHT)
                                        .build()
                                        : null)
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
                        case ENTER -> {
                            new ImageViewerWindow(currentFile, !withFrame,
                                    new Dimension2D(stage.getX(), stage.getY()),
                                    new Dimension2D(imageView.getImage().getWidth(), imageView.getImage().getHeight()));
                            stage.close();
                        }
                        default -> {
                        }
                    }
                })
                .build();

        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty()
                .bind(scene.heightProperty().map(h -> h.doubleValue() - (withFrame ? STATUS_HEIGHT : 0)));

        Platform.runLater(() -> {
            if (imageAreaSize != null) {
                resizeWindow(getWindowSizeFromImageSize(imageAreaSize));
                stage.setX(position.getWidth());
                stage.setY(position.getHeight());
            } else {
                resizeWindow(getWindowSizeFromImageSize(
                    new Dimension2D(imageView.getImage().getWidth(), imageView.getImage().getHeight())));
                var bounds = Screen.getPrimary().getVisualBounds(); // exclude taskbar
                stage.setX(bounds.getWidth() / 2 - stage.getWidth() / 2);
                stage.setY(bounds.getHeight() / 2 - stage.getHeight() / 2);
            }
        });

        stage.setScene(scene);
        stage.setTitle(currentFile.getName());
        stage.show();
    }

    private Image getImageFromFile(File file) {
        return new Image(file.toURI().toString());
    }

    private Dimension2D getWindowSizeFromImageSize(Dimension2D imageSize) {
        var titleBarHeight = stage.getHeight() - scene.getHeight();

        double maxDimension = 800;
        double imageWidth = imageSize.getWidth();
        double imageHeight = imageSize.getHeight();
        double scale = Math.min(maxDimension / imageWidth, maxDimension / imageHeight);
        double windowWidth = imageWidth * scale;
        double windowHeight = imageHeight * scale +  titleBarHeight;
        return new Dimension2D(windowWidth, windowHeight);
    }

    private void resizeWindow(Dimension2D windowSize) {
        stage.setWidth(windowSize.getWidth());
        stage.setHeight(windowSize.getHeight());
    }

    private void setImage(File file) {
        currentFile = file;
        imageView.setImage(getImageFromFile(file));
        stage.setTitle("Image: " + currentFile.getName());
    }

    private void updateFileList(File file) {
        if (files != null) {
            return;
        }
        var folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            files = List.of(folder.listFiles((dir, name) -> ImageUtil.isImageFile(name)));
        } else {
            files = List.of(file);
        }
    }

    private void getPreviousImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        setImage(files.get(nextIndex));
        resizeWindow(getWindowSizeFromImageSize(new Dimension2D(imageView.getImage().getWidth(), imageView.getImage().getHeight())));
    }

    private void getNextImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        setImage(files.get(nextIndex));
        resizeWindow(getWindowSizeFromImageSize(new Dimension2D(imageView.getImage().getWidth(), imageView.getImage().getHeight())));
    }
}