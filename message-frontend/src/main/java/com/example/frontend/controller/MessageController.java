package com.example.frontend.controller;

import com.example.frontend.service.JmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final JmsService jmsService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/api/messages")
    @ResponseBody
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        Map<String, Object> response = new HashMap<>();

        try {
            jmsService.sendMessage(content);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }
}