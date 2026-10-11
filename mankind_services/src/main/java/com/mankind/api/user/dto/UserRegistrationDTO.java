package com.mankind.api.user.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UserRegistrationDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Map<String,String> customAttributes = new HashMap<>();
} 