package com.giarts.ateliegiarts.util;

import com.giarts.ateliegiarts.enums.EImageFolder;
import org.springframework.beans.factory.annotation.Value;

public class ImageUrlGenerator {
    @Value("${server.url}")
    private static String serverUrl;

    public static String generateImageUrl(EImageFolder entityFolder, Long entityId, String fileName) {
        return String.format("%s/%s/%d/images/%s", serverUrl, entityFolder.getFolderName(), entityId, fileName);
    }
}
