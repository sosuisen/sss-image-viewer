package com.sosuisha.imageviewer;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sosuisha.imageviewer.jfxbuilder.BorderPaneBuilder;
import com.sosuisha.imageviewer.jfxbuilder.ImageViewBuilder;
import com.sosuisha.imageviewer.jfxbuilder.LabelBuilder;
import com.sosuisha.imageviewer.jfxbuilder.SceneBuilder;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {
    private static final int STATUS_HEIGHT = 20;
    private static final double MAX_DIMENSION = 800.0; // デフォルトの最大サイズ

    private enum AspectRatio {
        LANDSCAPE, PORTRAIT
    }

    private static final Map<AspectRatio, Dimension2D> aspectRatioSizes = new ConcurrentHashMap<>();

    private double xOffset = 0;
    private double yOffset = 0;

    private boolean withFrame = true;

    private List<File> files = null;
    private ImageView imageView = null;
    private Map<File, Image> imageCache = new ConcurrentHashMap<>();
    private ObservableList<File> markedImages = FXCollections.observableArrayList();
    private Stage stage = null;
    private Scene scene = null;
    private ObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private DoubleProperty orgImageWidth = new SimpleDoubleProperty(0);
    private DoubleProperty orgImageHeight = new SimpleDoubleProperty(0);
    private DoubleProperty currentScale = new SimpleDoubleProperty(1.0);
    private BooleanProperty mousePressed = new SimpleBooleanProperty(false);
    private Label statusLabel = null;

    public ImageViewerWindow(File file, boolean withFrame) {
        this(file, withFrame, null, null);
    }

    /**
     * Constructs an ImageViewerWindow instance.
     *
     * @param file         The image file to be displayed.
     * @param withFrame    Whether the window should have a frame (decorated or
     *                     undecorated).
     * @param position     The initial position of the window on the screen (can be
     *                     null).
     * @param initialScale The initial scale of the image (can be null).
     */
    public ImageViewerWindow(File file, boolean withFrame, Point2D position, Double initialScale) {
        currentFile.set(file);
        this.withFrame = withFrame;

        stage = new Stage(withFrame ? StageStyle.DECORATED : StageStyle.UNDECORATED);

        imageView = ImageViewBuilder.create()
                .preserveRatio(true)
                .smooth(true)
                .build();

        setImage(currentFile.get());

        scene = buildScene();

        setupWindowSizeBinding();
        setupTitleBinding();
        setupStatusLabelBinding();

        stage.setWidth(1);
        stage.setHeight(1);
        Platform.runLater(() -> initializeWindowSizeAndPosition(position, initialScale));
        stage.setScene(scene);
        stage.show();
    }

    private Scene buildScene() {
        return SceneBuilder.create()
                .root(BorderPaneBuilder.create()
                        .style("-fx-background-color: black")
                        .center(imageView)
                        .bottom(
                                withFrame ? (statusLabel = LabelBuilder.create()
                                        .text(imageView.getImage() != null
                                                ? (int) imageView.getImage().getWidth() + " x "
                                                        + (int) imageView.getImage().getHeight()
                                                : "No Image")
                                        .style("-fx-text-fill: white")
                                        .padding(new Insets(1, 5, 1, 5))
                                        .prefHeight(STATUS_HEIGHT)
                                        .minHeight(STATUS_HEIGHT)
                                        .maxHeight(STATUS_HEIGHT)
                                        .build())
                                        : null)
                        .build())
                .apply(this::dragWindow)
                .onScroll(this::handleScroll)
                .onKeyPressed(this::handleKeyPressed)
                .onMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                })
                .build();
    }

    private void setupWindowSizeBinding() {
        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty()
                .bind(scene.heightProperty().map(h -> h.doubleValue() - (withFrame ? STATUS_HEIGHT : 0)));
    }

    private void setupTitleBinding() {
        stage.titleProperty()
                .bind(Bindings.createStringBinding(
                        () -> currentFile.get() != null ? currentFile.get().getName() : "No Image",
                        currentFile));
    }

    private void setupStatusLabelBinding() {
        if (statusLabel != null) {
            statusLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                String markPrefix = "";
                if (isCurrentImageMarked()) {
                    String pos = getMarkedImagePosition();
                    markPrefix = pos.isEmpty() ? "" : "#" + pos + " ";
                }
                String baseText = markPrefix + (int) orgImageWidth.get() + " x " + (int) orgImageHeight.get();
                return mousePressed.get()
                        ? baseText
                                + " | 'Space': mark/unmark, 'D': duplicate, 'Enter': noframe, 'Esc': close, 'DblClick': maximize"
                        : baseText;
            }, orgImageWidth, orgImageHeight, mousePressed, currentFile, markedImages));
        }
    }

    private void initializeWindowSizeAndPosition(Point2D position, Double initialScale) {
        // It seems that GraalVM Native Image does not support subscribe method.
        // currentScale.subscribe(scale -> setWindowSizeFromScale(scale.doubleValue()));
        currentScale.addListener((_, _, newValue) -> {
            setWindowSizeFromScale(newValue.doubleValue());
        });
        if (initialScale != null) {
            currentScale.set(initialScale);
            stage.setX(position.getX());
            stage.setY(position.getY());
        } else {
            currentScale.set(calcScaleFromMaxDimension(MAX_DIMENSION));
            var windowSize = getWindowSize();
            var bounds = Screen.getPrimary().getVisualBounds(); // exclude taskbar
            stage.setX(bounds.getWidth() / 2 - windowSize.getWidth() / 2);
            stage.setY(bounds.getHeight() / 2 - windowSize.getHeight() / 2);
        }
    }

    private void dragWindow(Scene scene) {
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY() + (withFrame ? getTitleBarHeight() : 0);
            mousePressed.set(true);
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        scene.setOnMouseReleased(_ -> {
            mousePressed.set(false);
        });
    }

    private void handleScroll(javafx.scene.input.ScrollEvent event) {
        double delta = event.getDeltaY();
        double scaleFactor = (delta > 0) ? 1.05 : 0.95;
        double newScale = currentScale.get() * scaleFactor;
        currentScale.set(newScale);
    }

    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        var keyCode = event.getCode();
        switch (keyCode) {
            case ESCAPE -> stage.close();
            case F11 -> stage.setFullScreen(!stage.isFullScreen());
            case LEFT -> {
                getPreviousImage(currentFile.get());
            }
            case RIGHT -> {
                getNextImage(currentFile.get());
            }
            case ENTER -> {
                new ImageViewerWindow(currentFile.get(), !withFrame,
                        new Point2D(stage.getX(), stage.getY()),
                        currentScale.get());
                stage.close();
            }
            case D -> {
                new ImageViewerWindow(currentFile.get(), withFrame,
                        new Point2D(stage.getX() + 30, stage.getY() + 30),
                        currentScale.get());
            }
            case SPACE -> {
                toggleMarkOnCurrentImage();
            }
            default -> {
            }
        }
    }

    private Image getImageFromFile(File file) {
        return imageCache.computeIfAbsent(file, f -> new Image(f.toURI().toString()));
    }

    private double getFrameBorderWidth() {
        return (stage.getWidth() - scene.getWidth()) / 2;
    }

    private double getTitleBarHeight() {
        return stage.getHeight() - scene.getHeight() - getFrameBorderWidth();
    }

    private AspectRatio getAspectRatio() {
        return orgImageWidth.get() >= orgImageHeight.get() ? AspectRatio.LANDSCAPE : AspectRatio.PORTRAIT;
    }

    private Dimension2D getWindowSize() {
        return new Dimension2D(orgImageWidth.get() * currentScale.get() + getFrameBorderWidth(),
                orgImageHeight.get() * currentScale.get() + getTitleBarHeight()
                        + (withFrame ? STATUS_HEIGHT : 0));
    }

    private void setWindowSizeFromScale(double scale) {
        var windowSize = getWindowSize();

        AspectRatio aspectRatio = getAspectRatio();
        aspectRatioSizes.put(aspectRatio,
                new Dimension2D(orgImageWidth.get() * scale, orgImageHeight.get() * scale));

        Platform.runLater(() -> {
            stage.setWidth(windowSize.getWidth());
            stage.setHeight(windowSize.getHeight());
        });
    }

    private double calcScaleFromMaxDimension(double maxDimension) {
        if (orgImageWidth.get() <= 0 || orgImageHeight.get() <= 0) {
            return 1; // Default scale if dimensions are invalid
        }
        return maxDimension > 0 ? Math.min(maxDimension / orgImageWidth.get(), maxDimension / orgImageHeight.get()) : 1;
    }

    private void setImage(File file) {
        currentFile.set(file);
        var image = getImageFromFile(file);
        orgImageWidth.set(image.getWidth());
        orgImageHeight.set(image.getHeight());
        imageView.setImage(image);
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

    private void applyAspectRatioSize() {
        AspectRatio aspectRatio = getAspectRatio();
        Dimension2D savedSize = aspectRatioSizes.get(aspectRatio);
        double maxDimension = savedSize != null ? Math.max(savedSize.getWidth(), savedSize.getHeight()) : MAX_DIMENSION;
        currentScale.set(calcScaleFromMaxDimension(maxDimension));
    }

    private void getPreviousImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index > 0 ? index - 1 : files.size() - 1;
        setImage(files.get(nextIndex));
        applyAspectRatioSize();
    }

    private void getNextImage(File file) {
        updateFileList(file);
        var index = files.indexOf(file);
        var nextIndex = index < files.size() - 1 ? index + 1 : 0;
        setImage(files.get(nextIndex));
        applyAspectRatioSize();
    }

    private void toggleMarkOnCurrentImage() {
        File current = currentFile.get();
        if (current != null) {
            if (markedImages.contains(current)) {
                markedImages.remove(current);
            } else {
                markedImages.add(current);
            }
        }
    }

    private boolean isCurrentImageMarked() {
        File current = currentFile.get();
        return current != null && markedImages.contains(current);
    }

    private String getMarkedImagePosition() {
        File current = currentFile.get();
        if (current == null || !markedImages.contains(current)) {
            return "";
        }

        int position = markedImages.indexOf(current) + 1;
        return position + "/" + markedImages.size();
    }
}