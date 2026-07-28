package com.qeat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/operator/test")
    public String operator() {
        return "operator ok";
    }

    @GetMapping("/admin/test")
    public String admin() {
        return "admin ok";
    }
}
