package com.conduct.interview._3_spring._13_spring_security;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_13_spring_security/security-context.xml")) {

            AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);

            System.out.println("-- correct password --");
            authenticate(authenticationManager, "alice", "secret123");

            System.out.println("-- wrong password --");
            authenticate(authenticationManager, "alice", "wrong-password");

            System.out.println("-- unknown user --");
            authenticate(authenticationManager, "mallory", "whatever");
        }
    }

    private static void authenticate(AuthenticationManager authenticationManager, String username, String password) {
        Authentication request = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication result = authenticationManager.authenticate(request);
            System.out.println("  SUCCESS: authenticated=" + result.isAuthenticated()
                    + ", authorities=" + result.getAuthorities());
        } catch (BadCredentialsException e) {
            System.out.println("  REJECTED: " + e.getMessage());
        }
    }
}
