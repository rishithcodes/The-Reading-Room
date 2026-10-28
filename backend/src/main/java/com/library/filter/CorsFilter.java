package com.library.filter;

import com.library.config.AppConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CORS filter applied to all /api/* endpoints.
 * Allows the React frontend to communicate with the backend.
 */
@WebFilter(urlPatterns = "/api/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String allowedOrigins = AppConfig.getCorsAllowedOrigins();

        // Echo back the specific origin if it's in the allowed list
        String origin = req.getHeader("Origin");
        if (origin != null) {
            for (String allowed : allowedOrigins.split(",")) {
                if (origin.trim().equalsIgnoreCase(allowed.trim())) {
                    resp.setHeader("Access-Control-Allow-Origin",      origin);
                    resp.setHeader("Access-Control-Allow-Credentials", "true");
                    break;
                }
            }
        }

        resp.setHeader("Access-Control-Allow-Methods",
                AppConfig.get("cors.allowed.methods", "GET,POST,PUT,DELETE,OPTIONS"));
        resp.setHeader("Access-Control-Allow-Headers",
                AppConfig.get("cors.allowed.headers", "Content-Type,Authorization,X-Requested-With"));
        resp.setHeader("Access-Control-Max-Age",
                AppConfig.get("cors.max.age", "3600"));

        // Preflight — respond immediately without processing
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
