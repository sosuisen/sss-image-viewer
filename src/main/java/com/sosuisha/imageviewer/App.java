package com.sosuisha.imageviewer;

import javafx.application.Application;
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
            }
        } else {
            new DragAndDropWindow(stage);
        }
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
