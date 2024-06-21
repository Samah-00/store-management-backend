package com.example.storebackend.service;

import com.example.storebackend.model.CartItem;
import com.example.storebackend.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw new IllegalArgumentException("Product not found");
        }

        // Check if the item already exists in the cart
        Optional<CartItem> existingCartItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        // if item already exist in cart, increment its quantity.
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // If item doesn't exist in cart, add it
            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cart.add(cartItem);
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
                throw new RuntimeException("Something went wrong");
            }
        }

        return products;
    }
}
