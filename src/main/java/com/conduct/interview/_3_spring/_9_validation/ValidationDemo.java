package com.conduct.interview._3_spring._9_validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

public class ValidationDemo {

    public static void main(String[] args) {
        System.out.println("-- plain Bean Validation, no Spring involved --");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SignupRequest badRequest = new SignupRequest("ab", "not-an-email");
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(badRequest);
        violations.forEach(v -> System.out.println("  violation: " + v.getPropertyPath() + " - " + v.getMessage()));

        System.out.println();
        System.out.println("-- @Validated method, backed by an AOP proxy --");
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_9_validation/validation-context.xml")) {

            RegistrationService service = context.getBean(RegistrationService.class);

            SignupRequest goodRequest = new SignupRequest("alice", "alice@example.com");
            System.out.println("valid call   -> " + service.register(goodRequest, "REF123"));

            try {
                service.register(goodRequest, ""); // blank referral code, violates @NotBlank
            } catch (ConstraintViolationException e) {
                System.out.println("rejected before register() body ran -> " + e.getMessage());
            }
        }
    }
}
