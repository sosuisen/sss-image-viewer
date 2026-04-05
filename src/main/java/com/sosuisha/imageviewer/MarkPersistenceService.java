package com.sosuisha.imageviewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Persists marked image paths to SQLite.
 * Each save creates a new session (identified by UUID) so history accumulates.
 */
public class MarkPersistenceService {

    private static MarkPersistenceService instance = null;

    private final String dbUrl;
    private final List<Runnable> changeListeners = new ArrayList<>();

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
                    mark_order INTEGER NOT NULL DEFAULT 0,
                    image_scale REAL NOT NULL DEFAULT 1.0,
                    frame_scale REAL NOT NULL DEFAULT 1.0,
                    saved_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """);
            addColumnIfMissing(conn, "mark_order", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(conn, "image_scale", "REAL NOT NULL DEFAULT 1.0");
            addColumnIfMissing(conn, "frame_scale", "REAL NOT NULL DEFAULT 1.0");
        } catch (SQLException e) {
            System.err.println("Failed to initialize DB schema: " + e.getMessage());
        }
    }

    private void addColumnIfMissing(Connection conn, String columnName, String columnDef) {
        try (var rs = conn.getMetaData().getColumns(null, null, "marked_images", columnName)) {
            if (!rs.next()) {
                try (var stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE marked_images ADD COLUMN " + columnName + " " + columnDef);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to add " + columnName + " column: " + e.getMessage());
        }
    }

    /**
     * Adds a listener that is notified when data changes.
     *
     * @param listener the callback to invoke on change
     * @throws NullPointerException if listener is null
     */
    public void addChangeListener(Runnable listener) {
        changeListeners.add(java.util.Objects.requireNonNull(listener, "listener must not be null"));
    }

    /**
     * Removes a previously added change listener.
     *
     * @param listener the callback to remove
     */
    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    private void notifyChange() {
        for (var listener : changeListeners) {
            listener.run();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Loads all history entries from the database.
     *
     * @return list of history entries ordered by saved_at descending
     */
    public List<HistoryEntry> loadHistory() {
        var entries = new ArrayList<HistoryEntry>();
        var sql = """
                SELECT session_id, path, mark_order, image_scale, frame_scale, saved_at
                FROM marked_images
                ORDER BY saved_at DESC, mark_order ASC
                """;
        try (var conn = getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                var sessionId = rs.getString("session_id");
                var filePath = rs.getString("path");
                var fileName = Path.of(filePath).getFileName().toString();
                int markOrder = rs.getInt("mark_order");
                double imageScale = rs.getDouble("image_scale");
                double frameScale = rs.getDouble("frame_scale");
                var savedAt = rs.getString("saved_at");
                entries.add(new HistoryEntry(sessionId, filePath, fileName, markOrder, imageScale, frameScale, savedAt));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load history: " + e.getMessage());
        }
        return entries;
    }

    /**
     * Loads all grid image entries for a specific session.
     *
     * @param sessionId the session UUID to load
     * @return list of grid image entries ordered by mark_order
     * @throws NullPointerException if sessionId is null
     */
    public List<GridImageEntry> loadSession(String sessionId) {
        java.util.Objects.requireNonNull(sessionId, "sessionId must not be null");
        var result = new ArrayList<GridImageEntry>();
        var sql = """
                SELECT path, mark_order, image_scale, frame_scale
                FROM marked_images
                WHERE session_id = ?
                ORDER BY mark_order ASC
                """;
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sessionId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    var file = new File(rs.getString("path"));
                    int markOrder = rs.getInt("mark_order");
                    double imageScale = rs.getDouble("image_scale");
                    double frameScale = rs.getDouble("frame_scale");
                    result.add(new GridImageEntry(file, markOrder, imageScale, frameScale));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session: " + e.getMessage());
        }
        return result;
    }

    /**
     * Deletes all entries for the specified session.
     *
     * @param sessionId the session UUID to delete
     * @throws NullPointerException if sessionId is null
     */
    public void deleteSession(String sessionId) {
        java.util.Objects.requireNonNull(sessionId, "sessionId must not be null");
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement("DELETE FROM marked_images WHERE session_id = ?")) {
            pstmt.setString(1, sessionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete session: " + e.getMessage());
        }
        notifyChange();
    }

    /**
     * Saves grid view entries with scale information.
     * If sessionId is null, a new session is created.
     *
     * @param gridEntries list of grid image entries with scale data
     * @param sessionId   the session ID to use, or null for a new session
     */
    public void saveGridEntries(List<GridImageEntry> gridEntries, String sessionId) {
        if (gridEntries.isEmpty()) {
            return;
        }
        var id = sessionId != null ? sessionId : UUID.randomUUID().toString();
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try (var pstmt = conn.prepareStatement(
                    "INSERT INTO marked_images (session_id, path, mark_order, image_scale, frame_scale) VALUES (?, ?, ?, ?, ?)")) {
                for (var entry : gridEntries) {
                    pstmt.setString(1, id);
                    pstmt.setString(2, entry.file().getCanonicalPath());
                    pstmt.setInt(3, entry.markOrder());
                    pstmt.setDouble(4, entry.imageScale());
                    pstmt.setDouble(5, entry.frameScale());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException | IOException e) {
            System.err.println("Failed to save grid entries: " + e.getMessage());
        }
        notifyChange();
    }
}
