package com.mankind.api.user.dto;


import com.mankind.api.user.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String,String> customAttributes = new HashMap<>();
    private boolean active;
    private String profilePictureUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}