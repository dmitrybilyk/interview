package org.design.designurlshortenerredirector.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public Mono<Void> sendNotificationEmail(String shortCode, String originalUrl) {
        log.info("Preparing notification email for shortCode: {}", shortCode);

        // 1. Create a SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();

        // **IMPORTANT**: You need to replace these with your actual email addresses
        String recipientEmail = "notification.target@example.com";
        String senderEmail = "no-reply@yourdomain.com";

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("URL Shortener Notification: Short Code Used");
        message.setText(
                String.format(
                        "The short code '%s' was resolved.\n" +
                                "Original URL: %s\n" +
                                "Timestamp: %s",
                        shortCode,
                        originalUrl,
                        java.time.LocalDateTime.now()
                )
        );

        // 2. Wrap the blocking operation in Mono.fromRunnable
        return Mono.fromRunnable(() -> {
                    try {
                        // This is the blocking call that must be executed off the event loop
                        mailSender.send(message);
                        log.info("Successfully sent notification email for shortCode: {}", shortCode);
                    } catch (Exception e) {
                        // Log the exception but let it be handled by the .doOnError/.onErrorResume
                        // in your calling service (ShortCodeResolverService).
                        log.error("Failed to send email via JavaMailSender for shortCode: {}", shortCode, e);
                        throw new RuntimeException("Email sending failed", e);
                    }
                })
                // 3. Offload the blocking task to a dedicated thread pool (Scheduler)
                .subscribeOn(Schedulers.boundedElastic())
                // 4. Return Mono<Void>
                .then();
    }
}
