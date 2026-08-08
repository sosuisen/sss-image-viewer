package com.sosuisha.imageviewer.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sosuisha.imageviewer.GridImageEntry;
import com.sosuisha.imageviewer.ImageService;
import com.sosuisha.imageviewer.MarkPersistenceService;
import com.sosuisha.imageviewer.SharedMarkManager;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Window;

public class MarkedImagesGridWindow {

    private final List<TreemapEntry> entries = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();
    private String sessionId;
    private boolean closed = false;

    private static class TreemapEntry {
        final File file;
        final Image image;
        final double initialWeight;
        final double imageScale;
        final int markOrder;
        double weight;
        double offsetX;
        double offsetY;
        Screen screen;
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
     * Opens grid windows for the given marked images with default scales.
     * The images are grouped by the display where each image was marked,
     * and one maximized grid window is opened on each of those displays.
     *
     * @param markedImages list of marked image files
     */
    public MarkedImagesGridWindow(List<File> markedImages) {
        if (markedImages.isEmpty()) {
            return;
        }

        int order = 0;
        for (File file : markedImages) {
            entries.add(createEntry(file, order++));
        }

        showGrids(groupEntriesByScreen());
    }

    /**
     * Opens grid windows restoring saved scales and display layout from a
     * history session. Each entry is shown on the display whose top-left corner
     * is nearest to the saved screen position, so the original layout is
     * reproduced when the display configuration is unchanged.
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

        var groups = new LinkedHashMap<Screen, List<TreemapEntry>>();
        int order = 0;
        for (var ge : gridImageEntries) {
            Image originalImage = ImageService.getInstance().getImageFromFile(ge.file());
            double rotation = ImageService.getInstance().getRotationForFile(ge.file());
            Image displayImage = ImageService.createRotatedImage(originalImage, rotation);
            double baseWeight = displayImage.getWidth() * displayImage.getHeight();
            var entry = new TreemapEntry(ge.file(), displayImage, baseWeight, ge.imageScale(), order++);
            entry.weight = baseWeight * ge.frameScale();
            entry.offsetX = ge.offsetX();
            entry.offsetY = ge.offsetY();
            entry.screen = resolveScreenForPosition(ge.screenX(), ge.screenY());
            entries.add(entry);
            groups.computeIfAbsent(entry.screen, _ -> new ArrayList<>()).add(entry);
        }

        showGrids(groups);
    }

    private TreemapEntry createEntry(File file, int markOrder) {
        Image originalImage = ImageService.getInstance().getImageFromFile(file);
        double rotation = ImageService.getInstance().getRotationForFile(file);
        Image displayImage = ImageService.createRotatedImage(originalImage, rotation);
        double weight = displayImage.getWidth() * displayImage.getHeight();
        return new TreemapEntry(file, displayImage, weight, 1.0, markOrder);
    }

    private Map<Screen, List<TreemapEntry>> groupEntriesByScreen() {
        var groups = new LinkedHashMap<Screen, List<TreemapEntry>>();
        for (TreemapEntry entry : entries) {
            entry.screen = resolveScreenForFile(entry.file);
            groups.computeIfAbsent(entry.screen, _ -> new ArrayList<>()).add(entry);
        }
        return groups;
    }

    private Screen resolveScreenForFile(File file) {
        Window origin = SharedMarkManager.getInstance().getMarkOrigin(file);
        if (origin == null || !origin.isShowing()) {
            return Screen.getPrimary();
        }
        var screens = Screen.getScreensForRectangle(origin.getX(), origin.getY(),
                origin.getWidth(), origin.getHeight());
        return screens.isEmpty() ? Screen.getPrimary() : screens.get(0);
    }

    private Screen resolveScreenForPosition(double screenX, double screenY) {
        // Pick the screen whose top-left corner is nearest to the saved position.
        // Rectangle2D.contains() cannot be used here: it includes the max edges,
        // so the top-left corner of a screen also matches the screen on its left,
        // which comes earlier in Screen.getScreens().
        Screen nearest = Screen.getPrimary();
        double nearestDistance = Double.MAX_VALUE;
        for (Screen screen : Screen.getScreens()) {
            var bounds = screen.getBounds();
            double dx = bounds.getMinX() - screenX;
            double dy = bounds.getMinY() - screenY;
            double distance = dx * dx + dy * dy;
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = screen;
            }
        }
        return nearest;
    }

    private void showGrids(Map<Screen, List<TreemapEntry>> groups) {
        for (var group : groups.entrySet()) {
            // Arrange by ascending mark index within each display
            List<TreemapEntry> screenEntries = group.getValue();
            screenEntries.sort(Comparator.comparingInt(e -> e.markOrder));
            showGridOnScreen(group.getKey(), screenEntries);
        }
    }

    private void showGridOnScreen(Screen screen, List<TreemapEntry> screenEntries) {
        Pane container = new Pane();
        container.setStyle("-fx-background-color: black");

        Stage stage = new Stage(StageStyle.UNDECORATED);
        Scene scene = new Scene(container);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.G) {
                stage.close();
            }
        });

        stage.setOnHidden(_ -> onGridClosed());
        stage.setScene(scene);

        Rectangle2D screenBounds = screen.getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.show();

        double areaW = screenBounds.getWidth();
        double areaH = screenBounds.getHeight();

        for (TreemapEntry entry : screenEntries) {
            ImageView imageView = new ImageView(entry.image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setScaleX(entry.imageScale);
            imageView.setScaleY(entry.imageScale);
            imageView.setTranslateX(entry.offsetX);
            imageView.setTranslateY(entry.offsetY);

            StackPane cell = new StackPane(imageView);
            cell.setAlignment(Pos.CENTER);

            entry.cell = cell;
            entry.imageView = imageView;

            cell.setOnScroll(e -> {
                double scaleFactor = e.getDeltaY() > 0 ? 1.1 : 0.9;
                if (e.isControlDown()) {
                    entry.weight *= scaleFactor;
                    relayout(screenEntries, areaW, areaH);
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

        relayout(screenEntries, areaW, areaH);

        stage.toFront();
        stage.requestFocus();

        stages.add(stage);
    }

    private void onGridClosed() {
        if (closed) {
            return;
        }
        closed = true;

        saveGridEntries();
        if (sessionId != null) {
            SharedMarkManager.getInstance().clearMarks();
        }

        // Closing one grid window closes the grid windows on all displays
        for (Stage stage : new ArrayList<>(stages)) {
            if (stage.isShowing()) {
                stage.close();
            }
        }
    }

    private void saveGridEntries() {
        var gridEntries = new ArrayList<GridImageEntry>();
        for (var entry : entries) {
            double imageScale = entry.imageView.getScaleX();
            double frameScale = entry.weight / entry.initialWeight;
            Screen screen = entry.screen != null ? entry.screen : Screen.getPrimary();
            var screenBounds = screen.getBounds();
            gridEntries.add(new GridImageEntry(entry.file, entry.markOrder, imageScale, frameScale,
                    screenBounds.getMinX(), screenBounds.getMinY(),
                    entry.imageView.getTranslateX(), entry.imageView.getTranslateY()));
        }
        var service = MarkPersistenceService.getInstance();
        if (sessionId != null) {
            service.deleteSession(sessionId);
        }
        service.saveGridEntries(gridEntries, sessionId);
    }

    private void relayout(List<TreemapEntry> screenEntries, double areaW, double areaH) {
        double totalWeight = screenEntries.stream().mapToDouble(e -> e.weight).sum();
        double totalArea = areaW * areaH;
        List<Double> normalizedAreas = new ArrayList<>();
        for (TreemapEntry entry : screenEntries) {
            normalizedAreas.add(entry.weight / totalWeight * totalArea);
        }

        List<Rect> rects = computeTreemap(normalizedAreas, new Rect(0, 0, areaW, areaH));

        for (int i = 0; i < screenEntries.size(); i++) {
            TreemapEntry entry = screenEntries.get(i);
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
