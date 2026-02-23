package org.design.designurlshortenerredirector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Manually set properties if not relying on application.properties
        mailSender.setHost("smtp.mycustomserver.com");
        mailSender.setPort(25);
        mailSender.setUsername("customuser");
        // ... and so on

        return mailSender;
    }
}