package com.library.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for writing consistent JSON responses to HttpServletResponse.
 */
public class JsonResponse {

    private static final Logger logger = LoggerFactory.getLogger(JsonResponse.class);
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonResponse() {}

    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Sends a success response with data.
     */
    public static void sendSuccess(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        send(resp, statusCode, response);
    }

    /**
     * Sends a success response with data and a message.
     */
    public static void sendSuccess(HttpServletResponse resp, int statusCode, Object data, String message) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        send(resp, statusCode, response);
    }

    /**
     * Sends a success response with only a message.
     */
    public static void sendMessage(HttpServletResponse resp, int statusCode, String message) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        send(resp, statusCode, response);
    }

    /**
     * Sends an error response.
     */
    public static void sendError(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", errorMessage);
        send(resp, statusCode, response);
    }

    /**
     * Sends a paginated list response.
     */
    public static void sendPaginated(HttpServletResponse resp, Object data, int page, int pageSize,
                                     long totalCount) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("totalCount", totalCount);
        response.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
        send(resp, 200, response);
    }

    private static void send(HttpServletResponse resp, int statusCode, Object body) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(mapper.writeValueAsString(body));
            writer.flush();
        }
    }

    /**
     * Parse JSON request body into a target class.
     */
    public static <T> T parseBody(jakarta.servlet.http.HttpServletRequest req, Class<T> clazz) throws IOException {
        return mapper.readValue(req.getInputStream(), clazz);
    }
}
