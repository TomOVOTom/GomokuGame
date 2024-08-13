package com.example.gomoku;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    @Scheduled(fixedRate = 5000)
    @SendTo("/topic/test")
    public String sendTestMessage() {
        return "Test message at " + System.currentTimeMillis();
    }
}