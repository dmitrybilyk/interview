package org.design.designurlshortenergenerator.service.notification;

import org.design.designurlshortenergenerator.service.notification.turboService.TurboMailService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TurboMailAdapter implements EmailProvider {
    private TurboMailService turboMailService;

    public TurboMailAdapter(TurboMailService turboMail) {
        this.turboMailService = turboMail;
    }

    @Override
    public void send(String recipient, String message) {
        // Translating our 'send' call to their 'dispatch' call
        turboMailService.dispatch(message, recipient, true);
    }
}