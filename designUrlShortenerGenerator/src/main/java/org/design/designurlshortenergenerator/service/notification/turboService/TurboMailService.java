package org.design.designurlshortenergenerator.service.notification.turboService;

import org.springframework.stereotype.Component;

@Component
public class TurboMailService {
    // Different method name and parameter order!
    public void dispatch(String recipient, String body, boolean turbo) {
        System.out.println("Sending with turbo");
    }
}