package com.example.storebackend.service;

import com.example.storebackend.model.Order;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

    public boolean processPayment(Order order) {
        // Simulate successful payment processing
        log.info(String.format("Processing payment for order ID %d. Total amount: %.2f",
                    order.getId(), order.getTotalPrice()));
        return true; // Return true for successful payment
    }
}
