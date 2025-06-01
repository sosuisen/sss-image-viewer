package com.sosuisha.imageviewer.jfxbuilder;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.*;
import javafx.util.*;
public class AlertBuilder {
    private Alert in;
    public static  AlertBuilder create(AlertType value) { return new AlertBuilder(value); }
    private AlertBuilder(AlertType value) { in = new Alert(value); }
    public static  AlertBuilder create(AlertType value1, String value2, ButtonType... value3) { return new AlertBuilder(value1, value2, value3); }
    private AlertBuilder(AlertType value1, String value2, ButtonType... value3) { in = new Alert(value1, value2, value3); }
    public Alert build() { return in; }
    public AlertBuilder apply(java.util.function.Consumer<Alert> func) {
        func.accept((Alert) in);
        return this;
    }
    public AlertBuilder alertType(AlertType value) { in.setAlertType(value); return this; }
    public AlertBuilder dialogPane(DialogPane value) { in.setDialogPane(value); return this; }
    public AlertBuilder contentText(String value) { in.setContentText(value); return this; }
    public AlertBuilder headerText(String value) { in.setHeaderText(value); return this; }
    public AlertBuilder graphic(Node value) { in.setGraphic(value); return this; }
    public AlertBuilder result(ButtonType value) { in.setResult(value); return this; }
    public AlertBuilder resultConverter(Callback<ButtonType,ButtonType> value) { in.setResultConverter(value); return this; }
    public AlertBuilder resizable(boolean value) { in.setResizable(value); return this; }
    public AlertBuilder width(double value) { in.setWidth(value); return this; }
    public AlertBuilder height(double value) { in.setHeight(value); return this; }
    public AlertBuilder title(String value) { in.setTitle(value); return this; }
    public AlertBuilder x(double value) { in.setX(value); return this; }
    public AlertBuilder y(double value) { in.setY(value); return this; }
    public AlertBuilder onShowing(EventHandler<DialogEvent> value) { in.setOnShowing(value); return this; }
    public AlertBuilder onShown(EventHandler<DialogEvent> value) { in.setOnShown(value); return this; }
    public AlertBuilder onHiding(EventHandler<DialogEvent> value) { in.setOnHiding(value); return this; }
    public AlertBuilder onHidden(EventHandler<DialogEvent> value) { in.setOnHidden(value); return this; }
    public AlertBuilder onCloseRequest(EventHandler<DialogEvent> value) { in.setOnCloseRequest(value); return this; }
}
