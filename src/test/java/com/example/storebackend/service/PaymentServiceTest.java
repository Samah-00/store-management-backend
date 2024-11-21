package com.example.storebackend.service;

import com.example.storebackend.model.Order;
import com.example.storebackend.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testProcessPayment_Success() {
       Order mockOrder = new Order(1L, new User("testUser", "password"), null, 200.0);

        boolean result = paymentService.processPayment(mockOrder);

        assertTrue(result, "Payment should be processed successfully.");
    }
}
