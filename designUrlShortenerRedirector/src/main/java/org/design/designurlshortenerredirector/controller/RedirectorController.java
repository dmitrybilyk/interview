package org.design.designurlshortenerredirector.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redirect")
public class RedirectorController {

    /**
     * Endpoint, який слухає Redirector.
     * Generator Service буде надсилати сюди запити.
     * @return Просте підтвердження.
     */
    @GetMapping("/status")
    public String getStatus() {
        System.out.println("Redirector received a REST request.");
        
        // Тут могла б бути складна логіка, наприклад, збереження посилання
        
        return "Redirector: Status is OK! Link registered successfully.";
    }
}