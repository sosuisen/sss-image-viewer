package com.sosuisha.imageviewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Parameters params = getParameters();
        String filePath = params.getUnnamed().isEmpty() ? null : params.getUnnamed().get(0);
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                new ImageViewerWindow(file, true);
            } else {
                System.err.println("File not found: " + filePath);
                showStartupErrorAndExit("Startup Error: ", new IllegalArgumentException("File not found: " + filePath), false);
                new DragAndDropWindow(stage);
            }
        } else {
            new DragAndDropWindow(stage);
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

    private void showStartupErrorAndExit(String message, Exception e, boolean canExit) {
        e.printStackTrace();

        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Startup Error");
            alert.setHeaderText("An error occurred during startup");
            alert.getDialogPane().setPrefWidth(480);
            alert.getDialogPane().setPrefHeight(320);
            
            alert.getDialogPane().setExpandableContent(new Label(message + ": " + e.getMessage()));
            alert.getDialogPane().setExpanded(true);
            if(canExit) {
                alert.setOnHidden(ev -> Platform.exit());
            }
            alert.show();
        });
    }
}
