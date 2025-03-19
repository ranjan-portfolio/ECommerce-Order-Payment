package com.ecommerce.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private Long productId;
    private Long quantity;
    private Float priceAtTime;
    private Float subtotal;
    @ManyToOne
    @JoinColumn(name="order_id",nullable = false)
    private Order order;
    
}
