package com.mankind.mankindmatrixuserservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class KeycloakUnavailableException extends RuntimeException {

    public KeycloakUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
