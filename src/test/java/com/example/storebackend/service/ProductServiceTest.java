package com.example.storebackend.service;

import com.example.storebackend.model.Product;
import com.example.storebackend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(null, "Test Product", 100.0, 10);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_shouldSaveAndReturnProduct() {
        // Arrange
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // Act
        Product result = productService.addProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void addProduct_shouldThrowRuntimeExceptionWhenRepositoryFails() {
        // Arrange
        when(productRepository.save(testProduct)).thenThrow(new RuntimeException("Mock database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(testProduct);
        });

        assertEquals("Error saving product", exception.getMessage());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        // Arrange
        Product product1 = new Product(1L, "Product 1", 50.0, 20);
        Product product2 = new Product(2L, "Product 2", 75.0, 15);

        List<Product> products = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product1.getName(), result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_shouldThrowRuntimeExceptionWhenRepositoryFails() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new RuntimeException("Mock database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getAllProducts();
        });

        assertEquals("Error getting all products", exception.getMessage());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_shouldReturnEmptyListWhenNoProductsExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_shouldReturnProductWhenFound() {
        // Arrange
        testProduct.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_shouldThrowIllegalArgumentExceptionWhenNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void deleteProductById_shouldDeleteProductSuccessfully() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // Act
        productService.deleteProductById(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void deleteProductById_shouldThrowRuntimeExceptionWhenDeletionFails() {
        // Arrange
        Long productId = 1L;
        doThrow(new RuntimeException("Mock database error")).when(productRepository).deleteById(productId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProductById(productId);
        });

        assertEquals("Error deleting product", exception.getMessage());
        verify(productRepository, times(1)).deleteById(productId);
    }
}

