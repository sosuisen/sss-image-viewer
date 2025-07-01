package com.sosuisha.imageviewer.view;

import java.io.File;

import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.view.jfxbuilder.BorderPaneBuilder;
import com.sosuisha.imageviewer.view.jfxbuilder.ImageViewBuilder;
import com.sosuisha.imageviewer.view.jfxbuilder.LabelBuilder;
import com.sosuisha.imageviewer.view.jfxbuilder.SceneBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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


    private double xOffset = 0;
    private double yOffset = 0;

    private boolean withFrame = true;

    private ImageView imageView = null;
    private ImageView imageView2 = null;
    private ImageNavigator imageNavigator;
    private Stage stage = null;
    private Scene scene = null;
    private DoubleProperty orgImageWidth = new SimpleDoubleProperty(0);
    private DoubleProperty orgImageHeight = new SimpleDoubleProperty(0);
    private DoubleProperty currentScale = new SimpleDoubleProperty(0);
    private DoubleProperty currentFullScreenScale = new SimpleDoubleProperty(1.0);
    private DoubleProperty imageTranslateX = new SimpleDoubleProperty(0.0);
    private DoubleProperty imageTranslateY = new SimpleDoubleProperty(0.0);
    private DoubleProperty imageRotation = new SimpleDoubleProperty(0.0);
    // Manual full-screen state management: stage.isFullScreen() is not correctly
    // captured
    // when we need to judge it in ESCAPE key press event handler
    private BooleanProperty isFullScreen = new SimpleBooleanProperty(false);
    private BooleanProperty mousePressed = new SimpleBooleanProperty(false);
    private BooleanProperty helpMode = new SimpleBooleanProperty(false);
    private ParallelTransition currentAnimation = null;
    private Label statusLabel = null;
    
    // Store listener references for cleanup
    private javafx.beans.value.ChangeListener<Number> currentScaleListener;
    private javafx.beans.value.ChangeListener<Number> currentFullScreenScaleListener;
    private javafx.beans.value.ChangeListener<Number> imageTranslateXListener;
    private javafx.beans.value.ChangeListener<Number> imageTranslateYListener;
    private javafx.beans.value.ChangeListener<Number> imageRotationListener;

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
        this.withFrame = withFrame;

        // Initialize image navigator with callback to change images
        imageNavigator = new ImageNavigator(this::setImage, this::applyAspectRatioSize);
        imageNavigator.setCurrentFile(file);

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

        setImage(file, false);

        scene = buildScene();

        setupWindowSizeBinding();
        setupTitleBinding();
        setupStatusLabelBinding();

        stage.setWidth(100);
        stage.setHeight(100);
        Platform.runLater(() -> initializeWindowSizeAndPosition(position, initialScale));
        stage.setScene(scene);
        
        // Add cleanup when window is closed
        stage.setOnCloseRequest(e -> cleanup());
        
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
        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty()
                .bind(scene.heightProperty().map(h -> h.doubleValue() - (withFrame ? STATUS_HEIGHT : 0)));

        imageView2.fitWidthProperty().bind(scene.widthProperty());
        imageView2.fitHeightProperty()
                .bind(scene.heightProperty().map(h -> h.doubleValue() - (withFrame ? STATUS_HEIGHT : 0)));
    }

    private void setupTitleBinding() {
        stage.titleProperty()
                .bind(Bindings.createStringBinding(
                        () -> imageNavigator.getCurrentFile() != null ? imageNavigator.getCurrentFile().getName()
                                : "No Image", imageNavigator.getCurrentFileProperty()));
    }

    private void setupStatusLabelBinding() {
        if (statusLabel != null) {
            statusLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                var markPrefix = "";
                if (imageNavigator.isCurrentImageMarked().get()) {
                    var pos = imageNavigator.getMarkedImagePosition();
                    markPrefix = pos.isEmpty() ? "" : "#" + pos + " ";
                }
                var slideshowPrefix = imageNavigator.slideshowModeProperty().get() ? "[SLIDESHOW] " : "";
                var baseText = slideshowPrefix + markPrefix + (int) orgImageWidth.get() + " x "
                        + (int) orgImageHeight.get();
                var canStartSlideShow = imageNavigator.getCanStartSlideShow() ? "'S': slideshow, " : "";

                if (mousePressed.get() || helpMode.get()) {
                    return baseText + " | " + canStartSlideShow + "'Space': mark/unmark, 'D': duplicate, 'Enter': noframe, 'Esc': close, 'DblClick': maximize";
                }
                else if (imageNavigator.slideshowModeProperty().get()) {
                    return baseText + " | 'S': end";
                }
                return baseText + " | 'H': help"; 
            }, orgImageWidth, orgImageHeight, mousePressed, imageNavigator.getMarkedImages(),
                    imageNavigator.slideshowModeProperty(), imageNavigator.isCurrentImageMarked(), helpMode));
        }
    }

    private void initializeWindowSizeAndPosition(Point2D position, Double initialScale) {
        // It seems that GraalVM Native Image does not support subscribe method.
        // currentScale.subscribe(scale -> setWindowSizeFromScale(scale.doubleValue()));
        // Store listeners for cleanup
        currentScaleListener = (_, _, newValue) -> {
            setWindowSizeFromScale(newValue.doubleValue());
        };
        currentScale.addListener(currentScaleListener);

        currentFullScreenScaleListener = (_, _, newValue) -> {
            setImageScaleInFullScreen(newValue.doubleValue());
        };
        currentFullScreenScale.addListener(currentFullScreenScaleListener);

        imageTranslateXListener = (_, _, newValue) -> {
            Platform.runLater(() -> {
                imageView.setTranslateX(newValue.doubleValue());
                imageView2.setTranslateX(newValue.doubleValue());
            });
        };
        imageTranslateX.addListener(imageTranslateXListener);

        imageTranslateYListener = (_, _, newValue) -> {
            Platform.runLater(() -> {
                imageView.setTranslateY(newValue.doubleValue());
                imageView2.setTranslateY(newValue.doubleValue());
            });
        };
        imageTranslateY.addListener(imageTranslateYListener);

        imageRotationListener = (_, _, newValue) -> {
            // Save rotation to memory for current image
            ImageService.getInstance().setRotationForFile(imageNavigator.getCurrentFile(), newValue.doubleValue());

            if (imageNavigator.getCurrentFile() != null) {
                Image originalImage = ImageService.getInstance().getImageFromFile(imageNavigator.getCurrentFile());
                Image rotatedImage = ImageService.createRotatedImage(originalImage, newValue.doubleValue());
                imageView.setImage(rotatedImage);
                imageView2.setImage(rotatedImage);

                // Update dimensions based on rotated image
                orgImageWidth.set(rotatedImage.getWidth());
                orgImageHeight.set(rotatedImage.getHeight());
            }
            Platform.runLater(() -> {
                setWindowSizeFromScale(currentScale.get());
            });
        };
        imageRotation.addListener(imageRotationListener);
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
                    cleanup();
                    stage.close();
                }
            }
            case F11 -> toggleFullScreen();
            case LEFT -> {
                if (imageNavigator.slideshowModeProperty().get()) {
                    imageNavigator.navigateToPreviousMarked();
                } else {
                    imageNavigator.getPreviousImage();
                }
            }
            case RIGHT -> {
                if (imageNavigator.slideshowModeProperty().get()) {
                    imageNavigator.navigateToNextMarked();
                } else {
                    imageNavigator.getNextImage();
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
                new ImageViewerWindow(imageNavigator.getCurrentFile(), !withFrame,
                        new Point2D(stage.getX(), stage.getY()),
                        currentScale.get());
                cleanup();
                stage.close();
            }
            case D -> {
                new ImageViewerWindow(imageNavigator.getCurrentFile(), withFrame,
                        new Point2D(stage.getX() + 30, stage.getY() + 30),
                        currentScale.get());
            }
            case H -> {
                helpMode.set(!helpMode.get());
            }
            case SPACE -> {
                imageNavigator.toggleMarkOnCurrentImage();
            }
            case S -> {
                imageNavigator.toggleSlideshow();

                // If slideshow started and current image is not marked, navigate to first
                // marked
                if (imageNavigator.slideshowModeProperty().get() &&
                        !imageNavigator.isCurrentImageMarked().get()) {
                    File firstMarked = imageNavigator.getFirstMarkedImage();
                    if (firstMarked != null) {
                        setImage(firstMarked, true);
                        applyAspectRatioSize();
                    }
                }
            }
            case C -> {
                if (event.isControlDown()) {
                    copyImageToClipboard();
                }
            }
            default -> {
            }
        }
    }


    private double getFrameBorderWidth() {
        return (stage.getWidth() - scene.getWidth()) / 2;
    }

    private double getTitleBarHeight() {
        return stage.getHeight() - scene.getHeight() - getFrameBorderWidth();
    }

    private ImageService.AspectRatio getAspectRatio() {
        return ImageService.getInstance().getAspectRatio(orgImageWidth.get(), orgImageHeight.get());
    }

    private Dimension2D getWindowSize() {
        return new Dimension2D(orgImageWidth.get() * currentScale.get() + getFrameBorderWidth(),
                orgImageHeight.get() * currentScale.get() + getTitleBarHeight()
                        + (withFrame ? STATUS_HEIGHT : 0));
    }

    private void setWindowSizeFromScale(double scale) {
        if (isFullScreen.get()) {
            return;
        }
        var windowSize = getWindowSize();

        ImageService.AspectRatio aspectRatio = getAspectRatio();
        ImageService.getInstance().setAspectRatioSize(aspectRatio,
                new Dimension2D(orgImageWidth.get() * scale, orgImageHeight.get() * scale));

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
        if (orgImageWidth.get() <= 0 || orgImageHeight.get() <= 0) {
            return 1; // Default scale if dimensions are invalid
        }
        return maxDimension > 0 ? Math.min(maxDimension / orgImageWidth.get(), maxDimension / orgImageHeight.get()) : 1;
    }

    private void setImage(File file, boolean animate) {
        // Cancel any ongoing animation
        cancelCurrentAnimation();

        imageNavigator.setCurrentFile(file);
        var originalImage = ImageService.getInstance().getImageFromFile(file);

        // Restore rotation from memory
        double savedRotation = ImageService.getInstance().getRotationForFile(file);

        // Create rotated image if needed
        var displayImage = ImageService.createRotatedImage(originalImage, savedRotation);
        orgImageWidth.set(displayImage.getWidth());
        orgImageHeight.set(displayImage.getHeight());

        imageRotation.set(savedRotation);

        if (animate && imageView.getImage() != null) {
            crossFadeToImage(displayImage);
        } else {
            imageView.setImage(displayImage);
        }
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
        FadeTransition fadeOut = new FadeTransition(Duration.millis(ImageNavigator.CROSSFADE_DURATION_MILLIS),
                imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.7);

        // Create fade in animation for new image
        FadeTransition fadeIn = new FadeTransition(Duration.millis(ImageNavigator.CROSSFADE_DURATION_MILLIS),
                imageView2);
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

    private void applyAspectRatioSize() {
        ImageService.AspectRatio aspectRatio = getAspectRatio();
        Dimension2D savedSize = ImageService.getInstance().getAspectRatioSize(aspectRatio);
        double maxDimension = savedSize != null ? Math.max(savedSize.getWidth(), savedSize.getHeight()) : MAX_DIMENSION;
        currentScale.set(calcScaleFromMaxDimension(maxDimension));
    }

    private void copyImageToClipboard() {
        if (imageView.getImage() != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            clipboard.setContent(content);
        }
    }
    
    /**
     * Clean up all listeners and bindings to prevent memory leaks
     */
    private void cleanup() {
        // Remove property listeners
        if (currentScaleListener != null) {
            currentScale.removeListener(currentScaleListener);
            currentScaleListener = null;
        }
        
        if (currentFullScreenScaleListener != null) {
            currentFullScreenScale.removeListener(currentFullScreenScaleListener);
            currentFullScreenScaleListener = null;
        }
        
        if (imageTranslateXListener != null) {
            imageTranslateX.removeListener(imageTranslateXListener);
            imageTranslateXListener = null;
        }
        
        if (imageTranslateYListener != null) {
            imageTranslateY.removeListener(imageTranslateYListener);
            imageTranslateYListener = null;
        }
        
        if (imageRotationListener != null) {
            imageRotation.removeListener(imageRotationListener);
            imageRotationListener = null;
        }
        
        // Unbind image view properties
        imageView.fitWidthProperty().unbind();
        imageView.fitHeightProperty().unbind();
        imageView2.fitWidthProperty().unbind();
        imageView2.fitHeightProperty().unbind();
        
        // Unbind stage title
        stage.titleProperty().unbind();
        
        // Unbind status label if it exists
        if (statusLabel != null) {
            statusLabel.textProperty().unbind();
        }
        
        // Cancel any running animations
        cancelCurrentAnimation();
        
        // Clear image references
        imageView.setImage(null);
        imageView2.setImage(null);
        
        // Note: imageCache and rotationMemory are now managed by ImageService singleton
        // and shared across all windows, so they are not cleared here
    }
}