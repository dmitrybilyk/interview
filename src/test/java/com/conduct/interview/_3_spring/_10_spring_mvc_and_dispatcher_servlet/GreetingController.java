package com.conduct.interview._3_spring._10_spring_mvc_and_dispatcher_servlet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class GreetingController {

    @GetMapping("/greet")
    @ResponseBody
    public String greet(@RequestParam String name) {
        return "Hello, " + name;
    }
}
