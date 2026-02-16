package com.sosuisha.imageviewer;

import com.sosuisha.imageviewer.view.DragAndDropWindow;
import com.sosuisha.imageviewer.view.ImageViewerWindow;
import com.sosuisha.imageviewer.view.jfxbuilder.TextInputDialogBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.Consumer;

public class App extends Application {
    private final SingleInstanceService singleInstanceService = new SingleInstanceService();
    private final TrayIconService trayIconService = new TrayIconService();

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(false);

        // for benchmark: Automatically exit after opening the file to measure startup
        // time without user interaction
        if ("true".equals(System.getProperty("auto.exit"))) {
            Platform.exit();
        }

        trayIconService.install();

        try {
            singleInstanceService.startServer(
                    filePath -> Platform.runLater(() -> handleIncomingFile(filePath)));
        } catch (Exception e) {
            System.err.println("Failed to start single-instance server: " + e.getMessage());
        }

        var params = getParameters();
        var filePath = params.getUnnamed().isEmpty() ? null : params.getUnnamed().get(0);
        openFile(stage, filePath, false);
    }

    @Override
    public void stop() {
        trayIconService.uninstall();
        singleInstanceService.stopServer();
    }

    public static void main(String[] args) throws Throwable {
        args = UnicodeArgs.resolveArgs(args);

        String filePath = args.length > 0 ? args[0] : null;
        if (SingleInstanceService.trySendToExisting(filePath)) {
            System.exit(0);
        }

        Application.launch(App.class, args);
    }

    private void handleIncomingFile(String filePath) {
        if (filePath != null) {
            var file = new File(filePath);
            if (file.exists() && file.isFile()) {
                new ImageViewerWindow(file, true);
            }
        } else {
            new DragAndDropWindow(new Stage());
        }
    }

    private void openFile(Stage stage, String filePath, boolean fileNameWillBeChanged) {
        if (filePath == null) {
            new DragAndDropWindow(stage);
            return;
        }

        var file = new File(filePath);
        if (file.exists() && file.isFile()) {
            new ImageViewerWindow(file, true);

            if (fileNameWillBeChanged) {
                Platform.runLater(() -> {
                    TextInputDialogBuilder.create(file.getName())
                            .title("Change Image Name")
                            .headerText("Please input a new image name.")
                            .contentText("New Name")
                            .build()
                            .showAndWait().ifPresent(newFileName -> {
                                if (newFileName.isEmpty()) {
                                    System.err.println("File name cannot be empty.");
                                    return;
                                }
                                File newFile = new File(file.getParent(), newFileName);
                                if (file.renameTo(newFile)) {
                                    System.out.println("File renamed to: " + newFile.getAbsolutePath());
                                } else {
                                    System.err.println("Failed to rename file.");
                                }
                            });
                });
            }
        } else {
            showFileNotFoundDialog(filePath,
                    newPath -> {
                        openFile(stage, newPath, true);
                    },
                    () -> {
                        new DragAndDropWindow(stage);
                    });
        }
    }

    private void showFileNotFoundDialog(String filePath, Consumer<String> onOkAction, Runnable onCancelAction) {
        System.err.println("File not found: " + filePath);
        Platform.runLater(() -> {
            var directory = new File(filePath).getParent();
            var fileName = new File(filePath).getName();
            TextInputDialogBuilder.create(fileName)
                    .title("File Not Found")
                    .headerText("Please input a valid image name.")
                    .contentText("Name")
                    .build()
                    .showAndWait()
                    .ifPresent(
                            inputName -> {
                                if (!inputName.isEmpty()) {
                                    onOkAction.accept(directory + File.separator + inputName);
                                } else {
                                    onCancelAction.run();
                                }
                            });
        });

    }
}
