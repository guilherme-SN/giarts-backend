package com.giarts.ateliegiarts.service.unit;

import com.giarts.ateliegiarts.dto.product.CreateProductDTO;
import com.giarts.ateliegiarts.dto.product.ResponseProductDTO;
import com.giarts.ateliegiarts.dto.product.UpdateProductDTO;
import com.giarts.ateliegiarts.enums.EProductType;
import com.giarts.ateliegiarts.exception.ProductNotFoundException;
import com.giarts.ateliegiarts.model.Product;
import com.giarts.ateliegiarts.repository.ProductRepository;
import com.giarts.ateliegiarts.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
            Pageable pageable = PageRequest.of(0, 100);

            List<Product> productsList = List.of(
                    createProduct(1L, "product 1", "description 1", EProductType.BOLSA),
                    createProduct(2L, "product 2", "description 2", EProductType.TAPETE)
            );

            Page<Product> productsPage = new PageImpl<>(productsList, pageable, productsList.size());

            when(productRepository.findAllProductsPaginated(pageable)).thenReturn(productsPage);

            List<ResponseProductDTO> productsRetrieved = productService.getAllProducts(pageable).getContent();

            assertNotNull(productsRetrieved);
            assertProductDetails(productsList.get(0), productsRetrieved.get(0));
            assertProductDetails(productsList.get(1), productsRetrieved.get(1));

            verify(productRepository, times(1)).findAllProductsPaginated(pageable);
        }
    }

    @Nested
    class getProductById {
        @Test
        @DisplayName("Should get product by ID with success when product exists")
        void shouldGetProductByIdWithSuccessWhenProductExists() {
            Product product = createProduct(1L, "product", "description", EProductType.BOLSA);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            ResponseProductDTO productRetrieved = productService.getProductById(product.getId());

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
            CreateProductDTO productDTO = new CreateProductDTO("product", "description", EProductType.BOLSA);
            Product product = createProduct(1L, "product", "description", EProductType.BOLSA);

            when(productRepository.save(productArgumentCaptor.capture())).thenReturn(product);

            ResponseProductDTO createdProduct = productService.createProduct(productDTO);

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
            UpdateProductDTO updateProductDTO = new UpdateProductDTO("product updated", "description updated",
                    EProductType.BOLSA);

            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
            when(productRepository.save(productArgumentCaptor.capture())).thenReturn(product);

            ResponseProductDTO updatedProduct = productService.updateProductById(product.getId(), updateProductDTO);

            assertNotNull(updatedProduct);
            assertProductDetails(updateProductDTO, updatedProduct);

            verify(productRepository, times(1)).findById(product.getId());
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ProductNotFoundException when product does not exists")
        void shouldThrowExceptionWhenProductDoesNotExists() {
            Long productId = 1L;
            UpdateProductDTO updateProductDTO = new UpdateProductDTO("product updated", "description updated",
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

    private void assertProductDetails(Product expected, ResponseProductDTO actual) {
        assertAll(
                () -> assertEquals(expected.getName(), actual.name()),
                () -> assertEquals(expected.getDescription(), actual.description()),
                () -> assertEquals(expected.getProductType(), actual.productType())
        );
    }

    private void assertProductDetails(UpdateProductDTO expected, ResponseProductDTO actual) {
        assertProductDetails(
                Product.builder()
                        .name(expected.name())
                        .description(expected.description())
                        .productType(expected.productType())
                        .build(),
                actual
        );
    }
}
