package com.yihong.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Value("${JmsMessageReceiver.name}")
    private String name;

    @GetMapping("/getQueueName")
    public String getQueueName() {
        System.out.println("调用了getQueueName");
        return "successCallback({\"queueName\":\""+name+"\"})";
    }

}
