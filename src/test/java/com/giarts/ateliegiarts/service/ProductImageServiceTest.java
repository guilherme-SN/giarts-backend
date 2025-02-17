package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.model.ProductImage;
import com.giarts.ateliegiarts.repository.ProductImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceTest {
    @Mock
    private ProductService productService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductImageService productImageService;

    @Nested
    class getAllProductImages {
        @Test
        @DisplayName("Should retrieve all images of a product with success when product exists")
        void shouldGetAllProductImagesWithSuccessWhenProductExists() {
            Long productId = 1L;

            List<ProductImage> productImages = List.of(
                    createProductImage(1L, "products/1/image_1.png", true, "image_1", productId),
                    createProductImage(2L, "products/2/image_2.png", false, "image_2", productId)
            );

            doNothing().when(productService).validateProduct(productId);
            when(productImageRepository.findAllByProductId(productId)).thenReturn(productImages);

            List<ProductImage> productImagesRetrieved = productImageService.getAllProductImages(productId);

            assertNotNull(productImagesRetrieved);
            assertNotEquals(0, productImagesRetrieved.size());
            assertProductImageDetails(productImages.get(0), productImagesRetrieved.get(0));
            assertProductImageDetails(productImages.get(1), productImagesRetrieved.get(1));

            verify(productService, times(1)).validateProduct(productId);
            verify(productImageRepository, times(1)).findAllByProductId(productId);
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product does not exist")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            Long productId = 1L;

            doThrow(new ProductNotFoundException(productId)).when(productService).validateProduct(productId);

            assertThrows(ProductNotFoundException.class, () -> productImageService.getAllProductImages(productId));

            verify(productService, times(1)).validateProduct(productId);
            verify(productImageRepository, times(0)).findAllByProductId(productId);
        }
    }

    @Nested
    class saveUploadedImage {
        @Test
        @DisplayName("Should save uploaded image with success")
        void shouldSaveUploadedImageWithSuccess() throws IOException {
            Long productId = 1L;
            Product product = createProduct(productId);
            MultipartFile file = mock(MultipartFile.class);
            String fileName = "image.png";
            boolean isMainImage = true;

            doNothing().when(productService).validateProduct(anyLong());
            doNothing().when(fileStorageService).storeFileInProductFolder(productId, file);

            when(file.getOriginalFilename()).thenReturn(fileName);
            when(productService.getProductById(productId)).thenReturn(product);
            when(productImageRepository.save(any(ProductImage.class))).thenReturn(new ProductImage());

            assertDoesNotThrow(() -> productImageService.saveUploadedImage(productId, file, isMainImage));

            // TODO: add captor to capture ProductImage in save() method and verify it
        }
    }

    private ProductImage createProductImage(Long imageId, String imageUrl, boolean isMainImage, String fileName,
                                            Long productId) {
        return ProductImage.builder()
                .id(imageId)
                .imageUrl(imageUrl)
                .isMainImage(isMainImage)
                .fileName(fileName)
                .fileSize(1000L)
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
