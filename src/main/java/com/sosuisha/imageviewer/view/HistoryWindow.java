package com.sosuisha.imageviewer.view;

import com.sosuisha.imageviewer.HistoryEntry;
import com.sosuisha.imageviewer.MarkPersistenceService;
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Displays the history of marked image sessions in a TableView.
 */
public class HistoryWindow {

    /**
     * Opens the history window and loads entries from the database.
     */
    public HistoryWindow() {
        var entries = MarkPersistenceService.getInstance().loadHistory();
        var items = FXCollections.observableArrayList(entries);

        var thumbnailCol = TableColumnBuilder.<HistoryEntry, Void>create("")
                .cellFactory(_ -> new ThumbnailCell())
                .prefWidth(60)
                .sortable(false)
                .build();

        var sessionCol = TableColumnBuilder.<HistoryEntry, String>create("Session ID")
                .cellValueFactory(cd -> new SimpleStringProperty(cd.getValue().sessionId()))
                .prefWidth(140)
                .build();

        var fileCol = TableColumnBuilder.<HistoryEntry, String>create("File Name")
                .cellValueFactory(cd -> new SimpleStringProperty(cd.getValue().fileName()))
                .prefWidth(300)
                .build();

        var orderCol = TableColumnBuilder.<HistoryEntry, Number>create("Order")
                .cellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().markOrder()))
                .prefWidth(60)
                .build();

        var imageScaleCol = TableColumnBuilder.<HistoryEntry, Number>create("Image Scale")
                .cellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().imageScale()))
                .prefWidth(100)
                .build();

        var frameScaleCol = TableColumnBuilder.<HistoryEntry, Number>create("Frame Scale")
                .cellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().frameScale()))
                .prefWidth(100)
                .build();

        var dateCol = TableColumnBuilder.<HistoryEntry, String>create("Date")
                .cellValueFactory(cd -> new SimpleStringProperty(cd.getValue().savedAt()))
                .prefWidth(180)
                .build();

        var deleteCol = TableColumnBuilder.<HistoryEntry, Void>create("")
                .cellFactory(_ -> new DeleteButtonCell())
                .prefWidth(120)
                .sortable(false)
                .build();

        var tableView = TableViewBuilder.<HistoryEntry>create(items)
                .addColumns(thumbnailCol, sessionCol, fileCol, orderCol, imageScaleCol, frameScaleCol, dateCol, deleteCol)
                .apply(tv -> {
                    tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
                    tv.setFixedCellSize(THUMBNAIL_SIZE + 8);
                })
                .rowFactory(_ -> {
                    var row = new TableRow<HistoryEntry>();
                    row.setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2 && !row.isEmpty()) {
                            var file = new File(row.getItem().filePath());
                            if (file.exists()) {
                                new ImageViewerWindow(file, false);
                            }
                        }
                    });
                    return row;
                })
                .build();

        var openGridButton = ButtonBuilder.create("Open session")
                .onAction(_ -> openGridFromSelection(tableView))
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
            tableView.getItems().setAll(MarkPersistenceService.getInstance().loadHistory());
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

    private void openGridFromSelection(TableView<HistoryEntry> tableView) {
        var selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        var sessionEntries = MarkPersistenceService.getInstance().loadSession(selected.sessionId());
        if (sessionEntries.isEmpty()) {
            return;
        }

        var markManager = SharedMarkManager.getInstance();
        for (var entry : sessionEntries) {
            if (!markManager.getMarkedImages().contains(entry.file())) {
                markManager.toggleMark(entry.file());
            }
        }

        new MarkedImagesGridWindow(sessionEntries, selected.sessionId());
    }

    private static final double THUMBNAIL_SIZE = 40;

    private static class ThumbnailCell extends TableCell<HistoryEntry, Void> {
        private final ImageView imageView = new ImageView();

        ThumbnailCell() {
            imageView.setFitWidth(THUMBNAIL_SIZE);
            imageView.setFitHeight(THUMBNAIL_SIZE);
            imageView.setPreserveRatio(true);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                setGraphic(null);
            } else {
                var entry = getTableView().getItems().get(getIndex());
                var file = new File(entry.filePath());
                if (file.exists()) {
                    var image = new Image(file.toURI().toString(), THUMBNAIL_SIZE, THUMBNAIL_SIZE, true, true, true);
                    imageView.setImage(image);
                } else {
                    imageView.setImage(null);
                }
                setGraphic(imageView);
                setAlignment(Pos.CENTER);
            }
        }
    }

    private static class DeleteButtonCell extends TableCell<HistoryEntry, Void> {
        private final Button button = new Button("Delete");

        DeleteButtonCell() {
            button.setOnAction(_ -> {
                var entry = getTableView().getItems().get(getIndex());
                var alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete this session?\n" + entry.sessionId());
                var result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    MarkPersistenceService.getInstance().deleteSession(entry.sessionId());
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
