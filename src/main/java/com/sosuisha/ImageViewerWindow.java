package com.sosuisha;

import com.sosuisha.jfxbuilder.SceneBuilder;
import com.sosuisha.jfxbuilder.ImageViewBuilder;
import com.sosuisha.jfxbuilder.BorderPaneBuilder;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ImageViewerWindow {

    private double xOffset = 0;
    private double yOffset = 0;

    public ImageViewerWindow(File file) {
        var image = new Image(file.toURI().toString());

        var imageView = ImageViewBuilder.create(image)
                .image(image)
                .preserveRatio(true)
                .smooth(true)
                .build();

        var stage = new Stage(StageStyle.UNDECORATED); // ウィンドウ枠をなくす
        var root = BorderPaneBuilder.create()
                .style("-fx-background-color: black")
                .center(imageView)
                .onScroll(event -> {
                    // マウスホイールでウィンドウを拡大・縮小
                    double delta = event.getDeltaY(); // マウスホイールの回転量
                    double scaleFactor = (delta > 0) ? 1.05 : 0.95; // 拡大または縮小
                    stage.setWidth(stage.getWidth() * scaleFactor);
                    stage.setHeight(stage.getHeight() * scaleFactor);
                })
                .onMousePressed(event -> {
                    // マウスドラッグでウィンドウを移動                    
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                })
                .onMouseDragged(event -> {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                })
                .build();

        // 長辺を最大800に制限し、アスペクト比を維持してサイズを計算
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double maxDimension = 800;
        double scale = Math.min(maxDimension / imageWidth, maxDimension / imageHeight);
        double windowWidth = imageWidth * scale;
        double windowHeight = imageHeight * scale;

        var scene = SceneBuilder.create()
                .root(root)
                .width(windowWidth)
                .height(windowHeight)
                .apply(myScene -> {
                    // ウィンドウサイズ変更時にImageViewをリサイズ
                    myScene.widthProperty().addListener((obs, oldVal, newVal) -> {

                        imageView.setFitWidth(newVal.doubleValue());
                    });
                    myScene.heightProperty().addListener((obs, oldVal, newVal) -> {
                        imageView.setFitHeight(newVal.doubleValue());
                    });
                })
                .onKeyPressed(event -> {
                    // Escキーでウィンドウを閉じる
                    if (event.getCode() == KeyCode.ESCAPE) {
                        stage.close();
                    }
                })
                .build();

        stage.setScene(scene);
        stage.setTitle("Image: " + file.getName());
        stage.show();
    }
}