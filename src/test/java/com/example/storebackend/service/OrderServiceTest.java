package com.example.storebackend.service;

import com.example.storebackend.model.*;
import com.example.storebackend.repository.OrderRepository;
import com.example.storebackend.repository.ProductRepository;
import com.example.storebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product1, product2;
    private CartItem cartItem1, cartItem2;

    @BeforeEach
    public void setUp() {
        user = new User("testUser", "password");
        user.setId(1L);

        product1 = new Product(1L, "Product1", 100.0, 10);
        product2 = new Product(2L, "Product2", 200.0, 5);

        cartItem1 = new CartItem(1L, 3); // product1, quantity 3
        cartItem2 = new CartItem(2L, 2); // product2, quantity 2
    }

    @Test
    public void testCreateOrder_Success() {
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(java.util.Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        Order order = orderService.createOrder(cartItems, 1L);

        assertNotNull(order);
        assertEquals(1L, order.getUser().getId());
        assertEquals(2, order.getOrderItems().size());
        assertEquals(700.0, order.getTotalPrice()); // (3 * 100) + (2 * 200)
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(cartItems, 1L);
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    public void testCreateOrder_ProductNotFound() {
        List<CartItem> cartItems = Arrays.asList(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(cartItems, 1L);
        });
        assertEquals("Product not found.", exception.getMessage());
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Set the product stock to 1, while cart requires 3
        product1.setStock(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(Arrays.asList(cartItem1), 1L);
        });
        assertTrue(exception.getMessage().contains("Not enough stock"));
    }

    @Test
    public void testGetOrderById_Success() {
        Order order = new Order(1L, user, null, 200.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderById(1L);
        });
        assertEquals("Order not found.", exception.getMessage());
    }

    @Test
    public void testGetUserOrders_Success() {
        List<Order> orders = Arrays.asList(new Order(1L, user, null, 200.0));

        when(orderRepository.findByUserId(user.getId())).thenReturn(orders);

        List<Order> userOrders = orderService.getUserOrders(user.getId());

        assertNotNull(userOrders);
        assertFalse(userOrders.isEmpty());
        assertEquals(1L, orders.get(0).getId());
    }

    @Test
    public void testGetUserOrders_NotFound() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        List<Order> orders = orderService.getUserOrders(1L);

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testGetOrders_Success() {
        List<Order> orders = Arrays.asList(new Order(1L, user, null, 200.0));

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> allOrders = orderService.getOrders();

        // Assert: Verify the result
        assertNotNull(allOrders);
        assertEquals(1, allOrders.size());
        assertEquals(1L, allOrders.get(0).getId());
    }
}

