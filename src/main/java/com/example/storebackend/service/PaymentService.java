package com.example.storebackend.service;

import com.example.storebackend.model.Order;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean processPayment(Order order) {
        // Simulate successful payment processing
        System.out.printf("Processing payment for order %d. Total amount: %.2f%n",
                order.getId(), order.getTotalPrice());
        return true; // Return true for successful payment
    }
}
