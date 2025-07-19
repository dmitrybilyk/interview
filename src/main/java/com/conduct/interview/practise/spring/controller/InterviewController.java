package com.conduct.interview.practise.spring.controller;

import com.conduct.interview.practise.spring.aop.with_params.UserService;
import com.conduct.interview.practise.spring.controller.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InterviewController {

  @Autowired
  private MyService myService;

  @Autowired
  private UserService userService;

  @GetMapping("/interview")
  @ResponseBody
  public String interview() {
    String s = myService.sayHello();
//    myService.sayHello();
    userService.performAction(1L, "some action");
    return "interview";
  }
}
