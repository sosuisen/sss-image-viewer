package com.sosuisha.imageviewer.view.jfxbuilder;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.geometry.*;
public class ImageViewBuilder {
    private ImageView in;
    public static  ImageViewBuilder create() { return new ImageViewBuilder(); }
    private ImageViewBuilder() { in = new ImageView(); }
    public static  ImageViewBuilder create(String value) { return new ImageViewBuilder(value); }
    private ImageViewBuilder(String value) { in = new ImageView(value); }
    public static  ImageViewBuilder create(Image value) { return new ImageViewBuilder(value); }
    private ImageViewBuilder(Image value) { in = new ImageView(value); }
    public ImageView build() { return in; }
    public ImageViewBuilder apply(java.util.function.Consumer<ImageView> func) {
        func.accept((ImageView) in);
        return this;
    }
    public ImageViewBuilder image(Image value) { in.setImage(value); return this; }
    public ImageViewBuilder x(double value) { in.setX(value); return this; }
    public ImageViewBuilder y(double value) { in.setY(value); return this; }
    public ImageViewBuilder fitWidth(double value) { in.setFitWidth(value); return this; }
    public ImageViewBuilder fitHeight(double value) { in.setFitHeight(value); return this; }
    public ImageViewBuilder preserveRatio(boolean value) { in.setPreserveRatio(value); return this; }
    public ImageViewBuilder smooth(boolean value) { in.setSmooth(value); return this; }
    public ImageViewBuilder viewport(Rectangle2D value) { in.setViewport(value); return this; }
    public ImageViewBuilder userData(Object value) { in.setUserData(value); return this; }
    public ImageViewBuilder id(String value) { in.setId(value); return this; }
    public ImageViewBuilder styleClass(String styleClassName) { in.getStyleClass().add(styleClassName); return this; }
    public ImageViewBuilder style(String value) { in.setStyle(value); return this; }
    public ImageViewBuilder visible(boolean value) { in.setVisible(value); return this; }
    public ImageViewBuilder cursor(Cursor value) { in.setCursor(value); return this; }
    public ImageViewBuilder opacity(double value) { in.setOpacity(value); return this; }
    public ImageViewBuilder blendMode(BlendMode value) { in.setBlendMode(value); return this; }
    public ImageViewBuilder clip(Node value) { in.setClip(value); return this; }
    public ImageViewBuilder cache(boolean value) { in.setCache(value); return this; }
    public ImageViewBuilder cacheHint(CacheHint value) { in.setCacheHint(value); return this; }
    public ImageViewBuilder effect(Effect value) { in.setEffect(value); return this; }
    public ImageViewBuilder depthTest(DepthTest value) { in.setDepthTest(value); return this; }
    public ImageViewBuilder disable(boolean value) { in.setDisable(value); return this; }
    public ImageViewBuilder pickOnBounds(boolean value) { in.setPickOnBounds(value); return this; }
    public ImageViewBuilder onDragEntered(EventHandler<? super DragEvent> value) { in.setOnDragEntered(value); return this; }
    public ImageViewBuilder onDragExited(EventHandler<? super DragEvent> value) { in.setOnDragExited(value); return this; }
    public ImageViewBuilder onDragOver(EventHandler<? super DragEvent> value) { in.setOnDragOver(value); return this; }
    public ImageViewBuilder onDragDropped(EventHandler<? super DragEvent> value) { in.setOnDragDropped(value); return this; }
    public ImageViewBuilder onDragDone(EventHandler<? super DragEvent> value) { in.setOnDragDone(value); return this; }
    public ImageViewBuilder managed(boolean value) { in.setManaged(value); return this; }
    public ImageViewBuilder layoutX(double value) { in.setLayoutX(value); return this; }
    public ImageViewBuilder layoutY(double value) { in.setLayoutY(value); return this; }
    public ImageViewBuilder viewOrder(double value) { in.setViewOrder(value); return this; }
    public ImageViewBuilder translateX(double value) { in.setTranslateX(value); return this; }
    public ImageViewBuilder translateY(double value) { in.setTranslateY(value); return this; }
    public ImageViewBuilder translateZ(double value) { in.setTranslateZ(value); return this; }
    public ImageViewBuilder scaleX(double value) { in.setScaleX(value); return this; }
    public ImageViewBuilder scaleY(double value) { in.setScaleY(value); return this; }
    public ImageViewBuilder scaleZ(double value) { in.setScaleZ(value); return this; }
    public ImageViewBuilder rotate(double value) { in.setRotate(value); return this; }
    public ImageViewBuilder rotationAxis(Point3D value) { in.setRotationAxis(value); return this; }
    public ImageViewBuilder nodeOrientation(NodeOrientation value) { in.setNodeOrientation(value); return this; }
    public ImageViewBuilder mouseTransparent(boolean value) { in.setMouseTransparent(value); return this; }
    public ImageViewBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { in.setOnContextMenuRequested(value); return this; }
    public ImageViewBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { in.setOnMouseClicked(value); return this; }
    public ImageViewBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { in.setOnMouseDragged(value); return this; }
    public ImageViewBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { in.setOnMouseEntered(value); return this; }
    public ImageViewBuilder onMouseExited(EventHandler<? super MouseEvent> value) { in.setOnMouseExited(value); return this; }
    public ImageViewBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { in.setOnMouseMoved(value); return this; }
    public ImageViewBuilder onMousePressed(EventHandler<? super MouseEvent> value) { in.setOnMousePressed(value); return this; }
    public ImageViewBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { in.setOnMouseReleased(value); return this; }
    public ImageViewBuilder onDragDetected(EventHandler<? super MouseEvent> value) { in.setOnDragDetected(value); return this; }
    public ImageViewBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragOver(value); return this; }
    public ImageViewBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragReleased(value); return this; }
    public ImageViewBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragEntered(value); return this; }
    public ImageViewBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragExited(value); return this; }
    public ImageViewBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { in.setOnScrollStarted(value); return this; }
    public ImageViewBuilder onScroll(EventHandler<? super ScrollEvent> value) { in.setOnScroll(value); return this; }
    public ImageViewBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { in.setOnScrollFinished(value); return this; }
    public ImageViewBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { in.setOnRotationStarted(value); return this; }
    public ImageViewBuilder onRotate(EventHandler<? super RotateEvent> value) { in.setOnRotate(value); return this; }
    public ImageViewBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { in.setOnRotationFinished(value); return this; }
    public ImageViewBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { in.setOnZoomStarted(value); return this; }
    public ImageViewBuilder onZoom(EventHandler<? super ZoomEvent> value) { in.setOnZoom(value); return this; }
    public ImageViewBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { in.setOnZoomFinished(value); return this; }
    public ImageViewBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { in.setOnSwipeUp(value); return this; }
    public ImageViewBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { in.setOnSwipeDown(value); return this; }
    public ImageViewBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { in.setOnSwipeLeft(value); return this; }
    public ImageViewBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { in.setOnSwipeRight(value); return this; }
    public ImageViewBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { in.setOnTouchPressed(value); return this; }
    public ImageViewBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { in.setOnTouchMoved(value); return this; }
    public ImageViewBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { in.setOnTouchReleased(value); return this; }
    public ImageViewBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { in.setOnTouchStationary(value); return this; }
    public ImageViewBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { in.setOnKeyPressed(value); return this; }
    public ImageViewBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { in.setOnKeyReleased(value); return this; }
    public ImageViewBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { in.setOnKeyTyped(value); return this; }
    public ImageViewBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { in.setOnInputMethodTextChanged(value); return this; }
    public ImageViewBuilder inputMethodRequests(InputMethodRequests value) { in.setInputMethodRequests(value); return this; }
    public ImageViewBuilder focusTraversable(boolean value) { in.setFocusTraversable(value); return this; }
    public ImageViewBuilder eventDispatcher(EventDispatcher value) { in.setEventDispatcher(value); return this; }
    public ImageViewBuilder accessibleRole(AccessibleRole value) { in.setAccessibleRole(value); return this; }
    public ImageViewBuilder accessibleRoleDescription(String value) { in.setAccessibleRoleDescription(value); return this; }
    public ImageViewBuilder accessibleText(String value) { in.setAccessibleText(value); return this; }
    public ImageViewBuilder accessibleHelp(String value) { in.setAccessibleHelp(value); return this; }
}
