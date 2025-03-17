package com.ecommerce.order.service;

import java.math.BigDecimal;

import com.ecommerce.order.dto.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeService {
    
    public String createPaymentIntent(Order order) throws StripeException{

        Long amount=BigDecimal.valueOf(order.getTotalAmount())
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();
       PaymentIntentCreateParams paymentIntentCreateParams= PaymentIntentCreateParams.builder()
        .setAmount(amount)
        .setCurrency(order.getCurrency())
        .setPaymentMethod("Cards")
        .putMetadata("order-id", order.getOrderId().toString())
        .putMetadata("username", order.getUsername())
        .build();

        PaymentIntent intent=PaymentIntent.create(paymentIntentCreateParams);

        return intent.getStatus();
    }
}
