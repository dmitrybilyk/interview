package com.conduct.interview.practise.spring.controller;

import com.conduct.interview.practise.spring.aop.with_params.UserService;
import com.conduct.interview.practise.spring.controller.multithreading.service.DeadlockDemoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.DeadlockFixedDemoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.DeadlockLockDemoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.DeadlockLockFixedDemoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.MyService;
import com.conduct.interview.practise.spring.controller.multithreading.service.ThreadInfo;
import com.conduct.interview.practise.spring.controller.multithreading.service.ThreadInfoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.ThreadStarvationDemoService;
import com.conduct.interview.practise.spring.controller.multithreading.service.VolatileDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/interview")
public class InterviewController {

  @Autowired
  private ThreadStarvationDemoService threadStarvationDemoService;
  @Autowired
  private MyService myService;
  @Autowired
  private ThreadInfoService service;
  @Autowired
  private VolatileDemoService volatileDemoService;
  @Autowired
  private DeadlockDemoService deadlockDemoService;
  @Autowired
  private DeadlockFixedDemoService deadlockFixedDemoService;
  @Autowired
  private DeadlockLockDemoService deadlockLockDemoService;
  @Autowired
  private DeadlockLockFixedDemoService deadlockLockFixedDemoService;

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

  @GetMapping("/volatile")
  @ResponseBody
  public String volatileCheck() throws InterruptedException {
//    String s = myService.sayHello();
      volatileDemoService.startTask();
//    userService.performAction(1L, "some action");
      Thread.sleep(3000); // wait so both threads can complete
    log.info("In Volatile thread name is - " + Thread.currentThread().getName());
    return "volatile";
  }

  @GetMapping("/deadlock")
  @ResponseBody
  public String deadlockCheck() throws InterruptedException {
      deadlockDemoService.startTask();
    log.info("In deadlock thread name is - " + Thread.currentThread().getName());
    return "deadlock";
  }

  @GetMapping("/deadlock-lock")
  @ResponseBody
  public String deadlockLockCheck() throws InterruptedException {
      deadlockLockDemoService.startTask();
    log.info("In deadlock thread name is - " + Thread.currentThread().getName());
    return "deadlock-lock";
  }

  @GetMapping("/deadlock-fixed")
  @ResponseBody
  public String deadlockFixedCheck() throws InterruptedException {
      deadlockFixedDemoService.startTask();
    log.info("In deadlock fixed thread name is - " + Thread.currentThread().getName());
    return "deadlock fixed ";
  }

  @GetMapping("/deadlock-lock-fixed")
  @ResponseBody
  public String deadlockLockFixedCheck() throws InterruptedException {
      deadlockLockFixedDemoService.startTask();
    log.info("In deadlock fixed thread name is - " + Thread.currentThread().getName());
    return "deadlock lock fixed ";
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

    @GetMapping("/thr-test")
    @ResponseBody
    public ThreadInfo threadInfo() {
        return service.handleRequest();
    }

    @GetMapping("/starvation")
    @ResponseBody
    public String starvation() {
        return threadStarvationDemoService.runBlockingTask();
    }

    @GetMapping("/starvation-fixed")
    @ResponseBody
    public CompletableFuture<String> starvationFixed() {
        return threadStarvationDemoService.runBlockingTaskFixed();
    }

}
