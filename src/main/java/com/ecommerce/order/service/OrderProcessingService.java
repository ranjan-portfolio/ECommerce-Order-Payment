package com.ecommerce.order.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecommerce.order.dto.Order;
import com.stripe.exception.StripeException;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class OrderProcessingService {

    @Autowired
    private StripeService stripeService;

    @KafkaListener(topics = "order",groupId="order-processing-group")
    public void consume(ConsumerRecord<String,Object> record) {
        try{
            Order order=(Order) record.value();
            stripeService.createPaymentIntent(order);
        }catch(StripeException ex){

        }
        

        //get the value from record and call stripe service for payment
        //if payment successfuly done
            // send a notification to vendor with order details
            // send a notification to customer by email
        //else
            //send a failed notification to customer with stripe error message    
    }
    
}
