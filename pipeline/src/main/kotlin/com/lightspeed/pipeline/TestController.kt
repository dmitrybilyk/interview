package com.lightspeed.pipeline

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/result")
    fun getResult(): String {
        return "Result"
    }
}