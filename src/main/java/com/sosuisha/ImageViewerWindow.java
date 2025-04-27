package com.sosuisha;

import java.io.File;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {

    private double xOffset = 0;
    private double yOffset = 0;

    public ImageViewerWindow(File file) {
        // 新しいウィンドウを作成して画像を表示
        Stage stage = new Stage(StageStyle.UNDECORATED); // ウィンドウ枠をなくす
        BorderPane root = new BorderPane();

        // 背景を黒に設定
        root.setStyle("-fx-background-color: black;");

        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        root.setCenter(imageView);

        // 画像の幅と高さを取得
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        // 長辺を最大800に制限し、アスペクト比を維持してサイズを計算
        double maxDimension = 800;
        double scale = Math.min(maxDimension / imageWidth, maxDimension / imageHeight);
        double windowWidth = imageWidth * scale;
        double windowHeight = imageHeight * scale;

        Scene scene = new Scene(root, windowWidth, windowHeight);

        // ウィンドウサイズ変更時にImageViewをリサイズ
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.doubleValue());
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitHeight(newVal.doubleValue());
        });

        // マウスホイールでウィンドウを拡大・縮小
        root.setOnScroll(event -> {
            double delta = event.getDeltaY(); // マウスホイールの回転量
            double scaleFactor = (delta > 0) ? 1.05 : 0.95; // 拡大または縮小
            stage.setWidth(stage.getWidth() * scaleFactor);
            stage.setHeight(stage.getHeight() * scaleFactor);
        });

        // Escキーでウィンドウを閉じる
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        // マウスドラッグでウィンドウを移動
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);
        stage.setTitle("画像: " + file.getName());
        stage.show();
    }
}