package com.conduct.interview._3_spring._9_validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public class RegistrationService {

    public String register(@Valid SignupRequest request, @NotBlank String referralCode) {
        return "registered " + request.getUsername() + " with referral " + referralCode;
    }
}
