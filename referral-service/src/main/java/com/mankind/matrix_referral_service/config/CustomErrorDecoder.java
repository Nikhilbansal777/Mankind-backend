package com.mankind.matrix_referral_service.config;

import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        log.error("Feign client error for method: {}, status: {}, reason: {}",
                methodKey, response.status(), response.reason());
        return switch (response.status()) {
            case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request to user service");
            case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to user service");
            case 403 -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden to user service");
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            default -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User service error");
        };
    }
}
