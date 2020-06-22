package com.authentication.authenticationserver.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class HelloWorldController {

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("name","Umar Saidu");
        return "index";
    }

    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> firstPage(){
        Map<String,String> response = new HashMap<>();
        response.put("Message","We are Good" );
        response.put("Link URL","/api/auth/v1/hello" );
        response.put("Date", new Date().toString());
        return response;
    }
}
