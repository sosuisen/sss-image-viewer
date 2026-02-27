package com.sosuisha.imageviewer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum SharedMarkManager {
    INSTANCE;

    private final ObservableList<File> markedImages = FXCollections.observableArrayList();
    private final Map<String, Set<File>> windowFileRegistry = new HashMap<>();

    public static SharedMarkManager getInstance() {
        return INSTANCE;
    }

    public ObservableList<File> getMarkedImages() {
        return markedImages;
    }

    public void toggleMark(File file) {
        if (file == null) {
            return;
        }
        if (markedImages.contains(file)) {
            markedImages.remove(file);
        } else {
            markedImages.add(file);
        }
    }

    public void unmark(File file) {
        markedImages.remove(file);
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
                markedImages.remove(file);
            }
        }
    }
}
