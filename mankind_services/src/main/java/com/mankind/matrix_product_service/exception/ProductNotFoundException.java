package com.mankind.matrix_product_service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }

    public ProductNotFoundException(String category) {
        super("Product with category " + category + " not found");
    }
}
