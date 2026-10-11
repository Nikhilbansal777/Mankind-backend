package com.mankind.api.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String username;
    private String newPassword;
    private Boolean temporary;
}
