package com.sosuisha.imageviewer;

import java.io.File;
import java.util.List;

import com.sosuisha.imageviewer.jfxbuilder.BorderPaneBuilder;
import com.sosuisha.imageviewer.jfxbuilder.ImageViewBuilder;
import com.sosuisha.imageviewer.jfxbuilder.LabelBuilder;
import com.sosuisha.imageviewer.jfxbuilder.SceneBuilder;

import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {
    private static int STATUS_HEIGHT = 20;
    private static final double MAX_DIMENSION = 800.0; // デフォルトの最大サイズ

    private double xOffset = 0;
    private double yOffset = 0;

    private boolean withFrame = true;

    private List<File> files = null;
    private ImageView imageView = null;
    private Stage stage = null;
    private Scene scene = null;
    private File currentFile = null;
    private double orgImageWidth = 0;
    private double orgImageHeight = 0;
    private double currentScale = 1.0;

    public ImageViewerWindow(File file, boolean withFrame) {
        this(file, withFrame, null, null);
    }

    public ImageViewerWindow(File file, boolean withFrame, Point2D position, Double initialScale) {
        currentFile = file;
        this.withFrame = withFrame;

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
                            double newScale = currentScale * scaleFactor;
                            setWindowSizeFromScale(newScale);
                        })
                        .onMousePressed(event -> {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY() + (withFrame ? getTitleBarHeight() : 0);
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
                                    new Point2D(stage.getX(), stage.getY()),
                                    currentScale);
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
            if (initialScale != null) {
                setWindowSizeFromScale(initialScale);
                stage.setX(position.getX());
                stage.setY(position.getY());
            } else {
                setWindowSizeFromScale(calcScaleFromMaxDimension(MAX_DIMENSION));
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

    private double getFrameBorderWidth() {
        return (stage.getWidth() - scene.getWidth()) / 2;
    }

    private double getTitleBarHeight() {
        return stage.getHeight() - scene.getHeight() - getFrameBorderWidth();
    }

    private void setWindowSizeFromScale(double scale) {
        currentScale = scale;
        double windowWidth = orgImageWidth * currentScale + getFrameBorderWidth();
        double windowHeight = orgImageHeight * currentScale + getTitleBarHeight() + (withFrame ? STATUS_HEIGHT : 0);
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
    }

    private double calcScaleFromMaxDimension(double maxDimension) {
        return maxDimension > 0 ? Math.min(maxDimension / orgImageWidth, maxDimension / orgImageHeight) : 1;
    }

    private void setImage(File file) {
        currentFile = file;
        var image = getImageFromFile(file);
        orgImageWidth = image.getWidth();
        orgImageHeight = image.getHeight();
        imageView.setImage(image);
        stage.setTitle("Image: " + currentFile.getName());
    }

    private void updateFileList(File file) {
        if (files != null) {
            return;
        }
        var folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            files = List.of(folder.listFiles((_, name) -> ImageUtil.isImageFile(name)));
        } else {
            files = List.of(file);
        }
    }

    private void getPreviousImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        setImage(files.get(nextIndex));
        setWindowSizeFromScale(calcScaleFromMaxDimension(MAX_DIMENSION));
    }

    private void getNextImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        setImage(files.get(nextIndex));
        setWindowSizeFromScale(calcScaleFromMaxDimension(MAX_DIMENSION));
    }
}