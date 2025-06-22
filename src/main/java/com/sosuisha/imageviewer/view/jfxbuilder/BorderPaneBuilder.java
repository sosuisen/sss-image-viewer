package com.sosuisha.imageviewer.view.jfxbuilder;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.event.*;
import javafx.geometry.*;
public class BorderPaneBuilder {
    private BorderPane in;
    public static  BorderPaneBuilder create() { return new BorderPaneBuilder(); }
    private BorderPaneBuilder() { in = new BorderPane(); }
    public static  BorderPaneBuilder create(Node value) { return new BorderPaneBuilder(value); }
    private BorderPaneBuilder(Node value) { in = new BorderPane(value); }
    public static  BorderPaneBuilder create(Node value1, Node value2, Node value3, Node value4, Node value5) { return new BorderPaneBuilder(value1, value2, value3, value4, value5); }
    private BorderPaneBuilder(Node value1, Node value2, Node value3, Node value4, Node value5) { in = new BorderPane(value1, value2, value3, value4, value5); }
    public BorderPane build() { return in; }
    public BorderPaneBuilder apply(java.util.function.Consumer<BorderPane> func) {
        func.accept((BorderPane) in);
        return this;
    }
    public BorderPaneBuilder center(Node value) { in.setCenter(value); return this; }
    public BorderPaneBuilder top(Node value) { in.setTop(value); return this; }
    public BorderPaneBuilder bottom(Node value) { in.setBottom(value); return this; }
    public BorderPaneBuilder left(Node value) { in.setLeft(value); return this; }
    public BorderPaneBuilder right(Node value) { in.setRight(value); return this; }
    public BorderPaneBuilder children(Node... elements) { in.getChildren().setAll(elements); return this; }
    public BorderPaneBuilder snapToPixel(boolean value) { in.setSnapToPixel(value); return this; }
    public BorderPaneBuilder padding(Insets value) { in.setPadding(value); return this; }
    public BorderPaneBuilder background(Background value) { in.setBackground(value); return this; }
    public BorderPaneBuilder border(Border value) { in.setBorder(value); return this; }
    public BorderPaneBuilder opaqueInsets(Insets value) { in.setOpaqueInsets(value); return this; }
    public BorderPaneBuilder minWidth(double value) { in.setMinWidth(value); return this; }
    public BorderPaneBuilder minHeight(double value) { in.setMinHeight(value); return this; }
    public BorderPaneBuilder minSize(double width, double height) { in.setMinSize(width, height); return this; }
    public BorderPaneBuilder prefWidth(double value) { in.setPrefWidth(value); return this; }
    public BorderPaneBuilder prefHeight(double value) { in.setPrefHeight(value); return this; }
    public BorderPaneBuilder prefSize(double width, double height) { in.setPrefSize(width, height); return this; }
    public BorderPaneBuilder maxWidth(double value) { in.setMaxWidth(value); return this; }
    public BorderPaneBuilder maxHeight(double value) { in.setMaxHeight(value); return this; }
    public BorderPaneBuilder maxSize(double width, double height) { in.setMaxSize(width, height); return this; }
    public BorderPaneBuilder shape(Shape value) { in.setShape(value); return this; }
    public BorderPaneBuilder scaleShape(boolean value) { in.setScaleShape(value); return this; }
    public BorderPaneBuilder centerShape(boolean value) { in.setCenterShape(value); return this; }
    public BorderPaneBuilder cacheShape(boolean value) { in.setCacheShape(value); return this; }
    public BorderPaneBuilder childrenUnmodifiable() { in.getChildrenUnmodifiable(); return this; }
    public BorderPaneBuilder userData(Object value) { in.setUserData(value); return this; }
    public BorderPaneBuilder id(String value) { in.setId(value); return this; }
    public BorderPaneBuilder styleClass(String styleClassName) { in.getStyleClass().add(styleClassName); return this; }
    public BorderPaneBuilder style(String value) { in.setStyle(value); return this; }
    public BorderPaneBuilder visible(boolean value) { in.setVisible(value); return this; }
    public BorderPaneBuilder cursor(Cursor value) { in.setCursor(value); return this; }
    public BorderPaneBuilder opacity(double value) { in.setOpacity(value); return this; }
    public BorderPaneBuilder blendMode(BlendMode value) { in.setBlendMode(value); return this; }
    public BorderPaneBuilder clip(Node value) { in.setClip(value); return this; }
    public BorderPaneBuilder cache(boolean value) { in.setCache(value); return this; }
    public BorderPaneBuilder cacheHint(CacheHint value) { in.setCacheHint(value); return this; }
    public BorderPaneBuilder effect(Effect value) { in.setEffect(value); return this; }
    public BorderPaneBuilder depthTest(DepthTest value) { in.setDepthTest(value); return this; }
    public BorderPaneBuilder disable(boolean value) { in.setDisable(value); return this; }
    public BorderPaneBuilder pickOnBounds(boolean value) { in.setPickOnBounds(value); return this; }
    public BorderPaneBuilder onDragEntered(EventHandler<? super DragEvent> value) { in.setOnDragEntered(value); return this; }
    public BorderPaneBuilder onDragExited(EventHandler<? super DragEvent> value) { in.setOnDragExited(value); return this; }
    public BorderPaneBuilder onDragOver(EventHandler<? super DragEvent> value) { in.setOnDragOver(value); return this; }
    public BorderPaneBuilder onDragDropped(EventHandler<? super DragEvent> value) { in.setOnDragDropped(value); return this; }
    public BorderPaneBuilder onDragDone(EventHandler<? super DragEvent> value) { in.setOnDragDone(value); return this; }
    public BorderPaneBuilder managed(boolean value) { in.setManaged(value); return this; }
    public BorderPaneBuilder layoutX(double value) { in.setLayoutX(value); return this; }
    public BorderPaneBuilder layoutY(double value) { in.setLayoutY(value); return this; }
    public BorderPaneBuilder viewOrder(double value) { in.setViewOrder(value); return this; }
    public BorderPaneBuilder translateX(double value) { in.setTranslateX(value); return this; }
    public BorderPaneBuilder translateY(double value) { in.setTranslateY(value); return this; }
    public BorderPaneBuilder translateZ(double value) { in.setTranslateZ(value); return this; }
    public BorderPaneBuilder scaleX(double value) { in.setScaleX(value); return this; }
    public BorderPaneBuilder scaleY(double value) { in.setScaleY(value); return this; }
    public BorderPaneBuilder scaleZ(double value) { in.setScaleZ(value); return this; }
    public BorderPaneBuilder rotate(double value) { in.setRotate(value); return this; }
    public BorderPaneBuilder rotationAxis(Point3D value) { in.setRotationAxis(value); return this; }
    public BorderPaneBuilder nodeOrientation(NodeOrientation value) { in.setNodeOrientation(value); return this; }
    public BorderPaneBuilder mouseTransparent(boolean value) { in.setMouseTransparent(value); return this; }
    public BorderPaneBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { in.setOnContextMenuRequested(value); return this; }
    public BorderPaneBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { in.setOnMouseClicked(value); return this; }
    public BorderPaneBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { in.setOnMouseDragged(value); return this; }
    public BorderPaneBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { in.setOnMouseEntered(value); return this; }
    public BorderPaneBuilder onMouseExited(EventHandler<? super MouseEvent> value) { in.setOnMouseExited(value); return this; }
    public BorderPaneBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { in.setOnMouseMoved(value); return this; }
    public BorderPaneBuilder onMousePressed(EventHandler<? super MouseEvent> value) { in.setOnMousePressed(value); return this; }
    public BorderPaneBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { in.setOnMouseReleased(value); return this; }
    public BorderPaneBuilder onDragDetected(EventHandler<? super MouseEvent> value) { in.setOnDragDetected(value); return this; }
    public BorderPaneBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragOver(value); return this; }
    public BorderPaneBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragReleased(value); return this; }
    public BorderPaneBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragEntered(value); return this; }
    public BorderPaneBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragExited(value); return this; }
    public BorderPaneBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { in.setOnScrollStarted(value); return this; }
    public BorderPaneBuilder onScroll(EventHandler<? super ScrollEvent> value) { in.setOnScroll(value); return this; }
    public BorderPaneBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { in.setOnScrollFinished(value); return this; }
    public BorderPaneBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { in.setOnRotationStarted(value); return this; }
    public BorderPaneBuilder onRotate(EventHandler<? super RotateEvent> value) { in.setOnRotate(value); return this; }
    public BorderPaneBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { in.setOnRotationFinished(value); return this; }
    public BorderPaneBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { in.setOnZoomStarted(value); return this; }
    public BorderPaneBuilder onZoom(EventHandler<? super ZoomEvent> value) { in.setOnZoom(value); return this; }
    public BorderPaneBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { in.setOnZoomFinished(value); return this; }
    public BorderPaneBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { in.setOnSwipeUp(value); return this; }
    public BorderPaneBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { in.setOnSwipeDown(value); return this; }
    public BorderPaneBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { in.setOnSwipeLeft(value); return this; }
    public BorderPaneBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { in.setOnSwipeRight(value); return this; }
    public BorderPaneBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { in.setOnTouchPressed(value); return this; }
    public BorderPaneBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { in.setOnTouchMoved(value); return this; }
    public BorderPaneBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { in.setOnTouchReleased(value); return this; }
    public BorderPaneBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { in.setOnTouchStationary(value); return this; }
    public BorderPaneBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { in.setOnKeyPressed(value); return this; }
    public BorderPaneBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { in.setOnKeyReleased(value); return this; }
    public BorderPaneBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { in.setOnKeyTyped(value); return this; }
    public BorderPaneBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { in.setOnInputMethodTextChanged(value); return this; }
    public BorderPaneBuilder inputMethodRequests(InputMethodRequests value) { in.setInputMethodRequests(value); return this; }
    public BorderPaneBuilder focusTraversable(boolean value) { in.setFocusTraversable(value); return this; }
    public BorderPaneBuilder eventDispatcher(EventDispatcher value) { in.setEventDispatcher(value); return this; }
    public BorderPaneBuilder accessibleRole(AccessibleRole value) { in.setAccessibleRole(value); return this; }
    public BorderPaneBuilder accessibleRoleDescription(String value) { in.setAccessibleRoleDescription(value); return this; }
    public BorderPaneBuilder accessibleText(String value) { in.setAccessibleText(value); return this; }
    public BorderPaneBuilder accessibleHelp(String value) { in.setAccessibleHelp(value); return this; }
}
