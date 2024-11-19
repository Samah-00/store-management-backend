package com.example.storebackend.service;

import com.example.storebackend.model.CartItem;
import com.example.storebackend.model.Product;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CartService {
    private final ProductService productService;

    @Autowired
    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public void addItemToCart(Long productId, int quantity, HttpSession session) {
        // Retrieve current cart or initialize a new one if it doesn't exist
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Check if the product exists in the database
        try {
            productService.getProductById(productId);
        } catch (Exception e) {
            log.error(String.format("Product with ID %d not found. Error: %s", productId, e.getMessage()));
            throw new IllegalArgumentException("Product not found");
        }

        Optional<CartItem> existingCartItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        // if item already exist in cart, increment its quantity.
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            log.info(String.format("Product with ID %d already in cart. Increased quantity to %d", productId, cartItem.getQuantity()));
        } else {
            // If item doesn't exist in cart, add it
            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cart.add(cartItem);
            log.info(String.format("Added product with ID %d to cart with quantity %d", productId, quantity));
        }

        // Update the session with the modified cart
        session.setAttribute("cart", cart);
    }

    public List<CartItem> viewCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    public void removeItemFromCart(Long productId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProductId().equals(productId));
            session.setAttribute("cart", cart);
            log.info(String.format("Removed product with ID %d from cart.", productId));
        } else {
            log.warn(String.format("Attempted to remove product with ID %d but cart is empty or not found.", productId));
        }
    }

    // Utility method to get the cart from session or initialize a new one
    public List<Product> getCartProducts(HttpSession session) {
        List<CartItem> cart = viewCart(session);
        List<Product> products = new ArrayList<>();

        for (CartItem cartItem : cart) {
            try {
                Product product = productService.getProductById(cartItem.getProductId());
                product.setStock(cartItem.getQuantity());  // Set stock as quantity in the cart for display purposes
                products.add(product);
            } catch (Exception e) {
                log.error(String.format("Error fetching product with ID %d for cart. Error: %s", cartItem.getProductId(), e.getMessage()));
                throw new RuntimeException("Something went wrong");
            }
        }

        return products;
    }
}
