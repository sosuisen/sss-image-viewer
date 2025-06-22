package com.sosuisha.imageviewer.view;

import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;

import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.view.jfxbuilder.BorderPaneBuilder;

public class DragAndDropWindow {

    public DragAndDropWindow(Stage stage) {
        var root = BorderPaneBuilder.create()
                .style("-fx-background-color: black")
                .onDragOver(event -> {
                    if (event.getDragboard().hasFiles()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                })
                .onDragDropped(event -> {
                    Dragboard dragboard = event.getDragboard();
                    if (dragboard.hasFiles()) {
                        File file = dragboard.getFiles().get(0);
                        if (ImageService.isImageFile(file)) {
                            new ImageViewerWindow(file, true);
                        }
                    }
                    event.setDropCompleted(true);
                    event.consume();
                })
                .build();

        var scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Drag & drop an image file here.");
        stage.show();
    }


}