package com.sosuisha.imageviewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
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
                    screen_x REAL NOT NULL DEFAULT 0,
                    screen_y REAL NOT NULL DEFAULT 0,
                    offset_x REAL NOT NULL DEFAULT 0,
                    offset_y REAL NOT NULL DEFAULT 0,
                    saved_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
                )
                """);
            addColumnIfMissing(conn, "mark_order", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(conn, "image_scale", "REAL NOT NULL DEFAULT 1.0");
            addColumnIfMissing(conn, "frame_scale", "REAL NOT NULL DEFAULT 1.0");
            addColumnIfMissing(conn, "screen_x", "REAL NOT NULL DEFAULT 0");
            addColumnIfMissing(conn, "screen_y", "REAL NOT NULL DEFAULT 0");
            addColumnIfMissing(conn, "offset_x", "REAL NOT NULL DEFAULT 0");
            addColumnIfMissing(conn, "offset_y", "REAL NOT NULL DEFAULT 0");
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
     * Loads one summary entry per session, newest first.
     *
     * @return list of session entries with image paths in ascending mark order
     */
    public List<SessionEntry> loadSessionSummaries() {
        var sessions = new LinkedHashMap<String, SessionEntry>();
        var sql = """
                SELECT session_id, path, saved_at
                FROM marked_images
                ORDER BY saved_at DESC, session_id ASC, mark_order ASC
                """;
        try (var conn = getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                var sessionId = rs.getString("session_id");
                var session = sessions.get(sessionId);
                if (session == null) {
                    session = new SessionEntry(sessionId, rs.getString("saved_at"), new ArrayList<>());
                    sessions.put(sessionId, session);
                }
                session.filePaths().add(rs.getString("path"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session summaries: " + e.getMessage());
        }
        return new ArrayList<>(sessions.values());
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
                SELECT path, mark_order, image_scale, frame_scale, screen_x, screen_y, offset_x, offset_y
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
                    double screenX = rs.getDouble("screen_x");
                    double screenY = rs.getDouble("screen_y");
                    double offsetX = rs.getDouble("offset_x");
                    double offsetY = rs.getDouble("offset_y");
                    result.add(new GridImageEntry(file, markOrder, imageScale, frameScale, screenX, screenY,
                            offsetX, offsetY));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session: " + e.getMessage());
        }
        return result;
    }

    /**
     * Deletes all entries for the specified session, and removes cached
     * thumbnails of images that are no longer referenced by any remaining
     * session. Use this for user-initiated deletion; use
     * {@link #deleteSession(String)} when the session is deleted only to be
     * saved again, so thumbnails are kept.
     *
     * @param sessionId the session UUID to delete
     * @throws NullPointerException if sessionId is null
     */
    public void deleteSessionAndThumbnails(String sessionId) {
        java.util.Objects.requireNonNull(sessionId, "sessionId must not be null");
        var paths = loadSessionPaths(sessionId);
        deleteSession(sessionId);
        for (var path : paths) {
            if (!isPathReferenced(path)) {
                ImageService.getInstance().deleteThumbnailsForFile(new File(path));
            }
        }
    }

    private List<String> loadSessionPaths(String sessionId) {
        var paths = new ArrayList<String>();
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(
                     "SELECT DISTINCT path FROM marked_images WHERE session_id = ?")) {
            pstmt.setString(1, sessionId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paths.add(rs.getString("path"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session paths: " + e.getMessage());
        }
        return paths;
    }

    private boolean isPathReferenced(String path) {
        try (var conn = getConnection();
             var pstmt = conn.prepareStatement(
                     "SELECT 1 FROM marked_images WHERE path = ? LIMIT 1")) {
            pstmt.setString(1, path);
            try (var rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Failed to check path reference: " + e.getMessage());
            // Keep thumbnails when the check fails
            return true;
        }
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
                    "INSERT INTO marked_images (session_id, path, mark_order, image_scale, frame_scale, screen_x, screen_y, offset_x, offset_y) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (var entry : gridEntries) {
                    pstmt.setString(1, id);
                    pstmt.setString(2, entry.file().getCanonicalPath());
                    pstmt.setInt(3, entry.markOrder());
                    pstmt.setDouble(4, entry.imageScale());
                    pstmt.setDouble(5, entry.frameScale());
                    pstmt.setDouble(6, entry.screenX());
                    pstmt.setDouble(7, entry.screenY());
                    pstmt.setDouble(8, entry.offsetX());
                    pstmt.setDouble(9, entry.offsetY());
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
