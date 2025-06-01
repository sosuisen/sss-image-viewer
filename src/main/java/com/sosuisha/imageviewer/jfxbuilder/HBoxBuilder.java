package com.sosuisha.imageviewer.jfxbuilder;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.event.*;
import javafx.geometry.*;
public class HBoxBuilder {
    private HBox in;
    public static  HBoxBuilder create() { return new HBoxBuilder(); }
    private HBoxBuilder() { in = new HBox(); }
    public static  HBoxBuilder create(double value) { return new HBoxBuilder(value); }
    private HBoxBuilder(double value) { in = new HBox(value); }
    public static  HBoxBuilder create(Node... value) { return new HBoxBuilder(value); }
    private HBoxBuilder(Node... value) { in = new HBox(value); }
    public static  HBoxBuilder create(double value1, Node... value2) { return new HBoxBuilder(value1, value2); }
    private HBoxBuilder(double value1, Node... value2) { in = new HBox(value1, value2); }
    public HBox build() { return in; }
    public HBoxBuilder apply(java.util.function.Consumer<HBox> func) {
        func.accept((HBox) in);
        return this;
    }
    public HBoxBuilder spacing(double value) { in.setSpacing(value); return this; }
    public HBoxBuilder alignment(Pos value) { in.setAlignment(value); return this; }
    public HBoxBuilder fillHeight(boolean value) { in.setFillHeight(value); return this; }
    public HBoxBuilder children(Node... elements) { in.getChildren().setAll(elements); return this; }
    public HBoxBuilder snapToPixel(boolean value) { in.setSnapToPixel(value); return this; }
    public HBoxBuilder padding(Insets value) { in.setPadding(value); return this; }
    public HBoxBuilder background(Background value) { in.setBackground(value); return this; }
    public HBoxBuilder border(Border value) { in.setBorder(value); return this; }
    public HBoxBuilder opaqueInsets(Insets value) { in.setOpaqueInsets(value); return this; }
    public HBoxBuilder minWidth(double value) { in.setMinWidth(value); return this; }
    public HBoxBuilder minHeight(double value) { in.setMinHeight(value); return this; }
    public HBoxBuilder minSize(double width, double height) { in.setMinSize(width, height); return this; }
    public HBoxBuilder prefWidth(double value) { in.setPrefWidth(value); return this; }
    public HBoxBuilder prefHeight(double value) { in.setPrefHeight(value); return this; }
    public HBoxBuilder prefSize(double width, double height) { in.setPrefSize(width, height); return this; }
    public HBoxBuilder maxWidth(double value) { in.setMaxWidth(value); return this; }
    public HBoxBuilder maxHeight(double value) { in.setMaxHeight(value); return this; }
    public HBoxBuilder maxSize(double width, double height) { in.setMaxSize(width, height); return this; }
    public HBoxBuilder shape(Shape value) { in.setShape(value); return this; }
    public HBoxBuilder scaleShape(boolean value) { in.setScaleShape(value); return this; }
    public HBoxBuilder centerShape(boolean value) { in.setCenterShape(value); return this; }
    public HBoxBuilder cacheShape(boolean value) { in.setCacheShape(value); return this; }
    public HBoxBuilder childrenUnmodifiable() { in.getChildrenUnmodifiable(); return this; }
    public HBoxBuilder userData(Object value) { in.setUserData(value); return this; }
    public HBoxBuilder id(String value) { in.setId(value); return this; }
    public HBoxBuilder styleClass(String styleClassName) { in.getStyleClass().add(styleClassName); return this; }
    public HBoxBuilder style(String value) { in.setStyle(value); return this; }
    public HBoxBuilder visible(boolean value) { in.setVisible(value); return this; }
    public HBoxBuilder cursor(Cursor value) { in.setCursor(value); return this; }
    public HBoxBuilder opacity(double value) { in.setOpacity(value); return this; }
    public HBoxBuilder blendMode(BlendMode value) { in.setBlendMode(value); return this; }
    public HBoxBuilder clip(Node value) { in.setClip(value); return this; }
    public HBoxBuilder cache(boolean value) { in.setCache(value); return this; }
    public HBoxBuilder cacheHint(CacheHint value) { in.setCacheHint(value); return this; }
    public HBoxBuilder effect(Effect value) { in.setEffect(value); return this; }
    public HBoxBuilder depthTest(DepthTest value) { in.setDepthTest(value); return this; }
    public HBoxBuilder disable(boolean value) { in.setDisable(value); return this; }
    public HBoxBuilder pickOnBounds(boolean value) { in.setPickOnBounds(value); return this; }
    public HBoxBuilder onDragEntered(EventHandler<? super DragEvent> value) { in.setOnDragEntered(value); return this; }
    public HBoxBuilder onDragExited(EventHandler<? super DragEvent> value) { in.setOnDragExited(value); return this; }
    public HBoxBuilder onDragOver(EventHandler<? super DragEvent> value) { in.setOnDragOver(value); return this; }
    public HBoxBuilder onDragDropped(EventHandler<? super DragEvent> value) { in.setOnDragDropped(value); return this; }
    public HBoxBuilder onDragDone(EventHandler<? super DragEvent> value) { in.setOnDragDone(value); return this; }
    public HBoxBuilder managed(boolean value) { in.setManaged(value); return this; }
    public HBoxBuilder layoutX(double value) { in.setLayoutX(value); return this; }
    public HBoxBuilder layoutY(double value) { in.setLayoutY(value); return this; }
    public HBoxBuilder viewOrder(double value) { in.setViewOrder(value); return this; }
    public HBoxBuilder translateX(double value) { in.setTranslateX(value); return this; }
    public HBoxBuilder translateY(double value) { in.setTranslateY(value); return this; }
    public HBoxBuilder translateZ(double value) { in.setTranslateZ(value); return this; }
    public HBoxBuilder scaleX(double value) { in.setScaleX(value); return this; }
    public HBoxBuilder scaleY(double value) { in.setScaleY(value); return this; }
    public HBoxBuilder scaleZ(double value) { in.setScaleZ(value); return this; }
    public HBoxBuilder rotate(double value) { in.setRotate(value); return this; }
    public HBoxBuilder rotationAxis(Point3D value) { in.setRotationAxis(value); return this; }
    public HBoxBuilder nodeOrientation(NodeOrientation value) { in.setNodeOrientation(value); return this; }
    public HBoxBuilder mouseTransparent(boolean value) { in.setMouseTransparent(value); return this; }
    public HBoxBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { in.setOnContextMenuRequested(value); return this; }
    public HBoxBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { in.setOnMouseClicked(value); return this; }
    public HBoxBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { in.setOnMouseDragged(value); return this; }
    public HBoxBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { in.setOnMouseEntered(value); return this; }
    public HBoxBuilder onMouseExited(EventHandler<? super MouseEvent> value) { in.setOnMouseExited(value); return this; }
    public HBoxBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { in.setOnMouseMoved(value); return this; }
    public HBoxBuilder onMousePressed(EventHandler<? super MouseEvent> value) { in.setOnMousePressed(value); return this; }
    public HBoxBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { in.setOnMouseReleased(value); return this; }
    public HBoxBuilder onDragDetected(EventHandler<? super MouseEvent> value) { in.setOnDragDetected(value); return this; }
    public HBoxBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragOver(value); return this; }
    public HBoxBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragReleased(value); return this; }
    public HBoxBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragEntered(value); return this; }
    public HBoxBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragExited(value); return this; }
    public HBoxBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { in.setOnScrollStarted(value); return this; }
    public HBoxBuilder onScroll(EventHandler<? super ScrollEvent> value) { in.setOnScroll(value); return this; }
    public HBoxBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { in.setOnScrollFinished(value); return this; }
    public HBoxBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { in.setOnRotationStarted(value); return this; }
    public HBoxBuilder onRotate(EventHandler<? super RotateEvent> value) { in.setOnRotate(value); return this; }
    public HBoxBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { in.setOnRotationFinished(value); return this; }
    public HBoxBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { in.setOnZoomStarted(value); return this; }
    public HBoxBuilder onZoom(EventHandler<? super ZoomEvent> value) { in.setOnZoom(value); return this; }
    public HBoxBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { in.setOnZoomFinished(value); return this; }
    public HBoxBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { in.setOnSwipeUp(value); return this; }
    public HBoxBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { in.setOnSwipeDown(value); return this; }
    public HBoxBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { in.setOnSwipeLeft(value); return this; }
    public HBoxBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { in.setOnSwipeRight(value); return this; }
    public HBoxBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { in.setOnTouchPressed(value); return this; }
    public HBoxBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { in.setOnTouchMoved(value); return this; }
    public HBoxBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { in.setOnTouchReleased(value); return this; }
    public HBoxBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { in.setOnTouchStationary(value); return this; }
    public HBoxBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { in.setOnKeyPressed(value); return this; }
    public HBoxBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { in.setOnKeyReleased(value); return this; }
    public HBoxBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { in.setOnKeyTyped(value); return this; }
    public HBoxBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { in.setOnInputMethodTextChanged(value); return this; }
    public HBoxBuilder inputMethodRequests(InputMethodRequests value) { in.setInputMethodRequests(value); return this; }
    public HBoxBuilder focusTraversable(boolean value) { in.setFocusTraversable(value); return this; }
    public HBoxBuilder eventDispatcher(EventDispatcher value) { in.setEventDispatcher(value); return this; }
    public HBoxBuilder accessibleRole(AccessibleRole value) { in.setAccessibleRole(value); return this; }
    public HBoxBuilder accessibleRoleDescription(String value) { in.setAccessibleRoleDescription(value); return this; }
    public HBoxBuilder accessibleText(String value) { in.setAccessibleText(value); return this; }
    public HBoxBuilder accessibleHelp(String value) { in.setAccessibleHelp(value); return this; }
}
