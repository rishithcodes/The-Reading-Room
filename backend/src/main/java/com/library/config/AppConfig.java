package com.library.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads and provides access to application.properties configuration.
 * Centralizes all configuration so credentials are never hardcoded.
 */
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties properties = new Properties();
    private static volatile boolean loaded = false;

    static {
        loadProperties();
    }

    private AppConfig() {}

    private static void loadProperties() {
        try (InputStream is = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is != null) {
                properties.load(is);
                loaded = true;
                logger.info("Application properties loaded successfully.");
            } else {
                logger.error("application.properties not found on classpath!");
            }
        } catch (IOException e) {
            logger.error("Failed to load application.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key, "");
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer config for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    public static long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long config for key '{}': {}", key, value);
            return defaultValue;
        }
    }

    // Convenience getters
    public static String getDbUrl()       { return get("db.url"); }
    public static String getDbUsername()  { return get("db.username"); }
    public static String getDbPassword()  { return get("db.password"); }
    public static String getDbDriver()    { return get("db.driver"); }
    public static String getJwtSecret()   { return get("jwt.secret"); }
    public static int    getJwtExpirationHours() { return getInt("jwt.expiration.hours", 24); }
    public static String getCorsAllowedOrigins() { return get("cors.allowed.origins", "*"); }
}
