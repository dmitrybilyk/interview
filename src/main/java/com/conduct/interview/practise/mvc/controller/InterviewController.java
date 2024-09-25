package com.conduct.interview.practise.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InterviewController {

  @GetMapping("/interview")
  @ResponseBody
  public String interview() {
    return "interview";
  }
}
