package com.rate.rate_limit_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
public class RateLimitController {
    @GetMapping("/user/{id}")
    public String getInfo(@PathVariable("id") String id) {
        return "Hello " + id;
    }
}
