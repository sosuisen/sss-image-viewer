package com.sosuisha.imageviewer.view.jfxbuilder;
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
        if (in == null) build();
func.accept((Scene) in);
        return this;
    }
    public SceneBuilder camera(Camera value) { if (in == null) build(); in.setCamera(value); return this; }
    public SceneBuilder cursor(Cursor value) { if (in == null) build(); in.setCursor(value); return this; }
    public SceneBuilder userAgentStylesheet(String value) { if (in == null) build(); in.setUserAgentStylesheet(value); return this; }
    public SceneBuilder eventDispatcher(EventDispatcher value) { if (in == null) build(); in.setEventDispatcher(value); return this; }
    public SceneBuilder onContextMenuRequested(EventHandler<? super ContextMenuEvent> value) { if (in == null) build(); in.setOnContextMenuRequested(value); return this; }
    public SceneBuilder onMouseClicked(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseClicked(value); return this; }
    public SceneBuilder onMouseDragged(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseDragged(value); return this; }
    public SceneBuilder onMouseEntered(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseEntered(value); return this; }
    public SceneBuilder onMouseExited(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseExited(value); return this; }
    public SceneBuilder onMouseMoved(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseMoved(value); return this; }
    public SceneBuilder onMousePressed(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMousePressed(value); return this; }
    public SceneBuilder onMouseReleased(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnMouseReleased(value); return this; }
    public SceneBuilder onDragDetected(EventHandler<? super MouseEvent> value) { if (in == null) build(); in.setOnDragDetected(value); return this; }
    public SceneBuilder onMouseDragOver(EventHandler<? super MouseDragEvent> value) { if (in == null) build(); in.setOnMouseDragOver(value); return this; }
    public SceneBuilder onMouseDragReleased(EventHandler<? super MouseDragEvent> value) { if (in == null) build(); in.setOnMouseDragReleased(value); return this; }
    public SceneBuilder onMouseDragEntered(EventHandler<? super MouseDragEvent> value) { if (in == null) build(); in.setOnMouseDragEntered(value); return this; }
    public SceneBuilder onMouseDragExited(EventHandler<? super MouseDragEvent> value) { if (in == null) build(); in.setOnMouseDragExited(value); return this; }
    public SceneBuilder onScrollStarted(EventHandler<? super ScrollEvent> value) { if (in == null) build(); in.setOnScrollStarted(value); return this; }
    public SceneBuilder onScroll(EventHandler<? super ScrollEvent> value) { if (in == null) build(); in.setOnScroll(value); return this; }
    public SceneBuilder onScrollFinished(EventHandler<? super ScrollEvent> value) { if (in == null) build(); in.setOnScrollFinished(value); return this; }
    public SceneBuilder onRotationStarted(EventHandler<? super RotateEvent> value) { if (in == null) build(); in.setOnRotationStarted(value); return this; }
    public SceneBuilder onRotate(EventHandler<? super RotateEvent> value) { if (in == null) build(); in.setOnRotate(value); return this; }
    public SceneBuilder onRotationFinished(EventHandler<? super RotateEvent> value) { if (in == null) build(); in.setOnRotationFinished(value); return this; }
    public SceneBuilder onZoomStarted(EventHandler<? super ZoomEvent> value) { if (in == null) build(); in.setOnZoomStarted(value); return this; }
    public SceneBuilder onZoom(EventHandler<? super ZoomEvent> value) { if (in == null) build(); in.setOnZoom(value); return this; }
    public SceneBuilder onZoomFinished(EventHandler<? super ZoomEvent> value) { if (in == null) build(); in.setOnZoomFinished(value); return this; }
    public SceneBuilder onSwipeUp(EventHandler<? super SwipeEvent> value) { if (in == null) build(); in.setOnSwipeUp(value); return this; }
    public SceneBuilder onSwipeDown(EventHandler<? super SwipeEvent> value) { if (in == null) build(); in.setOnSwipeDown(value); return this; }
    public SceneBuilder onSwipeLeft(EventHandler<? super SwipeEvent> value) { if (in == null) build(); in.setOnSwipeLeft(value); return this; }
    public SceneBuilder onSwipeRight(EventHandler<? super SwipeEvent> value) { if (in == null) build(); in.setOnSwipeRight(value); return this; }
    public SceneBuilder onTouchPressed(EventHandler<? super TouchEvent> value) { if (in == null) build(); in.setOnTouchPressed(value); return this; }
    public SceneBuilder onTouchMoved(EventHandler<? super TouchEvent> value) { if (in == null) build(); in.setOnTouchMoved(value); return this; }
    public SceneBuilder onTouchReleased(EventHandler<? super TouchEvent> value) { if (in == null) build(); in.setOnTouchReleased(value); return this; }
    public SceneBuilder onTouchStationary(EventHandler<? super TouchEvent> value) { if (in == null) build(); in.setOnTouchStationary(value); return this; }
    public SceneBuilder onDragEntered(EventHandler<? super DragEvent> value) { if (in == null) build(); in.setOnDragEntered(value); return this; }
    public SceneBuilder onDragExited(EventHandler<? super DragEvent> value) { if (in == null) build(); in.setOnDragExited(value); return this; }
    public SceneBuilder onDragOver(EventHandler<? super DragEvent> value) { if (in == null) build(); in.setOnDragOver(value); return this; }
    public SceneBuilder onDragDropped(EventHandler<? super DragEvent> value) { if (in == null) build(); in.setOnDragDropped(value); return this; }
    public SceneBuilder onDragDone(EventHandler<? super DragEvent> value) { if (in == null) build(); in.setOnDragDone(value); return this; }
    public SceneBuilder onKeyPressed(EventHandler<? super KeyEvent> value) { if (in == null) build(); in.setOnKeyPressed(value); return this; }
    public SceneBuilder onKeyReleased(EventHandler<? super KeyEvent> value) { if (in == null) build(); in.setOnKeyReleased(value); return this; }
    public SceneBuilder onKeyTyped(EventHandler<? super KeyEvent> value) { if (in == null) build(); in.setOnKeyTyped(value); return this; }
    public SceneBuilder onInputMethodTextChanged(EventHandler<? super InputMethodEvent> value) { if (in == null) build(); in.setOnInputMethodTextChanged(value); return this; }
    public SceneBuilder userData(Object value) { if (in == null) build(); in.setUserData(value); return this; }
    public SceneBuilder nodeOrientation(NodeOrientation value) { if (in == null) build(); in.setNodeOrientation(value); return this; }
}
