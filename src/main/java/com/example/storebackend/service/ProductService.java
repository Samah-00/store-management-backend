package com.example.storebackend.service;

import com.example.storebackend.model.Product;
import com.example.storebackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            System.err.printf("Error saving product: %s%n", e.getMessage());
            throw new RuntimeException("Error saving product", e);
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            System.err.printf("Error getting all products: %s%n", e.getMessage());
            throw new RuntimeException("Error getting all products", e);
        }
    }

    public Product getProductById(Long productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            return product.get();
        } catch (Exception e) {
            System.err.printf("Error getting product with Id %d: %s%n", productId, e.getMessage());
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
            System.err.printf("Error deleting product with Id %d: %s%n", productId, e.getMessage());
            throw new RuntimeException("Error deleting product", e);
        }
    }
}
