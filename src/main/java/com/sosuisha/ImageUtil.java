package com.sosuisha;

import java.io.File;
import java.util.Arrays;

public class ImageUtil {
    public static String[] availableImageFormats = {
        "png", "jpg", "jpeg", "jfif", "gif", "bmp"
    };
    
    public static boolean isImageFile(File file) {
        return isImageFile(file.getName());
    }

    public static boolean isImageFile(String fileName) {
        var lowerCaseName = fileName.toLowerCase();
        return Arrays.stream(availableImageFormats).anyMatch(lowerCaseName::endsWith);
    }

}
