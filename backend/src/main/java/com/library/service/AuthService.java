package com.library.service;

import com.library.dao.UserDao;
import com.library.model.User;
import com.library.util.JwtUtil;
import com.library.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service handling login and registration.
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDao userDao = new UserDao();

    /**
     * Authenticate a user by email and password.
     * Returns a map containing the JWT token and user data (without password hash).
     */
    public Map<String, Object> login(String email, String password) {
        Optional<User> optUser = userDao.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new RuntimeException("Invalid email or password.");
        }
        User user = optUser.get();
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password.");
        }
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new RuntimeException("Your account is not active. Please contact the library.");
        }
        String token = JwtUtil.generateToken(user);
        // Strip password hash before returning to frontend
        user.setPasswordHash(null);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    /**
     * Register a new student account.
     */
    public User register(String fullName, String email, String phone,
                         String password, String department, String studentId) {
        if (userDao.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters.");
        }
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(User.Role.STUDENT);
        user.setStatus(User.Status.ACTIVE);
        user.setDepartment(department);
        user.setStudentId(studentId);
        int id = userDao.insert(user);
        user.setId(id);
        user.setPasswordHash(null);
        return user;
    }
}
