package com.sosuisha.imageviewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Persists marked image paths to SQLite.
 * Each save creates a new session (identified by UUID) so history accumulates.
 */
public class MarkPersistenceService {

    private static MarkPersistenceService instance = null;

    private final String dbUrl;

    private MarkPersistenceService() {
        var dbDir = Path.of(System.getProperty("user.home"), ".sss-image-viewer");
        try {
            Files.createDirectories(dbDir);
        } catch (IOException e) {
            System.err.println("Failed to create DB directory: " + e.getMessage());
        }
        dbUrl = "jdbc:sqlite:" + dbDir.resolve("marks.db");
        initSchema();
    }

    public static synchronized MarkPersistenceService getInstance() {
        if (instance == null) {
            instance = new MarkPersistenceService();
        }
        return instance;
    }

    private void initSchema() {
        try (var conn = getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS marked_images (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id TEXT NOT NULL,
                    path TEXT NOT NULL,
                    saved_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """);
        } catch (SQLException e) {
            System.err.println("Failed to initialize DB schema: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Saves all marked image paths as a new session.
     *
     * @param markedImages list of marked image files
     */
    public void saveMarkedImages(List<File> markedImages) {
        if (markedImages.isEmpty()) {
            return;
        }
        var sessionId = UUID.randomUUID().toString();
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try (var pstmt = conn.prepareStatement(
                    "INSERT INTO marked_images (session_id, path) VALUES (?, ?)")) {
                for (var file : markedImages) {
                    pstmt.setString(1, sessionId);
                    pstmt.setString(2, file.getCanonicalPath());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException | IOException e) {
            System.err.println("Failed to save marked images: " + e.getMessage());
        }
    }
}
