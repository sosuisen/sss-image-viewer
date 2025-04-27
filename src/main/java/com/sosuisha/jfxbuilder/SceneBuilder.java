package com.sosuisha.jfxbuilder;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.geometry.*;
public class SceneBuilder {
    private Scene in;
    private Parent root;
    public SceneBuilder root(Parent root) { 
                            if(in == null) { this.root = root; }
                            else { in.setRoot(root); }
                            return this; }
    private double width = -1.0d;
    public SceneBuilder width(double width) { 
                            if(in == null) { this.width = width; }
                            return this; }
    private double height = -1.0d;
    public SceneBuilder height(double height) { 
                            if(in == null) { this.height = height; }
                            return this; }
    private boolean depthBuffer;
    public SceneBuilder depthBuffer(boolean depthBuffer) { 
                            if(in == null) { this.depthBuffer = depthBuffer; }
                            return this; }
    private SceneAntialiasing antiAliasing;
    public SceneBuilder antiAliasing(SceneAntialiasing antiAliasing) { 
                            if(in == null) { this.antiAliasing = antiAliasing; }
                            return this; }
    private Paint fill;
    public SceneBuilder fill(Paint fill) { 
                            if(in == null) { this.fill = fill; }
                            else { in.setFill(fill); }
                            return this; }
    public static SceneBuilder create() { return new SceneBuilder(); }
    private SceneBuilder() {}
    public static  SceneBuilder create(Parent value) { return new SceneBuilder(value); }
    private SceneBuilder(Parent value) { in = new Scene(value); }
    public static  SceneBuilder create(Parent value1, double value2, double value3) { return new SceneBuilder(value1, value2, value3); }
    private SceneBuilder(Parent value1, double value2, double value3) { in = new Scene(value1, value2, value3); }
    public static  SceneBuilder create(Parent value1, Paint value2) { return new SceneBuilder(value1, value2); }
    private SceneBuilder(Parent value1, Paint value2) { in = new Scene(value1, value2); }
    public static  SceneBuilder create(Parent value1, double value2, double value3, Paint value4) { return new SceneBuilder(value1, value2, value3, value4); }
    private SceneBuilder(Parent value1, double value2, double value3, Paint value4) { in = new Scene(value1, value2, value3, value4); }
    public static  SceneBuilder create(Parent value1, double value2, double value3, boolean value4) { return new SceneBuilder(value1, value2, value3, value4); }
    private SceneBuilder(Parent value1, double value2, double value3, boolean value4) { in = new Scene(value1, value2, value3, value4); }
    public static  SceneBuilder create(Parent value1, double value2, double value3, boolean value4, SceneAntialiasing value5) { return new SceneBuilder(value1, value2, value3, value4, value5); }
    private SceneBuilder(Parent value1, double value2, double value3, boolean value4, SceneAntialiasing value5) { in = new Scene(value1, value2, value3, value4, value5); }
    public Scene build() { 
                if(in == null) {
                    if(root != null && width != -1.0d && height != -1.0d && antiAliasing != null) {
                        in = new Scene(root, width, height, depthBuffer, antiAliasing);
                    }else if(root != null && width != -1.0d && height != -1.0d) {
                        in = new Scene(root, width, height, depthBuffer);
                    }else if(root != null && width != -1.0d && height != -1.0d && fill != null) {
                        in = new Scene(root, width, height, fill);
                    }else if(root != null && width != -1.0d && height != -1.0d) {
                        in = new Scene(root, width, height);
                    }else if(root != null && fill != null) {
                        in = new Scene(root, fill);
                    }else if(root != null) {
                        in = new Scene(root);
                    }
                }
                return in;
            }
    public SceneBuilder apply(java.util.function.Consumer<Scene> func) {
        if ( in == null ) {
            build();
        }
        func.accept((Scene) in);
        return this;
    }
    public SceneBuilder camera(Camera value) { in.setCamera(value); return this; }
    public SceneBuilder cursor(Cursor value) { in.setCursor(value); return this; }
    public SceneBuilder userAgentStylesheet(String value) { in.setUserAgentStylesheet(value); return this; }
    public SceneBuilder eventDispatcher(EventDispatcher value) { in.setEventDispatcher(value); return this; }
    public SceneBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { in.setOnContextMenuRequested(value); return this; }
    public SceneBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { in.setOnMouseClicked(value); return this; }
    public SceneBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { in.setOnMouseDragged(value); return this; }
    public SceneBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { in.setOnMouseEntered(value); return this; }
    public SceneBuilder onMouseExited(EventHandler<? super MouseEvent> value) { in.setOnMouseExited(value); return this; }
    public SceneBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { in.setOnMouseMoved(value); return this; }
    public SceneBuilder onMousePressed(EventHandler<? super MouseEvent> value) { in.setOnMousePressed(value); return this; }
    public SceneBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { in.setOnMouseReleased(value); return this; }
    public SceneBuilder onDragDetected(EventHandler<? super MouseEvent> value) { in.setOnDragDetected(value); return this; }
    public SceneBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragOver(value); return this; }
    public SceneBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragReleased(value); return this; }
    public SceneBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragEntered(value); return this; }
    public SceneBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { in.setOnMouseDragExited(value); return this; }
    public SceneBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { in.setOnScrollStarted(value); return this; }
    public SceneBuilder onScroll(EventHandler<? super ScrollEvent> value) { in.setOnScroll(value); return this; }
    public SceneBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { in.setOnScrollFinished(value); return this; }
    public SceneBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { in.setOnRotationStarted(value); return this; }
    public SceneBuilder onRotate(EventHandler<? super RotateEvent> value) { in.setOnRotate(value); return this; }
    public SceneBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { in.setOnRotationFinished(value); return this; }
    public SceneBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { in.setOnZoomStarted(value); return this; }
    public SceneBuilder onZoom(EventHandler<? super ZoomEvent> value) { in.setOnZoom(value); return this; }
    public SceneBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { in.setOnZoomFinished(value); return this; }
    public SceneBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { in.setOnSwipeUp(value); return this; }
    public SceneBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { in.setOnSwipeDown(value); return this; }
    public SceneBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { in.setOnSwipeLeft(value); return this; }
    public SceneBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { in.setOnSwipeRight(value); return this; }
    public SceneBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { in.setOnTouchPressed(value); return this; }
    public SceneBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { in.setOnTouchMoved(value); return this; }
    public SceneBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { in.setOnTouchReleased(value); return this; }
    public SceneBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { in.setOnTouchStationary(value); return this; }
    public SceneBuilder onDragEntered(EventHandler<? super DragEvent> value) { in.setOnDragEntered(value); return this; }
    public SceneBuilder onDragExited(EventHandler<? super DragEvent> value) { in.setOnDragExited(value); return this; }
    public SceneBuilder onDragOver(EventHandler<? super DragEvent> value) { in.setOnDragOver(value); return this; }
    public SceneBuilder onDragDropped(EventHandler<? super DragEvent> value) { in.setOnDragDropped(value); return this; }
    public SceneBuilder onDragDone(EventHandler<? super DragEvent> value) { in.setOnDragDone(value); return this; }
    public SceneBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { in.setOnKeyPressed(value); return this; }
    public SceneBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { in.setOnKeyReleased(value); return this; }
    public SceneBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { in.setOnKeyTyped(value); return this; }
    public SceneBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { in.setOnInputMethodTextChanged(value); return this; }
    public SceneBuilder userData(Object value) { in.setUserData(value); return this; }
    public SceneBuilder nodeOrientation(NodeOrientation value) { in.setNodeOrientation(value); return this; }
}
