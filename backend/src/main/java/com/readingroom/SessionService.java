package com.readingroom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final ConcurrentHashMap<String, Integer> usersByToken = new ConcurrentHashMap<>();
    public String create(Integer userId) { String token = UUID.randomUUID().toString(); usersByToken.put(token, userId); return token; }
    public Integer userId(String token) { return usersByToken.get(token); }
    public void remove(String token) { usersByToken.remove(token); }
}
