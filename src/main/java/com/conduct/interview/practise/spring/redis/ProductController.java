package com.conduct.interview.practise.spring.redis;

import com.conduct.interview.practise.spring.conditional.ConditionalBean;
import com.conduct.interview.practise.spring.events.CreateOrderEvent;
import com.conduct.interview.practise.spring.events.OrderCreateEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ProductController {

    private final Optional<ConditionalBean> conditionalBean;
    private final OrderCreateEventPublisher orderCreateEventPublisher;

    private final ProductService productService;

    public ProductController(ProductService productService, Optional<ConditionalBean> conditionalBean,
                             OrderCreateEventPublisher orderCreateEventPublisher) {
        this.productService = productService;
        this.conditionalBean = conditionalBean;
        this.orderCreateEventPublisher = orderCreateEventPublisher;
    }

    @GetMapping("/redis/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        conditionalBean.ifPresent(ConditionalBean::sayHello);
        orderCreateEventPublisher.publish(new CreateOrderEvent("some order id"));
        return productService.getProductById(id);
    }
}
