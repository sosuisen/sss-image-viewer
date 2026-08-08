package com.sosuisha.imageviewer;

import java.io.File;

/**
 * Data for a single image displayed in the grid view, including scale information.
 *
 * @param file       the image file
 * @param markOrder  the original mark order
 * @param imageScale the zoom scale of the image (1.0 = original)
 * @param frameScale the weight scale of the treemap cell (1.0 = original)
 * @param screenX    the min X of the bounds of the screen where the entry was displayed
 * @param screenY    the min Y of the bounds of the screen where the entry was displayed
 * @param offsetX    the drag offset X of the image within its cell
 * @param offsetY    the drag offset Y of the image within its cell
 */
public record GridImageEntry(File file, int markOrder, double imageScale, double frameScale,
        double screenX, double screenY, double offsetX, double offsetY) {
}
