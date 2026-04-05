package com.sosuisha.imageviewer.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sosuisha.imageviewer.GridImageEntry;
import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.MarkPersistenceService;
import com.sosuisha.imageviewer.SharedMarkManager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MarkedImagesGridWindow {

    private final List<TreemapEntry> entries = new ArrayList<>();
    private String sessionId;
    private double areaW;
    private double areaH;

    private static class TreemapEntry {
        final File file;
        final Image image;
        final double initialWeight;
        final double imageScale;
        final int markOrder;
        double weight;
        StackPane cell;
        ImageView imageView;

        TreemapEntry(File file, Image image, double weight, double imageScale, int markOrder) {
            this.file = file;
            this.image = image;
            this.initialWeight = weight;
            this.imageScale = imageScale;
            this.markOrder = markOrder;
            this.weight = weight;
        }
    }

    private record Rect(double x, double y, double w, double h) {}

    /**
     * Opens a grid window for the given marked images with default scales.
     *
     * @param markedImages list of marked image files
     */
    public MarkedImagesGridWindow(List<File> markedImages) {
        if (markedImages.isEmpty()) {
            return;
        }

        int order = 0;
        for (File file : markedImages) {
            Image originalImage = ImageService.getInstance().getImageFromFile(file);
            double rotation = ImageService.getInstance().getRotationForFile(file);
            Image displayImage = ImageService.createRotatedImage(originalImage, rotation);
            double weight = displayImage.getWidth() * displayImage.getHeight();
            entries.add(new TreemapEntry(file, displayImage, weight, 1.0, order++));
        }

        showGrid();
    }

    /**
     * Opens a grid window restoring saved scales from a history session.
     *
     * @param gridImageEntries list of grid image entries with saved scale data
     * @param sessionId        the existing session ID to overwrite on save
     * @throws NullPointerException if sessionId is null
     */
    public MarkedImagesGridWindow(List<GridImageEntry> gridImageEntries, String sessionId) {
        this.sessionId = java.util.Objects.requireNonNull(sessionId, "sessionId must not be null");
        if (gridImageEntries.isEmpty()) {
            return;
        }

        int order = 0;
        for (var ge : gridImageEntries) {
            Image originalImage = ImageService.getInstance().getImageFromFile(ge.file());
            double rotation = ImageService.getInstance().getRotationForFile(ge.file());
            Image displayImage = ImageService.createRotatedImage(originalImage, rotation);
            double baseWeight = displayImage.getWidth() * displayImage.getHeight();
            double weight = baseWeight * ge.frameScale();
            var entry = new TreemapEntry(ge.file(), displayImage, baseWeight, ge.imageScale(), order++);
            entry.weight = weight;
            entries.add(entry);
        }

        showGrid();
    }

    private void showGrid() {
        // Sort once by weight descending for initial treemap layout
        entries.sort((a, b) -> Double.compare(b.weight, a.weight));

        Pane container = new Pane();
        container.setStyle("-fx-background-color: black");

        Stage stage = new Stage(StageStyle.UNDECORATED);
        Scene scene = new Scene(container);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.G) {
                stage.close();
            }
        });

        stage.setOnHidden(_ -> {
            saveGridEntries();
            if (sessionId != null) {
                SharedMarkManager.getInstance().getMarkedImages().clear();
            }
        });
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        var screenBounds = Screen.getPrimary().getVisualBounds();
        areaW = screenBounds.getWidth();
        areaH = screenBounds.getHeight();

        for (TreemapEntry entry : entries) {
            ImageView imageView = new ImageView(entry.image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setScaleX(entry.imageScale);
            imageView.setScaleY(entry.imageScale);

            StackPane cell = new StackPane(imageView);
            cell.setAlignment(Pos.CENTER);

            entry.cell = cell;
            entry.imageView = imageView;

            cell.setOnScroll(e -> {
                double scaleFactor = e.getDeltaY() > 0 ? 1.1 : 0.9;
                if (e.isControlDown()) {
                    entry.weight *= scaleFactor;
                    relayout();
                } else {
                    imageView.setScaleX(imageView.getScaleX() * scaleFactor);
                    imageView.setScaleY(imageView.getScaleY() * scaleFactor);
                }
                e.consume();
            });

            final double[] dragStart = new double[2];
            cell.setOnMousePressed(e -> {
                dragStart[0] = e.getX() - imageView.getTranslateX();
                dragStart[1] = e.getY() - imageView.getTranslateY();
                e.consume();
            });
            cell.setOnMouseDragged(e -> {
                imageView.setTranslateX(e.getX() - dragStart[0]);
                imageView.setTranslateY(e.getY() - dragStart[1]);
                e.consume();
            });

            container.getChildren().add(cell);
        }

        relayout();

        stage.toFront();
        stage.requestFocus();
    }

    private void saveGridEntries() {
        var gridEntries = new ArrayList<GridImageEntry>();
        for (var entry : entries) {
            double imageScale = entry.imageView.getScaleX();
            double frameScale = entry.weight / entry.initialWeight;
            gridEntries.add(new GridImageEntry(entry.file, entry.markOrder, imageScale, frameScale));
        }
        var service = MarkPersistenceService.getInstance();
        if (sessionId != null) {
            service.deleteSession(sessionId);
        }
        service.saveGridEntries(gridEntries, sessionId);
    }

    private void relayout() {
        double totalWeight = entries.stream().mapToDouble(e -> e.weight).sum();
        double totalArea = areaW * areaH;
        List<Double> normalizedAreas = new ArrayList<>();
        for (TreemapEntry entry : entries) {
            normalizedAreas.add(entry.weight / totalWeight * totalArea);
        }

        List<Rect> rects = computeTreemap(normalizedAreas, new Rect(0, 0, areaW, areaH));

        for (int i = 0; i < entries.size(); i++) {
            TreemapEntry entry = entries.get(i);
            Rect rect = rects.get(i);

            entry.imageView.setFitWidth(rect.w);
            entry.imageView.setFitHeight(rect.h);
            entry.cell.setLayoutX(rect.x);
            entry.cell.setLayoutY(rect.y);
            entry.cell.setPrefSize(rect.w, rect.h);
            entry.cell.setClip(new Rectangle(rect.w, rect.h));
        }
    }

    // ========== Squarified Treemap Algorithm ==========

    private List<Rect> computeTreemap(List<Double> areas, Rect bounds) {
        List<Rect> result = new ArrayList<>();
        squarify(new ArrayList<>(areas), new ArrayList<>(), bounds, result);
        return result;
    }

    private void squarify(List<Double> remaining, List<Double> currentRow,
            Rect bounds, List<Rect> result) {
        if (remaining.isEmpty()) {
            layoutRow(currentRow, bounds, result);
            return;
        }

        if (currentRow.isEmpty()) {
            currentRow.add(remaining.remove(0));
            squarify(remaining, currentRow, bounds, result);
            return;
        }

        double w = Math.min(bounds.w, bounds.h);

        List<Double> withNext = new ArrayList<>(currentRow);
        withNext.add(remaining.get(0));

        if (worstAspectRatio(withNext, w) <= worstAspectRatio(currentRow, w)) {
            currentRow.add(remaining.remove(0));
            squarify(remaining, currentRow, bounds, result);
        } else {
            Rect remainingBounds = layoutRow(currentRow, bounds, result);
            squarify(remaining, new ArrayList<>(), remainingBounds, result);
        }
    }

    private double worstAspectRatio(List<Double> row, double w) {
        double s = row.stream().mapToDouble(Double::doubleValue).sum();
        double worst = 0;
        for (double r : row) {
            double ratio = Math.max(w * w * r / (s * s), s * s / (w * w * r));
            worst = Math.max(worst, ratio);
        }
        return worst;
    }

    private Rect layoutRow(List<Double> row, Rect bounds, List<Rect> result) {
        if (row.isEmpty()) {
            return bounds;
        }

        double sum = row.stream().mapToDouble(Double::doubleValue).sum();

        if (bounds.w >= bounds.h) {
            double stripWidth = sum / bounds.h;
            double y = bounds.y;
            for (double area : row) {
                double itemHeight = area / stripWidth;
                result.add(new Rect(bounds.x, y, stripWidth, itemHeight));
                y += itemHeight;
            }
            return new Rect(bounds.x + stripWidth, bounds.y,
                    bounds.w - stripWidth, bounds.h);
        } else {
            double stripHeight = sum / bounds.w;
            double x = bounds.x;
            for (double area : row) {
                double itemWidth = area / stripHeight;
                result.add(new Rect(x, bounds.y, itemWidth, stripHeight));
                x += itemWidth;
            }
            return new Rect(bounds.x, bounds.y + stripHeight,
                    bounds.w, bounds.h - stripHeight);
        }
    }
}
