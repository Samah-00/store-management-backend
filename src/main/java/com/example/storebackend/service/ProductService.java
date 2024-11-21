package com.example.storebackend.service;

import com.example.storebackend.model.Product;
import com.example.storebackend.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            log.error(String.format("Error saving product: %s", e.getMessage()));
            throw new RuntimeException("Error saving product", e);
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            log.error(String.format("Error getting all products: %s", e.getMessage()));
            throw new RuntimeException("Error getting all products", e);
        }
    }

    public Product getProductById(Long productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            return product.get();
        } catch (Exception e) {
            log.error(String.format("Error getting product with Id %s: %s", productId, e.getMessage()));
            if (Objects.equals(e.getMessage(), "No value present")) {
                throw new IllegalArgumentException("Product not found");
            }
            throw new RuntimeException("Error getting product", e);
        }
    }

    public void deleteProductById(Long productId) {
        try {
            productRepository.deleteById(productId);
        } catch (Exception e) {
            log.error(String.format("Error deleting product with Id %s: %s", productId, e.getMessage()));
            throw new RuntimeException("Error deleting product", e);
        }
    }
}
