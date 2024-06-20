package com.example.storebackend.model;

import lombok.Data;

@Data
public class CartItem {
    Long productId;
    int quantity;
}
