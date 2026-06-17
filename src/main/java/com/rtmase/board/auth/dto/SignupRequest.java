package com.rtmase.board.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "Please enter email.")
    @Email(message = "The email format is incorrect.")
    private String email;

    @NotBlank(message = "Please enter password.")
    @Size(min = 8, message = "Password muse be at least 8 characters long.")
    private String password;

    @NotBlank(message = "Please enter username.")
    private String username;
}
