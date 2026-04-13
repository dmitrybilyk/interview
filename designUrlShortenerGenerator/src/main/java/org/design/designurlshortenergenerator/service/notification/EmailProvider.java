package org.design.designurlshortenergenerator.service.notification;

public interface EmailProvider {
    void send(String recipient, String message);
}