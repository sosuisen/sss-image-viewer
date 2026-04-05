package com.sosuisha.imageviewer;

import java.io.File;

/**
 * Data for a single image displayed in the grid view, including scale information.
 *
 * @param file       the image file
 * @param markOrder  the original mark order
 * @param imageScale the zoom scale of the image (1.0 = original)
 * @param frameScale the weight scale of the treemap cell (1.0 = original)
 */
public record GridImageEntry(File file, int markOrder, double imageScale, double frameScale) {
}
