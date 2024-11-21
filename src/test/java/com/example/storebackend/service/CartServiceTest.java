package com.example.storebackend.service;

import com.example.storebackend.model.CartItem;
import com.example.storebackend.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private MockHttpSession session;

    private Product testProduct;

    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();

        testProduct = new Product(1L, "Test Product", 100.0, 10);

        cartItem = new CartItem(1L, 2);
    }

    @Test
    void addItemToCart_shouldAddNewItemWhenCartIsEmpty() {
        Long productId = 1L;
        int quantity = 2;

        when(productService.getProductById(productId)).thenReturn(testProduct);

        cartService.addItemToCart(productId, quantity, session);

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        assertNotNull(cart);
        assertEquals(1, cart.size());
        assertEquals(cartItem.getProductId(), cart.get(0).getProductId());
        assertEquals(cartItem.getQuantity(), cart.get(0).getQuantity());

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void addItemToCart_shouldIncrementQuantityIfItemExists() {
        Long productId = 1L;
        int initialQuantity = 2;
        int additionalQuantity = 3;

        when(productService.getProductById(productId)).thenReturn(testProduct);

        // Simulate an existing cart with an item
        CartItem existingItem = new CartItem(productId, initialQuantity);

        session.setAttribute("cart", List.of(existingItem));

        cartService.addItemToCart(productId, additionalQuantity, session);

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        assertNotNull(cart);
        assertEquals(1, cart.size());
        assertEquals(productId, cart.get(0).getProductId());
        assertEquals(initialQuantity + additionalQuantity, cart.get(0).getQuantity());

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    void viewCart_shouldReturnEmptyListWhenCartIsNull() {
        List<CartItem> cart = cartService.viewCart(session);

        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    void viewCart_shouldReturnExistingCart() {
        session.setAttribute("cart", List.of(cartItem));

        List<CartItem> cart = cartService.viewCart(session);

        assertNotNull(cart);
        assertEquals(1, cart.size());
        assertEquals(cartItem.getProductId(), cart.get(0).getProductId());
        assertEquals(cartItem.getQuantity(), cart.get(0).getQuantity());
    }

    @Test
    void removeItemFromCart_shouldRemoveItemIfItExists() {
        session.setAttribute("cart", new ArrayList<>(List.of(cartItem)));

        cartService.removeItemFromCart(cartItem.getProductId(), session);

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    void removeItemFromCart_shouldDoNothingIfCartIsEmpty() {
        cartService.removeItemFromCart(1L, session);

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        assertNull(cart); // No cart was set, so it should remain null
    }

    @Test
    void getCartProducts_shouldReturnPopulatedProductsList() {
        when(productService.getProductById(testProduct.getId())).thenReturn(testProduct);

        session.setAttribute("cart", List.of(cartItem));

        List<Product> products = cartService.getCartProducts(session);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(cartItem.getProductId(), products.get(0).getId());
        assertEquals(cartItem.getQuantity(), products.get(0).getStock());

        verify(productService, times(1)).getProductById(testProduct.getId());
    }

    @Test
    void getCartProducts_shouldThrowExceptionIfProductNotFound() {
        Long productId = 1L;

        session.setAttribute("cart", List.of(cartItem));

        when(productService.getProductById(productId)).thenThrow(new RuntimeException("Product not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cartService.getCartProducts(session));

        assertEquals("Something went wrong", exception.getMessage());
        verify(productService, times(1)).getProductById(productId);
    }
}
