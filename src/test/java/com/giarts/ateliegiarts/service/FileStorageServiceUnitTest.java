package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceUnitTest {
    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "uploadLocation", "/tmp/storage");
    }

    @Nested
    class storeFileInEntityFolder {
        @Test
        @DisplayName("Should throw ImageStoreException when directory creation fails")
        void shouldThrowExceptionWhenDirectoryCreationFails() {
            Long productId = 1L;

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new IOException("Directory creation error"));

                assertThrows(ImageStoreException.class, () -> fileStorageService.storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file));
            }
        }

        @Test
        @DisplayName("Should throw ImageStoreException when file copy fails")
        void shouldThrowExceptionWhenFileCopyFails() throws IOException {
            Long productId = 1L;
            String content = "content";

            when(file.getOriginalFilename()).thenReturn("image.png");
            when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes()));

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);
                mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                        .thenThrow(new IOException("File copy error"));

                assertThrows(ImageStoreException.class, () -> fileStorageService.storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file));
            }
        }
    }

    @Nested
    class deleteImageFromStorage {
        @Test
        @DisplayName("Should throw ImageStoreException when file deletion fails")
        void shouldThrowExceptionWhenFileDeletionFails() {
            Long productId = 1L;
            String fileName = "image.png";

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles.when(() -> Files.deleteIfExists(any(Path.class))).thenThrow(new IOException("File copy error"));

                assertThrows(ImageStoreException.class, () -> fileStorageService.deleteImageFromStorage(EImageFolder.PRODUCT, productId, fileName));
            }
        }
    }
}
