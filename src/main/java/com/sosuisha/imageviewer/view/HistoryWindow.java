package com.sosuisha.imageviewer.view;

import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.MarkPersistenceService;
import com.sosuisha.imageviewer.SessionEntry;
import com.sosuisha.imageviewer.SharedMarkManager;

import io.github.sosuisen.jfxbuilder.controls.ButtonBuilder;
import io.github.sosuisen.jfxbuilder.controls.TableColumnBuilder;
import io.github.sosuisen.jfxbuilder.controls.TableViewBuilder;
import io.github.sosuisen.jfxbuilder.graphics.HBoxBuilder;
import io.github.sosuisen.jfxbuilder.graphics.SceneBuilder;
import io.github.sosuisen.jfxbuilder.graphics.StageBuilder;
import io.github.sosuisen.jfxbuilder.graphics.VBoxBuilder;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays the history of saved grid sessions in a TableView.
 * Each row is one session with its thumbnails in ascending mark order.
 */
public class HistoryWindow {

    /**
     * Opens the history window and loads sessions from the database.
     */
    public HistoryWindow() {
        var items = FXCollections.observableArrayList(
                MarkPersistenceService.getInstance().loadSessionSummaries());

        var thumbnailsCol = TableColumnBuilder.<SessionEntry, Void>create("Images")
                .cellFactory(_ -> new SessionThumbnailsCell())
                .prefWidth(500)
                .sortable(false)
                .build();

        var dateCol = TableColumnBuilder.<SessionEntry, String>create("Date")
                .cellValueFactory(cd -> new SimpleStringProperty(cd.getValue().savedAt()))
                .prefWidth(160)
                .build();

        var deleteCol = TableColumnBuilder.<SessionEntry, Void>create("")
                .cellFactory(_ -> new DeleteButtonCell())
                .prefWidth(100)
                .sortable(false)
                .build();

        var tableView = TableViewBuilder.<SessionEntry>create(items)
                .addColumns(thumbnailsCol, dateCol, deleteCol)
                .apply(tv -> {
                    tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
                    tv.setFixedCellSize(THUMBNAIL_SIZE + 8);
                })
                .rowFactory(_ -> {
                    var row = new TableRow<SessionEntry>();
                    row.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2 && !row.isEmpty()) {
                            openSession(row.getItem());
                        }
                    });
                    return row;
                })
                .build();

        var openGridButton = ButtonBuilder.create("Open session")
                .onAction(_ -> {
                    var selected = tableView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        openSession(selected);
                    }
                })
                .build();

        var buttonBar = HBoxBuilder.withChildren(openGridButton)
                .padding(new Insets(4, 0, 0, 0))
                .alignment(Pos.CENTER_RIGHT)
                .build();

        var root = VBoxBuilder.withChildren(tableView, buttonBar)
                .padding(new Insets(8))
                .style("-fx-background-color: #2b2b2b;")
                .apply(vbox -> javafx.scene.layout.VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS))
                .build();

        Runnable reloadListener = () -> Platform.runLater(() -> {
            tableView.getItems().setAll(MarkPersistenceService.getInstance().loadSessionSummaries());
        });
        var service = MarkPersistenceService.getInstance();
        service.addChangeListener(reloadListener);

        StageBuilder.withScene(
                SceneBuilder.withRoot(root)
                        .width(800)
                        .height(500)
                        .build())
                .title("History")
                .apply(stage -> stage.setOnHidden(_ -> service.removeChangeListener(reloadListener)))
                .build()
                .show();
    }

    private void openSession(SessionEntry session) {
        var sessionEntries = MarkPersistenceService.getInstance().loadSession(session.sessionId());
        if (sessionEntries.isEmpty()) {
            return;
        }

        var markManager = SharedMarkManager.getInstance();
        for (var entry : sessionEntries) {
            if (!markManager.getMarkedImages().contains(entry.file())) {
                markManager.toggleMark(entry.file());
            }
        }

        new MarkedImagesGridWindow(sessionEntries, session.sessionId());
    }

    private static final double THUMBNAIL_SIZE = 40;

    private static class SessionThumbnailsCell extends TableCell<SessionEntry, Void> {
        private final HBox box = new HBox(2);

        SessionThumbnailsCell() {
            box.setAlignment(Pos.CENTER_LEFT);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                setGraphic(null);
            } else {
                var session = getTableView().getItems().get(getIndex());
                box.getChildren().clear();
                for (var filePath : session.filePaths()) {
                    var file = new File(filePath);
                    if (!file.exists()) {
                        continue;
                    }
                    var image = ImageService.getInstance().getThumbnailFromFile(file, THUMBNAIL_SIZE);
                    var imageView = new ImageView(image);
                    imageView.setFitWidth(THUMBNAIL_SIZE);
                    imageView.setFitHeight(THUMBNAIL_SIZE);
                    imageView.setPreserveRatio(true);
                    box.getChildren().add(imageView);
                }
                setGraphic(box);
                setAlignment(Pos.CENTER_LEFT);
            }
        }
    }

    private static class DeleteButtonCell extends TableCell<SessionEntry, Void> {
        private final Button button = new Button("Delete");

        DeleteButtonCell() {
            button.setOnAction(_ -> {
                var session = getTableView().getItems().get(getIndex());
                var alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete this session?\n" + session.savedAt());
                var result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    MarkPersistenceService.getInstance().deleteSessionAndThumbnails(session.sessionId());
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : button);
            setAlignment(Pos.CENTER);
        }
    }
}
