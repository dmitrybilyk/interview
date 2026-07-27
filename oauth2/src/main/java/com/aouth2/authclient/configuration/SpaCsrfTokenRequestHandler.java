package com.aouth2.authclient.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    
    // Використовуємо стандартний обробник як делегат
    private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        // Дозволяємо делегату обробити атрибути запиту (це важливо для захисту від BREACH атак)
        this.delegate.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        /*
         * 1. Якщо токен прийшов у стандартному HTTP заголовку (X-XSRF-TOKEN),
         *    використовуємо його значення. Це те, що робить React.
         */
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }

        /*
         * 2. Якщо заголовка немає, делегуємо перевірку стандартному обробнику
         *    (наприклад, якщо токен прийшов як параметр форми _csrf).
         */
        return this.delegate.resolveCsrfTokenValue(request, csrfToken);
    }
}