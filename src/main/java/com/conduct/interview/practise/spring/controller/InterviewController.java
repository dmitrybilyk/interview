package com.conduct.interview.practise.spring.controller;

import com.conduct.interview.practise.spring.aop.with_params.UserService;
import com.conduct.interview.practise.spring.controller.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class InterviewController {

  @Autowired
  private MyService myService;

  @Autowired
  private UserService userService;

  @GetMapping("/interview")
  @ResponseBody
  public String interview() {
//    String s = myService.sayHello();
    myService.incrementCounter();
//    userService.performAction(1L, "some action");
    log.info("In Controller thread name is - " + Thread.currentThread().getName());
    return "interview";
  }

  @GetMapping("/interview/reset")
  @ResponseBody
  public String reset() {
//    String s = myService.sayHello();
    myService.reset();
//    userService.performAction(1L, "some action");
    log.info("In Controller reset thread name is - " + Thread.currentThread().getName());
    return "interview";
  }
}
