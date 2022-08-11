package com.ttop.spring.stock.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/status")
public class StatusController {
    
    private final Environment env;


    @Value("${test.prop}")
    private String testProp;

    
    @GetMapping("/alive")
    public String getAliveStatus() {
        return "alive";
    }
    @GetMapping("/profile")
    public String getProfile() {
        return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
    }
    @GetMapping("/profiles")
    public String getProfiles() {
        return Arrays.stream(env.getActiveProfiles()).reduce((s1,s2)->s1+","+s2).orElse("");
    }
    @GetMapping("/testprop")
    public String getTestProp() {
        return testProp;
    }
    
    


}