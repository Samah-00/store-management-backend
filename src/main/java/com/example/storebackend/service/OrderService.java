package com.example.storebackend.service;

import com.example.storebackend.model.*;
import com.example.storebackend.repository.OrderRepository;
import com.example.storebackend.repository.ProductRepository;
import com.example.storebackend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Order createOrder(List<CartItem> cart, Long userId) {
        try {
            Order order = new Order();
            double totalPrice = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            User user = userRepository.findById(userId).orElseThrow(() -> {
                log.error(String.format("User with ID %d not found.", userId));
                return new IllegalArgumentException("User not found.");
            });

            for (CartItem cartItem : cart) {
                Product requestedProduct = productRepository.findById(cartItem.getProductId()).orElseThrow(() -> {
                    log.error(String.format("Product with ID %d not found.", cartItem.getProductId()));
                    return new IllegalArgumentException("Product not found.");
                });

                if (cartItem.getQuantity() > requestedProduct.getStock()) {
                    String message = String.format("Not enough stock for product %s", requestedProduct.getName());
                    log.error(message);
                    throw new IllegalArgumentException(message);
                }

                totalPrice += cartItem.getQuantity() * requestedProduct.getPrice();
                // Update product stock
                requestedProduct.setStock(requestedProduct.getStock() - cartItem.getQuantity());
                productRepository.save(requestedProduct);
                // Create and add order item
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .productId(requestedProduct.getId())
                        .quantity(cartItem.getQuantity())
                        .price(requestedProduct.getPrice())
                        .build();
                orderItems.add(orderItem);
            }

            // Create and save new order
            order.setUser(user);
            order.setOrderItems(orderItems);
            order.setTotalPrice(totalPrice);
            orderRepository.save(order);
            log.info(String.format("Order for user ID %d created successfully with total price %.2f.", userId, totalPrice));

            return order;
        } catch (Exception e) {
            log.error(String.format("Error while creating order: %s", e.getMessage()));

            throw new RuntimeException("Error while creating order", e);
        }
    }

    public Order getOrderById(Long orderId) {
        try {
            return orderRepository.findById(orderId).orElseThrow(() -> {
                log.error(String.format("Order with ID %d not found.", orderId));
                return new IllegalArgumentException("Order not found.");
            });
        } catch (Exception e) {
            log.error(String.format("Error getting order with ID %d: %s", orderId, e.getMessage()));
           
            throw new RuntimeException("Error getting order", e);
        }
    }

    public List<Order> getUserOrders(Long userId) {
        try {
            return orderRepository.findByUserId(userId);
        } catch (Exception e) {
            log.error(String.format("Error getting orders for user with ID %d: %s", userId, e.getMessage()));
            if (Objects.equals(e.getMessage(), "No value present")) {
                throw new IllegalArgumentException("Product not found.");
            }
            throw new RuntimeException("Error getting user orders", e);
        }
    }

    public List<Order> getOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            log.error(String.format("Error getting all orders: %s", e.getMessage()));
            throw new RuntimeException("Error getting all orders", e);
        }
    }
}
