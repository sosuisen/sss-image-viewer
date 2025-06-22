package com.sosuisha.imageviewer.view.jfxbuilder;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.event.*;
import javafx.util.*;
public class TextInputDialogBuilder {
    private TextInputDialog in;
    public static  TextInputDialogBuilder create() { return new TextInputDialogBuilder(); }
    private TextInputDialogBuilder() { in = new TextInputDialog(); }
    public static  TextInputDialogBuilder create(String value) { return new TextInputDialogBuilder(value); }
    private TextInputDialogBuilder(String value) { in = new TextInputDialog(value); }
    public TextInputDialog build() { return in; }
    public TextInputDialogBuilder apply(java.util.function.Consumer<TextInputDialog> func) {
        func.accept((TextInputDialog) in);
        return this;
    }
    public TextInputDialogBuilder dialogPane(DialogPane value) { in.setDialogPane(value); return this; }
    public TextInputDialogBuilder contentText(String value) { in.setContentText(value); return this; }
    public TextInputDialogBuilder headerText(String value) { in.setHeaderText(value); return this; }
    public TextInputDialogBuilder graphic(Node value) { in.setGraphic(value); return this; }
    public TextInputDialogBuilder result(String value) { in.setResult(value); return this; }
    public TextInputDialogBuilder resultConverter(Callback<ButtonType,String> value) { in.setResultConverter(value); return this; }
    public TextInputDialogBuilder resizable(boolean value) { in.setResizable(value); return this; }
    public TextInputDialogBuilder width(double value) { in.setWidth(value); return this; }
    public TextInputDialogBuilder height(double value) { in.setHeight(value); return this; }
    public TextInputDialogBuilder title(String value) { in.setTitle(value); return this; }
    public TextInputDialogBuilder x(double value) { in.setX(value); return this; }
    public TextInputDialogBuilder y(double value) { in.setY(value); return this; }
    public TextInputDialogBuilder onShowing(EventHandler<DialogEvent> value) { in.setOnShowing(value); return this; }
    public TextInputDialogBuilder onShown(EventHandler<DialogEvent> value) { in.setOnShown(value); return this; }
    public TextInputDialogBuilder onHiding(EventHandler<DialogEvent> value) { in.setOnHiding(value); return this; }
    public TextInputDialogBuilder onHidden(EventHandler<DialogEvent> value) { in.setOnHidden(value); return this; }
    public TextInputDialogBuilder onCloseRequest(EventHandler<DialogEvent> value) { in.setOnCloseRequest(value); return this; }
}
