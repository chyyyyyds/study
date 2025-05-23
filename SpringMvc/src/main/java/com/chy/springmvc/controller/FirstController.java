package com.chy.springmvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {
    @GetMapping("/a")
    public Object test(){
        System.out.println("test");
        return "Hello World";
    }
}
