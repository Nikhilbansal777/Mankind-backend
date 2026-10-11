package com.mankind.matrix_wishlistservice.exception;

public class ItemNotInWishlistException extends RuntimeException {
    public ItemNotInWishlistException(String message) {
        super(message);
    }
}