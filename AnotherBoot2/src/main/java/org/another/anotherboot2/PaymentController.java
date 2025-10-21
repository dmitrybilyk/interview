package org.another.anotherboot2;

import org.another.anotherbootshared.model.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/pay")
public class PaymentController {
    private final Random rnd = new Random();
    private final List<String> history = new ArrayList<>();

    @PostMapping
    public String pay(@RequestBody Order order) {
        int chance = rnd.nextInt(100);
        if (chance < 70) {
            history.add("Payment OK for " + order.getId());
            return "OK";
        }
        history.add("Payment FAIL for " + order.getId());
        throw new RuntimeException("Transient payment error");
    }

    @GetMapping("/history")
    public List<String> getHistory() {
        return history;
    }
}
