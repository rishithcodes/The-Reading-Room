package com.library.filter;

import com.library.model.User;
import com.library.util.JsonResponse;
import com.library.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT authentication filter.
 * Validates Bearer tokens on all protected API routes.
 * Public routes (login, register) are exempted.
 */
@WebFilter(urlPatterns = "/api/*")
public class AuthFilter implements Filter {

    // These paths do NOT require authentication
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/health"
    ));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Allow preflight and public paths without auth
        if ("OPTIONS".equalsIgnoreCase(req.getMethod()) || isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract and validate JWT
        String token = JwtUtil.extractToken(req);
        if (token == null) {
            JsonResponse.sendError(resp, 401, "Authentication required. Please login.");
            return;
        }

        Claims claims = JwtUtil.validateToken(token);
        if (claims == null) {
            JsonResponse.sendError(resp, 401, "Invalid or expired token. Please login again.");
            return;
        }

        // Attach user context to request for downstream servlets
        req.setAttribute("userId", JwtUtil.getUserId(claims));
        req.setAttribute("userRole", JwtUtil.getRole(claims));
        req.setAttribute("userEmail", claims.getSubject());
        req.setAttribute("claims", claims);

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path);
    }

    /**
     * Helper for controllers to check if caller has required role.
     */
    public static boolean hasRole(HttpServletRequest req, User.Role... requiredRoles) {
        User.Role callerRole = (User.Role) req.getAttribute("userRole");
        if (callerRole == null) return false;
        for (User.Role r : requiredRoles) {
            if (callerRole == r) return true;
        }
        return false;
    }

    /**
     * Returns the authenticated user's ID from request attributes.
     */
    public static Integer getAuthUserId(HttpServletRequest req) {
        Object id = req.getAttribute("userId");
        return id instanceof Integer ? (Integer) id : null;
    }

    /**
     * Returns the authenticated user's role.
     */
    public static User.Role getAuthRole(HttpServletRequest req) {
        return (User.Role) req.getAttribute("userRole");
    }
}
