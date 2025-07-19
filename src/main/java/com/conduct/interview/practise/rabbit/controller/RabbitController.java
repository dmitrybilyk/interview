package com.conduct.interview.practise.rabbit.controller;

import com.conduct.interview.practise.rabbit.service.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@Profile("rabbit")
public class RabbitController {

  @Autowired private MessageProducer messageProducer;

  @GetMapping("/rabbit/send")
  @ResponseBody
  public String interview() {
    messageProducer.sendMessage("This is my rabbit message");
    return "rabbit message sent";
  }
}
