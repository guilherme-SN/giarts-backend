package com.giarts.ateliegiarts.service.unit;

import com.giarts.ateliegiarts.enums.EImageFolder;
import com.giarts.ateliegiarts.exception.ImageStoreException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.model.ProductImage;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
import com.giarts.ateliegiarts.service.FileStorageService;
import com.giarts.ateliegiarts.service.ProductImageService;
import com.giarts.ateliegiarts.service.ProductService;
import com.giarts.ateliegiarts.utils.MultipartFileTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceTest {
    @Value("${server.url}")
    private String serverUrl;

    @Mock
    private ProductService productService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductImageService productImageService;

    @Captor
    private ArgumentCaptor<ProductImage> productImageArgumentCaptor;

    @Nested
    class getAllProductImages {
        @Test
        @DisplayName("Should retrieve all images of a product with success when product exists")
        void shouldGetAllProductImagesWithSuccessWhenProductExists() {
            Long productId = 1L;

            List<ProductImage> productImages = List.of(
                    createProductImage(1L, "products/1/image_1.png", true, "image_1", productId),
                    createProductImage(2L, "products/1/image_2.png", false, "image_2", productId)
            );

            doNothing().when(productService).validateProduct(productId);
            when(productImageRepository.findAllByProductId(productId)).thenReturn(productImages);

            List<ProductImage> productImagesRetrieved = productImageService.getAllProductImages(productId);

            assertNotNull(productImagesRetrieved);
            assertProductImageDetails(productImages.get(0), productImagesRetrieved.get(0));
            assertProductImageDetails(productImages.get(1), productImagesRetrieved.get(1));

            verify(productService, times(1)).validateProduct(productId);
            verify(productImageRepository, times(1)).findAllByProductId(productId);
        }
    }

    @Nested
    class saveUploadedImage {
        @Test
        @DisplayName("Should save uploaded image with success")
        void shouldSaveUploadedImageWithSuccess() {
            Long productId = 1L;
            boolean isMainImage = true;
            String fileName = "image.png";
            long fileSize = 1024L;
            String contentType = "image/png";
            String expectedImageUrl = String.format("%s/products/%d/images/%s", serverUrl, productId, fileName);

            ProductImage expectedProductImage = createProductImage(
                    1L,
                    expectedImageUrl,
                    isMainImage,
                    fileName,
                    productId
            );

            MultipartFile file = MultipartFileTestUtils.createMultipartFileMock(fileName, fileSize, contentType);

            doNothing().when(productService).validateProduct(anyLong());
            doNothing().when(fileStorageService).storeFileInEntityFolder(any(EImageFolder.class), anyLong(), any(MultipartFile.class));
            when(productService.getProductEntityById(productId)).thenReturn(createProduct(productId));
            when(productImageRepository.save(productImageArgumentCaptor.capture()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            productImageService.saveUploadedProductImage(productId, file, isMainImage);

            ProductImage capturedProductImage = productImageArgumentCaptor.getValue();
            assertNotNull(capturedProductImage);
            assertProductImageDetails(expectedProductImage, capturedProductImage);

            verify(productService, times(1)).validateProduct(productId);
            verify(fileStorageService, times(1)).storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file);
            verify(productImageRepository, times(1)).save(any(ProductImage.class));
        }

        @Test
        @DisplayName("Should throw ImageStoreException when file storage fails")
        void shouldThrowExceptionWhenFileStorageFails() {
            Long productId = 1L;
            MultipartFile file = MultipartFileTestUtils.createMultipartFileMock("image.png", 1024L, "image/png");
            boolean isMainImage = true;

            doNothing().when(productService).validateProduct(anyLong());
            doThrow(new ImageStoreException("Failed to store image for product with id: " + productId))
                    .when(fileStorageService).storeFileInEntityFolder(any(EImageFolder.class), anyLong(), any(MultipartFile.class));

            assertThrows(ImageStoreException.class, () -> productImageService.saveUploadedProductImage(productId, file, isMainImage));

            verify(productService, times(1)).validateProduct(anyLong());
            verify(fileStorageService, times(1)).storeFileInEntityFolder(EImageFolder.PRODUCT, productId, file);
        }
    }

    @Nested
    class deleteProductImage {
        @Test
        @DisplayName("Should delete product image from database with success")
        void shouldDeleteProductImageFromDatabaseWithSuccess() {
            Long productId = 1L;
            Long imageId = 1L;
            String fileName = "image.png";
            ProductImage productImage = ProductImage.builder()
                    .id(imageId)
                    .fileName(fileName)
                    .build();

            doNothing().when(productService).validateProduct(productId);
            when(productImageRepository.findById(imageId)).thenReturn(Optional.of(productImage));
            doNothing().when(fileStorageService).deleteImageFromStorage(EImageFolder.PRODUCT, productId, fileName);

            assertDoesNotThrow(() -> productImageService.deleteProductImageById(productId, imageId));

            verify(productService, times(1)).validateProduct(productId);
            verify(productImageRepository, times(1)).findById(imageId);
            verify(fileStorageService, times(1)).deleteImageFromStorage(EImageFolder.PRODUCT, productId, fileName);
            verify(productImageRepository, times(1)).deleteById(imageId);
        }

        @Test
        @DisplayName("Should throw ImageStoreException when image does not exist")
        void shouldThrowExceptionWhenImageDoesNotExist() {
            Long productId = 1L;
            Long imageId = 1L;

            doNothing().when(productService).validateProduct(productId);
            when(productImageRepository.findById(imageId)).thenReturn(Optional.empty());

            assertThrows(ImageStoreException.class, () -> productImageService.deleteProductImageById(productId, imageId));

            verify(fileStorageService, never()).deleteImageFromStorage(any(EImageFolder.class), anyLong(), anyString());
            verify(productImageRepository, never()).deleteById(anyLong());
        }
    }

    private ProductImage createProductImage(Long imageId, String imageUrl, boolean isMainImage, String fileName,
                                            Long productId) {
        return ProductImage.builder()
                .id(imageId)
                .imageUrl(imageUrl)
                .isMainImage(isMainImage)
                .fileName(fileName)
                .fileSize(1024L)
                .fileType("image/png")
                .product(createProduct(productId))
                .build();
    }

    private Product createProduct(Long productId) {
        return Product.builder().id(productId).build();
    }

    private void assertProductImageDetails(ProductImage expected, ProductImage actual) {
        assertAll(
                () -> assertEquals(expected.getImageUrl(), actual.getImageUrl()),
                () -> assertEquals(expected.getIsMainImage(), actual.getIsMainImage()),
                () -> assertEquals(expected.getFileName(), actual.getFileName()),
                () -> assertEquals(expected.getFileSize(), actual.getFileSize()),
                () -> assertEquals(expected.getFileType(), actual.getFileType()),
                () -> assertEquals(expected.getProduct().getId(), actual.getProduct().getId())
        );
    }
}
