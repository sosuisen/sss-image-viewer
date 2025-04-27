package com.sosuisha;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // コマンドライン引数を取得
        Parameters params = getParameters();
        String filePath = params.getUnnamed().isEmpty() ? null : params.getUnnamed().get(0);

        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                new ImageViewerWindow(file); // 指定された画像ファイルを開く
            } else {
                System.err.println("指定されたファイルが存在しないか、無効です: " + filePath);
            }
        } else {
            // 引数がない場合はドラッグ＆ドロップ可能なウィンドウを開く
            new DragAndDropWindow(stage);
        }
    }
}
