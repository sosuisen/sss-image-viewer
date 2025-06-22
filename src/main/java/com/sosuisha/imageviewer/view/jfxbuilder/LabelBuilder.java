package com.sosuisha.imageviewer.view.jfxbuilder;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.event.*;
import javafx.geometry.*;
public class LabelBuilder {
    private Label in;
    public static  LabelBuilder create() { return new LabelBuilder(); }
    private LabelBuilder() { in = new Label(); }
    public static  LabelBuilder create(String value) { return new LabelBuilder(value); }
    private LabelBuilder(String value) { in = new Label(value); }
    public static  LabelBuilder create(String value1, Node value2) { return new LabelBuilder(value1, value2); }
    private LabelBuilder(String value1, Node value2) { in = new Label(value1, value2); }
    public Label build() { return in; }
    public LabelBuilder apply(java.util.function.Consumer<Label> func) {
        func.accept((Label) in);
        return this;
    }
    public LabelBuilder labelFor(Node value) { in.setLabelFor(value); return this; }
    public LabelBuilder text(String value) { in.setText(value); return this; }
    public LabelBuilder alignment(Pos value) { in.setAlignment(value); return this; }
    public LabelBuilder textAlignment(TextAlignment value) { in.setTextAlignment(value); return this; }
    public LabelBuilder textOverrun(OverrunStyle value) { in.setTextOverrun(value); return this; }
    public LabelBuilder ellipsisString(String value) { in.setEllipsisString(value); return this; }
    public LabelBuilder wrapText(boolean value) { in.setWrapText(value); return this; }
    public LabelBuilder font(Font value) { in.setFont(value); return this; }
    public LabelBuilder graphic(Node value) { in.setGraphic(value); return this; }
    public LabelBuilder underline(boolean value) { in.setUnderline(value); return this; }
    public LabelBuilder lineSpacing(double value) { in.setLineSpacing(value); return this; }
    public LabelBuilder contentDisplay(ContentDisplay value) { in.setContentDisplay(value); return this; }
    public LabelBuilder graphicTextGap(double value) { in.setGraphicTextGap(value); return this; }
    public LabelBuilder textFill(Paint value) { in.setTextFill(value); return this; }
    public LabelBuilder mnemonicParsing(boolean value) { in.setMnemonicParsing(value); return this; }
    public LabelBuilder skin(Skin<?> value) { in.setSkin(value); return this; }
    public LabelBuilder tooltip(Tooltip value) { in.setTooltip(value); return this; }
    public LabelBuilder contextMenu(ContextMenu value) { in.setContextMenu(value); return this; }
    public LabelBuilder snapToPixel(boolean value) { in.setSnapToPixel(value); return this; }
    public LabelBuilder padding(Insets value) { in.setPadding(value); return this; }
    public LabelBuilder background(Background value) { in.setBackground(value); return this; }
    public LabelBuilder border(Border value) { in.setBorder(value); return this; }
    public LabelBuilder opaqueInsets(Insets value) { in.setOpaqueInsets(value); return this; }
    public LabelBuilder minWidth(double value) { in.setMinWidth(value); return this; }
    public LabelBuilder minHeight(double value) { in.setMinHeight(value); return this; }
    public LabelBuilder minSize(double width, double height) { in.setMinSize(width, height); return this; }
    public LabelBuilder prefWidth(double value) { in.setPrefWidth(value); return this; }
    public LabelBuilder prefHeight(double value) { in.setPrefHeight(value); return this; }
    public LabelBuilder prefSize(double width, double height) { in.setPrefSize(width, height); return this; }
    public LabelBuilder maxWidth(double value) { in.setMaxWidth(value); return this; }
    public LabelBuilder maxHeight(double value) { in.setMaxHeight(value); return this; }
    public LabelBuilder maxSize(double width, double height) { in.setMaxSize(width, height); return this; }
    public LabelBuilder shape(Shape value) { in.setShape(value); return this; }
    public LabelBuilder scaleShape(boolean value) { in.setScaleShape(value); return this; }
    public LabelBuilder centerShape(boolean value) { in.setCenterShape(value); return this; }
    public LabelBuilder cacheShape(boolean value) { in.setCacheShape(value); return this; }
    public LabelBuilder childrenUnmodifiable() { in.getChildrenUnmodifiable(); return this; }
    public LabelBuilder userData(Object value) { in.setUserData(value); return this; }
    public LabelBuilder id(String value) { in.setId(value); return this; }
    public LabelBuilder styleClass(String styleClassName) { in.getStyleClass().add(styleClassName); return this; }
    public LabelBuilder style(String value) { in.setStyle(value); return this; }
    public LabelBuilder visible(boolean value) { in.setVisible(value); return this; }
    public LabelBuilder cursor(Cursor value) { in.setCursor(value); return this; }
    public LabelBuilder opacity(double value) { in.setOpacity(value); return this; }
    public LabelBuilder blendMode(BlendMode value) { in.setBlendMode(value); return this; }
    public LabelBuilder clip(Node value) { in.setClip(value); return this; }
    public LabelBuilder cache(boolean value) { in.setCache(value); return this; }
    public LabelBuilder cacheHint(CacheHint value) { in.setCacheHint(value); return this; }
    public LabelBuilder effect(Effect value) { in.setEffect(value); return this; }
    public LabelBuilder depthTest(DepthTest value) { in.setDepthTest(value); return this; }
    public LabelBuilder disable(boolean value) { in.setDisable(value); return this; }
    public LabelBuilder pickOnBounds(boolean value) { in.setPickOnBounds(value); return this; }
    public LabelBuilder onDragEntered(EventHandler<? super DragEvent> value) { in.setOnDragEntered(value); return this; }
    public LabelBuilder onDragExited(EventHandler<? super DragEvent> value) { in.setOnDragExited(value); return this; }
    public LabelBuilder onDragOver(EventHandler<? super DragEvent> value) { in.setOnDragOver(value); return this; }
    public LabelBuilder onDragDropped(EventHandler<? super DragEvent> value) { in.setOnDragDropped(value); return this; }
    public LabelBuilder onDragDone(EventHandler<? super DragEvent> value) { in.setOnDragDone(value); return this; }
    public LabelBuilder managed(boolean value) { in.setManaged(value); return this; }
    public LabelBuilder layoutX(double value) { in.setLayoutX(value); return this; }
    public LabelBuilder layoutY(double value) { in.setLayoutY(value); return this; }
    public LabelBuilder viewOrder(double value) { in.setViewOrder(value); return this; }
    public LabelBuilder translateX(double value) { in.setTranslateX(value); return this; }
    public LabelBuilder translateY(double value) { in.setTranslateY(value); return this; }
    public LabelBuilder translateZ(double value) { in.setTranslateZ(value); return this; }
    public LabelBuilder scaleX(double value) { in.setScaleX(value); return this; }
    public LabelBuilder scaleY(double value) { in.setScaleY(value); return this; }
    public LabelBuilder scaleZ(double value) { in.setScaleZ(value); return this; }
    public LabelBuilder rotate(double value) { in.setRotate(value); return this; }
    public LabelBuilder rotationAxis(Point3D value) { in.setRotationAxis(value); return this; }
    public LabelBuilder nodeOrientation(NodeOrientation value) { in.setNodeOrientation(value); return this; }
    public LabelBuilder mouseTransparent(boolean value) { in.setMouseTransparent(value); return this; }
    public LabelBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { in.setOnContextMenuRequested(value); return this; }
    public LabelBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { in.setOnMouseClicked(value); return this; }
    public LabelBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { in.setOnMouseDragged(value); return this; }
    public LabelBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { in.setOnMouseEntered(value); return this; }
    public LabelBuilder onMouseExited(EventHandler<? super MouseEvent> value) { in.setOnMouseExited(value); return this; }
    public LabelBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { in.setOnMouseMoved(value); return this; }
    public LabelBuilder onMousePressed(EventHandler<? super MouseEvent> value) { in.setOnMousePressed(value); return this; }
    public LabelBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { in.setOnMouseReleased(value); return this; }
    public LabelBuilder onDragDetected(EventHandler<? super MouseEvent> value) { in.setOnDragDetected(value); return this; }
    public LabelBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragOver(value); return this; }
    public LabelBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragReleased(value); return this; }
    public LabelBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragEntered(value); return this; }
    public LabelBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragExited(value); return this; }
    public LabelBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { in.setOnScrollStarted(value); return this; }
    public LabelBuilder onScroll(EventHandler<? super ScrollEvent> value) { in.setOnScroll(value); return this; }
    public LabelBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { in.setOnScrollFinished(value); return this; }
    public LabelBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { in.setOnRotationStarted(value); return this; }
    public LabelBuilder onRotate(EventHandler<? super RotateEvent> value) { in.setOnRotate(value); return this; }
    public LabelBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { in.setOnRotationFinished(value); return this; }
    public LabelBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { in.setOnZoomStarted(value); return this; }
    public LabelBuilder onZoom(EventHandler<? super ZoomEvent> value) { in.setOnZoom(value); return this; }
    public LabelBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { in.setOnZoomFinished(value); return this; }
    public LabelBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { in.setOnSwipeUp(value); return this; }
    public LabelBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { in.setOnSwipeDown(value); return this; }
    public LabelBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { in.setOnSwipeLeft(value); return this; }
    public LabelBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { in.setOnSwipeRight(value); return this; }
    public LabelBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { in.setOnTouchPressed(value); return this; }
    public LabelBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { in.setOnTouchMoved(value); return this; }
    public LabelBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { in.setOnTouchReleased(value); return this; }
    public LabelBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { in.setOnTouchStationary(value); return this; }
    public LabelBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { in.setOnKeyPressed(value); return this; }
    public LabelBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { in.setOnKeyReleased(value); return this; }
    public LabelBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { in.setOnKeyTyped(value); return this; }
    public LabelBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { in.setOnInputMethodTextChanged(value); return this; }
    public LabelBuilder inputMethodRequests(InputMethodRequests value) { in.setInputMethodRequests(value); return this; }
    public LabelBuilder focusTraversable(boolean value) { in.setFocusTraversable(value); return this; }
    public LabelBuilder eventDispatcher(EventDispatcher value) { in.setEventDispatcher(value); return this; }
    public LabelBuilder accessibleRole(AccessibleRole value) { in.setAccessibleRole(value); return this; }
    public LabelBuilder accessibleRoleDescription(String value) { in.setAccessibleRoleDescription(value); return this; }
    public LabelBuilder accessibleText(String value) { in.setAccessibleText(value); return this; }
    public LabelBuilder accessibleHelp(String value) { in.setAccessibleHelp(value); return this; }
}
