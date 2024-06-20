package com.example.storebackend.service;

import com.example.storebackend.repository.OrderItemRepository;
import com.example.storebackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Order-related business logic
}
