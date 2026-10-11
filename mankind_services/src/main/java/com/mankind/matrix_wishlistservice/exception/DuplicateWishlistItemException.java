package com.mankind.matrix_wishlistservice.exception;

public class DuplicateWishlistItemException extends RuntimeException {
    public DuplicateWishlistItemException(String message) {
        super(message);
    }
}