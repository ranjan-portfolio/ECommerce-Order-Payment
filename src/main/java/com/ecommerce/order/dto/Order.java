package com.ecommerce.order.dto;

import java.security.Timestamp;
import java.util.List;

import com.ecommerce.order.constants.OrderStatus;
import com.ecommerce.order.constants.PaymentStatus;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Order {
  
    private Long orderId;
    private String username;
    private Float totalAmount;
    private String currency;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private Timestamp createdAt;
    private Timestamp updatedAt;
  
    private List<OrderItem> orderItemList;
    

}
