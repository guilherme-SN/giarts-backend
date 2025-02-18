package com.giarts.ateliegiarts.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { FileStorageService.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileStorageServiceIntegrationTest {
    @Autowired
    private FileStorageService fileStorageService;

    private Path temporaryDirectory;

    @BeforeAll
    void setup() throws IOException {
        temporaryDirectory = Files.createTempDirectory("test-storage");
        ReflectionTestUtils.setField(fileStorageService, "uploadLocation", temporaryDirectory.toString());
    }

    @Test
    @DisplayName("Should store file with success")
    void shouldStoreFileWithSuccess() throws IOException {
        Long productId = 1L;
        String fileName = "image.png";
        String content = "content";
        MultipartFile file = createMockFile(fileName, content);

        fileStorageService.storeFileInProductFolder(productId, file);

        Path storedFilePath = temporaryDirectory.resolve(productId.toString() + "/" + fileName);
        assertTrue(Files.exists(storedFilePath));
    }

    @Test
    @DisplayName("Should delete file with success")
    void shouldDeleteFileWithSuccess() throws IOException {
        Long productId = 1L;
        String fileName = "image.png";
        String content = "content";
        MultipartFile file = createMockFile(fileName, content);

        fileStorageService.storeFileInProductFolder(productId, file);

        Path storedFilePath = temporaryDirectory.resolve(productId.toString() + "/" + fileName);
        assertTrue(Files.exists(storedFilePath));

        fileStorageService.deleteImageFromStorage(productId, fileName);

        assertFalse(Files.exists(storedFilePath));
    }

    private MultipartFile createMockFile(String fileName, String content) throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes()));

        return file;
    }
}
