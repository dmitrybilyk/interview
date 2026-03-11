package org.design.designurlshortenergenerator.service.notification;

import org.springframework.stereotype.Component;

@Component
public class EmailProviderImpl implements EmailProvider {
    @Override
    public void send(String recipient, String message) {
        System.out.println(recipient + " " + message);
    }
}
