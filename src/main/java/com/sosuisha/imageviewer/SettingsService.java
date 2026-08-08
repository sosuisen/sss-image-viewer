package com.sosuisha.imageviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Persists application settings to a properties file in the app data
 * directory ({@code ~/.sss-image-viewer/settings.properties}).
 */
public class SettingsService {

    private static final String KEY_OPEN_DND_AT_STARTUP = "openDragAndDropWindowAtStartup";

    private static SettingsService instance = null;

    private final Path settingsFile;
    private final Properties properties = new Properties();

    private SettingsService() {
        var dir = Path.of(System.getProperty("user.home"), ".sss-image-viewer");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Failed to create settings directory: " + e.getMessage());
        }
        settingsFile = dir.resolve("settings.properties");
        load();
    }

    public static synchronized SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService();
        }
        return instance;
    }

    private void load() {
        if (!Files.exists(settingsFile)) {
            return;
        }
        try (InputStream in = Files.newInputStream(settingsFile)) {
            properties.load(in);
        } catch (IOException e) {
            System.err.println("Failed to load settings: " + e.getMessage());
        }
    }

    private void save() {
        try (OutputStream out = Files.newOutputStream(settingsFile)) {
            properties.store(out, "SSS Image Viewer settings");
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    /**
     * Returns whether a drag and drop window should be opened at start-up
     * when the application is launched without a file. Defaults to true.
     *
     * @return true if the drag and drop window should be opened at start-up
     */
    public boolean isOpenDragAndDropWindowAtStartup() {
        return Boolean.parseBoolean(properties.getProperty(KEY_OPEN_DND_AT_STARTUP, "true"));
    }

    /**
     * Sets whether a drag and drop window should be opened at start-up and
     * saves the settings file.
     *
     * @param value true to open the drag and drop window at start-up
     */
    public void setOpenDragAndDropWindowAtStartup(boolean value) {
        properties.setProperty(KEY_OPEN_DND_AT_STARTUP, Boolean.toString(value));
        save();
    }
}
