package com.sosuisha.imageviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Window;

public enum SharedMarkManager {
    INSTANCE;

    private final ObservableList<File> markedImages = FXCollections.observableArrayList();
    private final Map<String, Set<File>> windowFileRegistry = new HashMap<>();
    private final Map<File, Window> markOrigins = new HashMap<>();

    public static SharedMarkManager getInstance() {
        return INSTANCE;
    }

    public ObservableList<File> getMarkedImages() {
        return markedImages;
    }

    /**
     * Toggles the mark state of the given file without recording an origin window.
     *
     * @param file the file to toggle (ignored if null)
     */
    public void toggleMark(File file) {
        toggleMark(file, null);
    }

    /**
     * Toggles the mark state of the given file and records the window where the
     * mark was made. The origin window is used to decide on which display the
     * marked image should appear in the grid view.
     *
     * @param file   the file to toggle (ignored if null)
     * @param origin the window where the mark was made (can be null)
     */
    public void toggleMark(File file, Window origin) {
        if (file == null) {
            return;
        }
        if (markedImages.contains(file)) {
            markedImages.remove(file);
            markOrigins.remove(file);
        } else {
            markedImages.add(file);
            if (origin != null) {
                markOrigins.put(file, origin);
            }
        }
    }

    /**
     * Returns the window where the given file was marked.
     *
     * @param file the marked file
     * @return the origin window, or null if unknown
     */
    public Window getMarkOrigin(File file) {
        return markOrigins.get(file);
    }

    /**
     * Updates the origin window of an already marked file.
     * Does nothing if the file is not marked.
     *
     * @param file   the marked file (ignored if null)
     * @param origin the new origin window (ignored if null)
     */
    public void updateMarkOrigin(File file, Window origin) {
        if (file == null || origin == null) {
            return;
        }
        if (markedImages.contains(file)) {
            markOrigins.put(file, origin);
        }
    }

    /**
     * Removes the marks of all files that were marked in the given window.
     *
     * @param origin the window whose marks should be removed (ignored if null)
     */
    public void unmarkAllFromOrigin(Window origin) {
        if (origin == null) {
            return;
        }
        var files = new ArrayList<File>();
        for (var entry : markOrigins.entrySet()) {
            if (entry.getValue() == origin) {
                files.add(entry.getKey());
            }
        }
        for (File file : files) {
            unmark(file);
        }
    }

    /**
     * Moves all mark origins from one window to another. Use this when a
     * window is recreated (e.g., toggling the window frame) so its marks
     * stay owned by the replacement window.
     *
     * @param from the window being replaced (ignored if null)
     * @param to   the replacement window (ignored if null)
     */
    public void transferMarkOrigins(Window from, Window to) {
        if (from == null || to == null) {
            return;
        }
        for (var entry : markOrigins.entrySet()) {
            if (entry.getValue() == from) {
                entry.setValue(to);
            }
        }
    }

    public void unmark(File file) {
        markedImages.remove(file);
        markOrigins.remove(file);
    }

    /**
     * Removes all marks and their origin windows.
     */
    public void clearMarks() {
        markedImages.clear();
        markOrigins.clear();
    }

    public void registerWindow(String windowId, Set<File> files) {
        windowFileRegistry.put(windowId, files);
    }

    public void unregisterWindow(String windowId) {
        Set<File> windowFiles = windowFileRegistry.remove(windowId);
        if (windowFiles == null) {
            return;
        }

        // Collect files that are still present in other windows
        Set<File> filesInOtherWindows = new HashSet<>();
        for (Set<File> otherFiles : windowFileRegistry.values()) {
            filesInOtherWindows.addAll(otherFiles);
        }

        // Remove marks for files exclusive to the closed window
        for (File file : windowFiles) {
            if (!filesInOtherWindows.contains(file)) {
                unmark(file);
            }
        }
    }
}
