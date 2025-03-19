package com.ecommerce.order.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.dao.OrderRepository;
import com.ecommerce.order.dto.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.entity.PaymentStatus;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.stripe.exception.StripeException;

import jakarta.persistence.OptimisticLockException;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class OrderProcessingService {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private OrderRepository orderRepository;

    private final String ORDER_PAYMENT_SUCCESS="succeeded";
    private final String ORDER_PAYMENT_FAILED="requires_payment_method";
    private final String CUSTOMER_TOPIC="customer";
    private final String VENDOR_TOPIC="vendor";

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @KafkaListener(topics = "order",groupId="order-processing-group")
    @Retryable(
        value = OptimisticLockException.class, 
        maxAttempts = 3, // Retry up to 3 times
        backoff = @Backoff(delay = 500) // Wait 500ms before retrying
    )
    @Transactional
    public void consume(ConsumerRecord<String,Object> record) throws StripeException,OrderNotFoundException {

       
            String status="";
        
            Order order=(Order) record.value();

            status=stripeService.createPaymentIntent(order);

            com.ecommerce.order.entity.Order currentOrder=orderRepository.findById(order.getOrderId())
                                                 .orElseThrow( () ->  new OrderNotFoundException("No order found for the provided order id::"));

            if(status!=null && status.equals(ORDER_PAYMENT_SUCCESS)){
               
                currentOrder.setOrderStatus(OrderStatus.PROCESSING);
                currentOrder.setPaymentStatus(PaymentStatus.PAID);
                orderRepository.save(currentOrder);

                //Use Kafka to send notification to notification service to vendor (vendor group)
                kafkaTemplate.send(VENDOR_TOPIC,order.getOrderId().toString(),currentOrder);


                //Use kafka to send notification to notification service to customer (customer group)
                kafkaTemplate.send(CUSTOMER_TOPIC, order.getOrderId().toString(),currentOrder);
                                
            }
            else if(status!=null && status.equals(ORDER_PAYMENT_FAILED)){
                currentOrder.setOrderStatus(OrderStatus.CANCELLED);
                currentOrder.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(currentOrder);
                //Use kafka to send notification to notification service to customer (customer group)
                kafkaTemplate.send(CUSTOMER_TOPIC, currentOrder);
            }
            else{
                System.out.println("Order status recieved for which no handling is available"+status);
            }

        
        
        //else
            //send a failed notification to customer with stripe error message    
    }
    
    
}
