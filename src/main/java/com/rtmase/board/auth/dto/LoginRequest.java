package com.rtmase.board.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "Please enter email.")
    @Email(message = "The email format is incorrect.")
    private String email;

    @NotBlank(message = "Please enter password.")
    private String password;
}
