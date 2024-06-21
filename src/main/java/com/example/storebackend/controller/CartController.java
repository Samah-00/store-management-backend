package com.example.storebackend.controller;

import com.example.storebackend.model.CartItem;
import com.example.storebackend.model.Product;
import com.example.storebackend.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItemToCart(@RequestBody CartItem cartItem, HttpSession session) {
        try {
            cartService.addItemToCart(cartItem.getProductId(), cartItem.getQuantity(), session);
            return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add item to cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> viewCart(HttpSession session) {
        try {
            List<Product> cartProducts = cartService.getCartProducts(session);
            return new ResponseEntity<>(cartProducts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItemFromCart(@RequestParam Long productId, HttpSession session) {
        try {
            cartService.removeItemFromCart(productId, session);
            return new ResponseEntity<>("Item removed from cart", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove item from cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
