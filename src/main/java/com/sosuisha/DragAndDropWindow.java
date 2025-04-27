package com.sosuisha;

import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

public class DragAndDropWindow {

    public DragAndDropWindow(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // ドラッグ＆ドロップの設定
        root.setOnDragOver(event -> {
            if (event.getGestureSource() != root && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        root.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                File file = dragboard.getFiles().get(0);
                if (isImageFile(file)) {
                    new ImageViewerWindow(file); // ドロップされた画像を新しいウィンドウで開く
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("ドラッグ＆ドロップで画像を開く");
        stage.show();
    }

    private boolean isImageFile(File file) {
        String lowerCaseName = file.getName().toLowerCase();
        return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg") ||
               lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".gif") ||
               lowerCaseName.endsWith(".bmp");
    }
}