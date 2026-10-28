package com.readingroom;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiRootController {
    @GetMapping("/api")
    public Map<String,Object> root() {
        return Map.of("success", true, "message", "The Reading Room API is running", "data", Map.of("status", "UP"));
    }
}
