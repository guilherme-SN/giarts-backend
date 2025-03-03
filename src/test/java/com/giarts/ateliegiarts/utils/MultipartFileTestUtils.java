package com.giarts.ateliegiarts.utils;

import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public class MultipartFileTestUtils {
    public static MultipartFile createMultipartFileMock(String fileName, Long fileSize, String contentType) {
        MultipartFile file = mock(MultipartFile.class);

        lenient().when(file.getOriginalFilename()).thenReturn(fileName);
        lenient().when(file.getSize()).thenReturn(fileSize);
        lenient().when(file.getContentType()).thenReturn(contentType);

        return file;
    }
}
