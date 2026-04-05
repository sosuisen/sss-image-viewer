package com.sosuisha.imageviewer;

/**
 * Represents a single history entry for the marked images history view.
 *
 * @param sessionId  the session UUID
 * @param filePath   the full path to the image file
 * @param fileName   the image file name
 * @param markOrder  the order in which the image was marked within the session
 * @param imageScale the zoom scale of the image
 * @param frameScale the weight scale of the treemap cell
 * @param savedAt    the date and time when the entry was saved
 */
public record HistoryEntry(String sessionId, String filePath, String fileName, int markOrder,
                           double imageScale, double frameScale, String savedAt) {
}
