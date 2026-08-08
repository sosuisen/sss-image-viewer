package com.sosuisha.imageviewer;

import java.util.List;

/**
 * Summary of a saved grid session shown as one row in the history window.
 *
 * @param sessionId the session UUID
 * @param savedAt   the date and time when the session was saved
 * @param filePaths image file paths of the session in ascending mark order
 */
public record SessionEntry(String sessionId, String savedAt, List<String> filePaths) {
}
