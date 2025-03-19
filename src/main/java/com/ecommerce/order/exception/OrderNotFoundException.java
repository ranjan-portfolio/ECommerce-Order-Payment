package com.ecommerce.order.exception;



public class OrderNotFoundException extends Exception{
    
    public OrderNotFoundException(String msg){
        super(msg);
    }

    public OrderNotFoundException(String msg,Throwable th){
        super(msg,th);
    }
}
