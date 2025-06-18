package com.sosuisha.imageviewer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sosuisha.imageviewer.jfxbuilder.BorderPaneBuilder;
import com.sosuisha.imageviewer.jfxbuilder.ImageViewBuilder;
import com.sosuisha.imageviewer.jfxbuilder.LabelBuilder;
import com.sosuisha.imageviewer.jfxbuilder.SceneBuilder;
import com.sosuisha.imageviewer.jfxbuilder.TextInputDialogBuilder;
import javafx.scene.layout.StackPane;

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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
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
    private static final int CROSSFADE_DURATION_MILLIS = 300;

    private enum AspectRatio {
        LANDSCAPE, PORTRAIT
    }

    private static final Map<AspectRatio, Dimension2D> aspectRatioSizes = new ConcurrentHashMap<>();

    private double xOffset = 0;
    private double yOffset = 0;

    private boolean withFrame = true;

    private List<File> files = null;
    private ImageView imageView = null;
    private ImageView imageView2 = null;
    private Map<File, Image> imageCache = new ConcurrentHashMap<>();
    private ObservableList<File> markedImages = FXCollections.observableArrayList();
    private Stage stage = null;
    private Scene scene = null;
    private ObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private DoubleProperty orgImageWidth = new SimpleDoubleProperty(0);
    private DoubleProperty orgImageHeight = new SimpleDoubleProperty(0);
    private DoubleProperty currentScale = new SimpleDoubleProperty(1.0);
    private DoubleProperty currentFullScreenScale = new SimpleDoubleProperty(1.0);
    private DoubleProperty imageTranslateX = new SimpleDoubleProperty(0.0);
    private DoubleProperty imageTranslateY = new SimpleDoubleProperty(0.0);
    private DoubleProperty imageRotation = new SimpleDoubleProperty(0.0);
    // Manual full-screen state management: stage.isFullScreen() is not correctly captured
    // when we need to judge it in ESCAPE key press event handler
    private BooleanProperty isFullScreen = new SimpleBooleanProperty(false);
    private BooleanProperty mousePressed = new SimpleBooleanProperty(false);
    private BooleanProperty slideshowMode = new SimpleBooleanProperty(false);
    private Timeline slideshowTimer = null;
    private double slideshowInterval = 0.0;
    private ParallelTransition currentAnimation = null;
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
        
        imageView2 = ImageViewBuilder.create()
                .preserveRatio(true)
                .smooth(true)
                .opacity(0.0)
                .build();
        

        setImage(currentFile.get(), false);

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
        var imageStack = new StackPane();
        imageStack.getChildren().addAll(imageView, imageView2);

        return SceneBuilder.create()
                .root(BorderPaneBuilder.create()
                        .style("-fx-background-color: black")
                        .center(imageStack)
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
                        toggleFullScreen();
                    }
                })
                .build();
    }

    private void setupWindowSizeBinding() {
        // Create bindings that account for rotation
        var widthBinding = Bindings.createDoubleBinding(() -> {
            double rotation = Math.abs(imageRotation.get() % 360);
            if (rotation == 90 || rotation == 270) {
                return scene.getHeight() - (withFrame ? STATUS_HEIGHT : 0);
            }
            return scene.getWidth();
        }, scene.widthProperty(), scene.heightProperty(), imageRotation);
        
        var heightBinding = Bindings.createDoubleBinding(() -> {
            double rotation = Math.abs(imageRotation.get() % 360);
            if (rotation == 90 || rotation == 270) {
                return scene.getWidth();
            }
            return scene.getHeight() - (withFrame ? STATUS_HEIGHT : 0);
        }, scene.widthProperty(), scene.heightProperty(), imageRotation);

        imageView.fitWidthProperty().bind(widthBinding);
        imageView.fitHeightProperty().bind(heightBinding);

        imageView2.fitWidthProperty().bind(widthBinding);
        imageView2.fitHeightProperty().bind(heightBinding);
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
                String slideshowPrefix = slideshowMode.get() ? "[SLIDESHOW] " : "";
                String baseText = slideshowPrefix + markPrefix + (int) orgImageWidth.get() + " x "
                        + (int) orgImageHeight.get();
                return mousePressed.get()
                        ? baseText
                                + " | 'S': slideshow, 'Space': mark/unmark, 'D': duplicate, 'Enter': noframe, 'Esc': close, 'DblClick': maximize"
                        : baseText;
            }, orgImageWidth, orgImageHeight, mousePressed, currentFile, markedImages, slideshowMode));
        }
    }

    private void initializeWindowSizeAndPosition(Point2D position, Double initialScale) {
        // It seems that GraalVM Native Image does not support subscribe method.
        // currentScale.subscribe(scale -> setWindowSizeFromScale(scale.doubleValue()));
        currentScale.addListener((_, _, newValue) -> {
            setWindowSizeFromScale(newValue.doubleValue());
        });
        
        currentFullScreenScale.addListener((_, _, newValue) -> {
            setImageScaleInFullScreen(newValue.doubleValue());
        });
        
        imageTranslateX.addListener((_, _, newValue) -> {
            Platform.runLater(() -> {
                imageView.setTranslateX(newValue.doubleValue());
                imageView2.setTranslateX(newValue.doubleValue());
            });
        });
        
        imageTranslateY.addListener((_, _, newValue) -> {
            Platform.runLater(() -> {
                imageView.setTranslateY(newValue.doubleValue());
                imageView2.setTranslateY(newValue.doubleValue());
            });
        });
        
        imageRotation.addListener((_, _, newValue) -> {
            Platform.runLater(() -> {
                imageView.setRotate(newValue.doubleValue());
                imageView2.setRotate(newValue.doubleValue());
                
                // Calculate proper positioning when not in full-screen to center the rotated image
                if (!isFullScreen.get()) {
                    double rotation = Math.abs(newValue.doubleValue() % 360);
                    
                    if (rotation == 0 || rotation == 180) {
                        // No rotation or 180° rotation - use default centering
                        imageTranslateX.set(0.0);
                        imageTranslateY.set(0.0);
                    } else {
                        // 90° or 270° rotation - adjust only the dimension that needs it
                        boolean isOriginalLandscape = orgImageWidth.get() >= orgImageHeight.get();
                        
                        if (isOriginalLandscape) {
                            // Original is landscape - only adjust X translation
                            Dimension2D effectiveDimensions = getEffectiveImageDimensions();
                            double effectiveWidth = effectiveDimensions.getWidth() * currentScale.get();
                            double windowWidth = scene.getWidth();
                            double xOffset = (windowWidth - effectiveWidth) / 2;
                            imageTranslateX.set(-xOffset);
                            imageTranslateY.set(0.0);
                        } else {
                            // Original is portrait - only adjust Y translation
                            Dimension2D effectiveDimensions = getEffectiveImageDimensions();
                            double effectiveHeight = effectiveDimensions.getHeight() * currentScale.get();
                            double windowHeight = scene.getHeight() - (withFrame ? STATUS_HEIGHT : 0);
                            double yOffset = (windowHeight - effectiveHeight) / 2;
                            imageTranslateX.set(0.0);
                            imageTranslateY.set(-yOffset);
                        }
                    }
                    setWindowSizeFromScale(currentScale.get());
                }
            });
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
            if (isFullScreen.get()) {
                double deltaX = event.getSceneX() - xOffset;
                double deltaY = event.getSceneY() - yOffset;
                imageTranslateX.set(imageTranslateX.get() + deltaX);
                imageTranslateY.set(imageTranslateY.get() + deltaY);
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            } else {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        scene.setOnMouseReleased(_ -> {
            mousePressed.set(false);
        });
    }

    private void handleScroll(javafx.scene.input.ScrollEvent event) {
        double delta = event.getDeltaY();
        double scaleFactor = (delta > 0) ? 1.05 : 0.95;
        
        if (isFullScreen.get()) {
            double newScale = currentFullScreenScale.get() * scaleFactor;
            currentFullScreenScale.set(newScale);
        } else {
            double newScale = currentScale.get() * scaleFactor;
            currentScale.set(newScale);
        }
    }

    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        var keyCode = event.getCode();
        switch (keyCode) {
            case ESCAPE -> {
                if (isFullScreen.get()) {
                    isFullScreen.set(false);
                    currentFullScreenScale.set(1.0);
                    imageTranslateX.set(0.0);
                    imageTranslateY.set(0.0);
                    imageRotation.set(0.0);
                } else {
                    stage.close();
                }
            }
            case F11 -> toggleFullScreen();
            case LEFT -> {
                if (slideshowMode.get()) {
                    getPreviousMarkedImage();
                } else {
                    getPreviousImage(currentFile.get());
                }
            }
            case RIGHT -> {
                if (slideshowMode.get()) {
                    getNextMarkedImage();
                } else {
                    getNextImage(currentFile.get());
                }
            }
            case UP -> {
                double currentRotation = imageRotation.get();
                imageRotation.set(currentRotation - 90);
            }
            case DOWN -> {
                double currentRotation = imageRotation.get();
                imageRotation.set(currentRotation + 90);
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
            case S -> {
                toggleSlideshow();
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
        Dimension2D effectiveDimensions = getEffectiveImageDimensions();
        return effectiveDimensions.getWidth() >= effectiveDimensions.getHeight() ? AspectRatio.LANDSCAPE : AspectRatio.PORTRAIT;
    }

    private Dimension2D getEffectiveImageDimensions() {
        double rotation = Math.abs(imageRotation.get() % 360);
        if (rotation == 90 || rotation == 270) {
            return new Dimension2D(orgImageHeight.get(), orgImageWidth.get());
        }
        return new Dimension2D(orgImageWidth.get(), orgImageHeight.get());
    }

    private Dimension2D getWindowSize() {
        Dimension2D effectiveDimensions = getEffectiveImageDimensions();
        return new Dimension2D(effectiveDimensions.getWidth() * currentScale.get() + getFrameBorderWidth(),
                effectiveDimensions.getHeight() * currentScale.get() + getTitleBarHeight()
                        + (withFrame ? STATUS_HEIGHT : 0));
    }

    private void setWindowSizeFromScale(double scale) {
        var windowSize = getWindowSize();
        Dimension2D effectiveDimensions = getEffectiveImageDimensions();

        AspectRatio aspectRatio = getAspectRatio();
        aspectRatioSizes.put(aspectRatio,
                new Dimension2D(effectiveDimensions.getWidth() * scale, effectiveDimensions.getHeight() * scale));

        Platform.runLater(() -> {
            stage.setWidth(windowSize.getWidth());
            stage.setHeight(windowSize.getHeight());
        });
    }

    private void setImageScaleInFullScreen(double scale) {
        // In full-screen mode, scale the image using transforms to keep it centered
        Platform.runLater(() -> {
            imageView.setScaleX(scale);
            imageView.setScaleY(scale);
            imageView.setTranslateX(imageTranslateX.get());
            imageView.setTranslateY(imageTranslateY.get());
            imageView2.setScaleX(scale);
            imageView2.setScaleY(scale);
            imageView2.setTranslateX(imageTranslateX.get());
            imageView2.setTranslateY(imageTranslateY.get());
        });
    }

    private void toggleFullScreen() {
        boolean wasFullScreen = isFullScreen.get();
        stage.setFullScreen(!wasFullScreen);
        isFullScreen.set(!wasFullScreen);
        currentFullScreenScale.set(1.0);
        imageTranslateX.set(0.0);
        imageTranslateY.set(0.0);
        imageRotation.set(0.0);
    }

    private double calcScaleFromMaxDimension(double maxDimension) {
        Dimension2D effectiveDimensions = getEffectiveImageDimensions();
        if (effectiveDimensions.getWidth() <= 0 || effectiveDimensions.getHeight() <= 0) {
            return 1; // Default scale if dimensions are invalid
        }
        return maxDimension > 0 ? Math.min(maxDimension / effectiveDimensions.getWidth(), maxDimension / effectiveDimensions.getHeight()) : 1;
    }

    private void setImage(File file) {
        setImage(file, true);
    }

    private void setImage(File file, boolean animate) {
        // Cancel any ongoing animation
        cancelCurrentAnimation();

        currentFile.set(file);
        var image = getImageFromFile(file);
        orgImageWidth.set(image.getWidth());
        orgImageHeight.set(image.getHeight());
        imageRotation.set(0.0);

        if (animate && imageView.getImage() != null && shouldUseCrossFade()) {
            crossFadeToImage(image);
        } else {
            imageView.setImage(image);
        }
    }

    private boolean shouldUseCrossFade() {
        // Don't use cross-fade if slideshow is active and interval is too short
        if (slideshowMode.get() && slideshowInterval > 0 && slideshowInterval * 1000 <= CROSSFADE_DURATION_MILLIS) {
            return false;
        }
        return true;
    }

    private void cancelCurrentAnimation() {
        if (currentAnimation != null && currentAnimation.getStatus() == ParallelTransition.Status.RUNNING) {
            currentAnimation.stop();
            // Reset opacity states
            imageView.setImage(null);
            imageView.setOpacity(1.0);
            imageView2.setOpacity(0.0);
            imageView2.setImage(null);
            currentAnimation = null;
        }
    }

    private void crossFadeToImage(Image newImage) {
        // Set new image to the second ImageView
        imageView2.setImage(newImage);

        // Create fade out animation for current image
        FadeTransition fadeOut = new FadeTransition(Duration.millis(CROSSFADE_DURATION_MILLIS), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.7);

        // Create fade in animation for new image
        FadeTransition fadeIn = new FadeTransition(Duration.millis(CROSSFADE_DURATION_MILLIS), imageView2);
        fadeIn.setFromValue(0.7);
        fadeIn.setToValue(1.0);

        // Create parallel transition
        currentAnimation = new ParallelTransition(fadeOut, fadeIn);

        currentAnimation.setOnFinished(event -> {
            // Swap the images and reset opacity
            imageView.setImage(newImage);
            imageView.setOpacity(1.0);
            imageView2.setImage(null);
            imageView2.setOpacity(0.0);
            currentAnimation = null;
        });

        currentAnimation.play();
    }

    private void updateFileList(File file) {
        if (files != null) {
            return;
        }
        var folder = file.getParentFile();
        if (folder != null && folder.isDirectory()) {
            var imageFiles = folder.listFiles((_, name) -> ImageUtil.isImageFile(name));
            Arrays.sort(imageFiles);
            files = List.of(imageFiles);
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

    private void toggleSlideshow() {
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
                    slideshowTimer = new Timeline(new KeyFrame(Duration.seconds(seconds), _ -> getNextMarkedImage()));
                    slideshowTimer.setCycleCount(Timeline.INDEFINITE);
                    slideshowTimer.play();
                }

                if (!markedImages.contains(currentFile.get())) {
                    if (!markedImages.isEmpty()) {
                        setImage(markedImages.get(0));
                        applyAspectRatioSize();
                    }
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

    private void getPreviousMarkedImage() {
        if (markedImages.isEmpty()) {
            return;
        }

        File current = currentFile.get();
        int currentIndex = markedImages.indexOf(current);
        int nextIndex = currentIndex > 0 ? currentIndex - 1 : markedImages.size() - 1;
        setImage(markedImages.get(nextIndex));
        applyAspectRatioSize();
    }

    private void getNextMarkedImage() {
        if (markedImages.isEmpty()) {
            return;
        }

        File current = currentFile.get();
        int currentIndex = markedImages.indexOf(current);
        int nextIndex = currentIndex < markedImages.size() - 1 ? currentIndex + 1 : 0;
        setImage(markedImages.get(nextIndex));
        applyAspectRatioSize();
    }
}