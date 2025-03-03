package com.giarts.ateliegiarts.util;

import com.giarts.ateliegiarts.enums.EImageFolder;

public class ImageUrlGenerator {
    public static String generateImageUrl(String serverUrl, EImageFolder entityFolder, Long entityId, String fileName) {
        return String.format("%s/%s/%d/images/%s", serverUrl, entityFolder.getFolderName(), entityId, fileName);
    }
}
