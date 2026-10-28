package com.library.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Input validation utility.
 */
public class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) return false;
        return email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (StringUtils.isBlank(phone)) return true; // phone is optional
        return phone.matches("^[+\\d\\-\\s()]{7,20}$");
    }

    public static boolean isNotBlank(String value) {
        return StringUtils.isNotBlank(value);
    }

    public static boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    public static boolean isNonNegative(Integer value) {
        return value != null && value >= 0;
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim();
    }

    public static boolean isValidYear(Integer year) {
        if (year == null) return true; // optional
        return year >= 1000 && year <= 2100;
    }

    public static boolean isValidIsbn(String isbn) {
        if (StringUtils.isBlank(isbn)) return true; // optional
        String cleaned = isbn.replaceAll("[\\-\\s]", "");
        return cleaned.matches("\\d{9}[\\dX]") || cleaned.matches("\\d{13}");
    }
}
