package com.ecommerce.order.dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem {
  
    private Long orderItemId;
    private Long productId;
    private Long quantity;
    private Float priceAtTime;
    private Float subtotal;
    
    
}
