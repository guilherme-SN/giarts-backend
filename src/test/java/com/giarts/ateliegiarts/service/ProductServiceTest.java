package com.giarts.ateliegiarts.service;

import com.giarts.ateliegiarts.dto.ProductDTO;
import com.giarts.ateliegiarts.enums.EProductType;
import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @Nested
    class getAllProducts {
        @Test
        @DisplayName("Should get all products with success")
        void shouldGetAllProductsWithSuccess() {
            List<Product> products = List.of(
                    createProduct(1L, "product 1", "description 1", EProductType.BOLSA),
                    createProduct(2L, "product 2", "description 2", EProductType.TAPETE)
            );

            when(productRepository.findAll()).thenReturn(products);

            List<Product> productsRetrieved = productService.getAllProducts();

            assertNotNull(productsRetrieved);
            assertProductDetails(products.get(0), productsRetrieved.get(0));
            assertProductDetails(products.get(1), productsRetrieved.get(1));

            verify(productRepository, times(1)).findAll();
        }
    }

    @Nested
    class getProductById {
        @Test
        @DisplayName("Should get product by ID with success when product exists")
        void shouldGetProductByIdWithSuccessWhenProductExists() {
            Product product = createProduct(1L, "product", "description", EProductType.BOLSA);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            Product productRetrieved = productService.getProductById(product.getId());

            assertNotNull(productRetrieved);
            assertProductDetails(product, productRetrieved);

            verify(productRepository, times(1)).findById(product.getId());
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product does not exists")
        void shouldThrowExceptionWhenProductDoesNotExists() {
            Long productId = 1L;

            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));

            verify(productRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class createProduct {
        @Test
        @DisplayName("Should create product with success")
        void shouldCreateProductWithSuccess() {
            ProductDTO productDTO = createProductDTO("product", "description", EProductType.BOLSA);
            Product product = createProduct(1L, "product", "description", EProductType.BOLSA);

            when(productRepository.save(productArgumentCaptor.capture())).thenReturn(product);

            Product createdProduct = productService.createProduct(productDTO);

            assertNotNull(createdProduct);
            assertProductDetails(product, createdProduct);

            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Nested
    class updateProduct {
        @Test
        @DisplayName("Should update product with success")
        void shouldUpdateProductWithSuccess() {
            Product product = createProduct(1L, "product", "description", EProductType.BOLSA);
            ProductDTO updateProductDTO = createProductDTO("product updated", "description updated",
                    EProductType.BOLSA);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
            when(productRepository.save(productArgumentCaptor.capture())).thenReturn(product);

            Product updatedProduct = productService.updateProductById(product.getId(), updateProductDTO);

            assertNotNull(updatedProduct);
            assertProductDetails(updateProductDTO, updatedProduct);

            verify(productRepository, times(1)).findById(product.getId());
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product does not exists")
        void shouldThrowExceptionWhenProductDoesNotExists() {
            Long productId = 1L;
            ProductDTO updateProductDTO = createProductDTO("product updated", "description updated",
                    EProductType.BOLSA);

            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ProductNotFoundException.class, () -> productService.updateProductById(productId, updateProductDTO));

            verify(productRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    class deleteProductById {
        @Test
        @DisplayName("Should delete product with success")
        void shouldDeleteProductWithSuccess() {
            Long productId = 1L;

            when(productRepository.existsById(productId)).thenReturn(true);

            assertDoesNotThrow(() -> productService.deleteProductById(productId));

            verify(productRepository, times(1)).existsById(productId);
            verify(productRepository, times(1)).deleteById(productId);
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product does not exists")
        void shouldThrowExceptionWhenProductDoesNotExists() {
            Long productId = 1L;

            when(productRepository.existsById(productId)).thenReturn(false);

            assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(productId));

            verify(productRepository, times(1)).existsById(productId);
            verify(productRepository, never()).deleteById(productId);
        }
    }

    @Nested
    class validateProduct {
        @Test
        @DisplayName("Should not throw exception when product is valid")
        void ShouldNotThrowExceptionWhenProductIsValid() {
            Long productId = 1L;

            when(productRepository.existsById(productId)).thenReturn(true);

            assertDoesNotThrow(() -> productService.validateProduct(productId));

            verify(productRepository, times(1)).existsById(productId);
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product is invalid")
        void ShouldThrowExceptionWhenProductIsInvalid() {
            Long productId = 1L;

            when(productRepository.existsById(productId)).thenReturn(false);

            assertThrows(ProductNotFoundException.class, () -> productService.validateProduct(productId));

            verify(productRepository, times(1)).existsById(productId);
        }
    }

    private Product createProduct(Long id, String name, String description, EProductType productType) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .productType(productType)
                .build();
    }

    private ProductDTO createProductDTO(String name, String description, EProductType productType) {
        return ProductDTO.builder()
                .name(name)
                .description(description)
                .productType(productType)
                .build();
    }

    private void assertProductDetails(Product expected, Product actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getProductType(), actual.getProductType())
        );
    }

    private void assertProductDetails(ProductDTO expected, Product actual) {
        assertProductDetails(
                Product.builder()
                        .name(expected.getName())
                        .description(expected.getDescription())
                        .productType(expected.getProductType())
                        .build(),
                actual
        );
    }
}
