package com.codewithdondamzy.onlinestore.Exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message) ;
    }
}
