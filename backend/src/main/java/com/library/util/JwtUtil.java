package com.library.util;

import com.library.config.AppConfig;
import com.library.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT utility for generating and validating JSON Web Tokens.
 */
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private JwtUtil() {}

    private static SecretKey getSigningKey() {
        String secret = AppConfig.getJwtSecret();
        if (secret == null || secret.length() < 32) {
            logger.warn("JWT secret is too short. Using padded version. Change in production!");
            secret = String.format("%-64s", secret).replace(' ', '0');
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for a user.
     */
    public static String generateToken(User user) {
        int expirationHours = AppConfig.getJwtExpirationHours();
        long expMillis = System.currentTimeMillis() + (long) expirationHours * 3600 * 1000;

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",   user.getId());
        claims.put("email",    user.getEmail());
        claims.put("role",     user.getRole().name());
        claims.put("fullName", user.getFullName());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuer("library-management-system")
                .issuedAt(new Date())
                .expiration(new Date(expMillis))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT token and returns its claims.
     * Returns null if invalid or expired.
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.debug("JWT token expired: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the user ID from a validated claims object.
     */
    public static Integer getUserId(Claims claims) {
        return claims.get("userId", Integer.class);
    }

    /**
     * Extracts the role from a validated claims object.
     */
    public static User.Role getRole(Claims claims) {
        String roleStr = claims.get("role", String.class);
        try {
            return User.Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Extracts Bearer token from Authorization header.
     */
    public static String extractToken(jakarta.servlet.http.HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
