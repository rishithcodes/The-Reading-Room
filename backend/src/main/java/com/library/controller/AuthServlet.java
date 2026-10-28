package com.library.controller;

import com.library.service.AuthService;
import com.library.util.JsonResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";
        try {
            if (pathInfo.equals("/login")) {
                Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);
                Map<String, Object> res = authService.login(body.get("email"), body.get("password"));
                JsonResponse.sendSuccess(resp,  200,  res,  "Login successful");
            } else if (pathInfo.equals("/register")) {
                Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);
                authService.register(body.get("fullName"), body.get("email"), body.get("phone"), body.get("password"), body.get("department"), body.get("studentId"));
                JsonResponse.sendMessage(resp,  201,  "Registration successful");
            } else {
                JsonResponse.sendError(resp, 404, "Not found");
            }
        } catch (RuntimeException e) {
            JsonResponse.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonResponse.sendError(resp, 500, "Internal error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if ("/health".equals(pathInfo)) {
            JsonResponse.sendMessage(resp,  200,  "OK");
        } else if ("/me".equals(pathInfo)) {
            // Handled by AuthFilter typically setting user in request
            JsonResponse.sendSuccess(resp,  200,  req.getAttribute("user"),  "User info");
        }
    }
}
