package com.conduct.interview._3_spring._9_validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "username must not be blank")
    @Size(min = 3, max = 20, message = "username must be 3-20 characters")
    private final String username;

    @Email(message = "must be a valid email address")
    private final String email;

    public SignupRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
