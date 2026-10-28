package com.readingroom;

import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final SessionService sessions;

    public AuthController(UserRepository users, SessionService sessions) { this.users = users; this.sessions = sessions; }

    @GetMapping("/health")
    public Map<String,Object> health() { return Map.of("success", true, "message", "The Reading Room API is running", "data", Map.of("status", "UP")); }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> input) {
        String email = input.getOrDefault("email", "").trim();
        String password = input.getOrDefault("password", "");
        User user = users.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !"ACTIVE".equals(user.getStatus()) || !matches(password, user.getPasswordHash()))
            return error(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        String token = sessions.create(user.getId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Login successful", "data", Map.of("token", token, "user", publicUser(user))));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody Map<String,String> input) {
        String email = input.getOrDefault("email", "").trim().toLowerCase();
        String password = input.getOrDefault("password", "");
        String name = input.getOrDefault("fullName", "").trim();
        String studentId = input.getOrDefault("studentId", "").trim();
        String department = input.getOrDefault("department", "").trim();
        if (name.isBlank() || email.isBlank() || studentId.isBlank() || department.isBlank() || password.length() < 8)
            return error(HttpStatus.BAD_REQUEST, "Name, email, student ID, department, and a password of at least 8 characters are required");
        if (users.existsByEmailIgnoreCase(email)) return error(HttpStatus.CONFLICT, "An account with this email already exists");
        if (users.existsByStudentId(studentId)) return error(HttpStatus.CONFLICT, "An account with this student ID already exists");
        User user = new User();
        user.setFullName(name); user.setEmail(email); user.setPhone(input.get("phone"));
        user.setDepartment(department); user.setStudentId(studentId);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt(12)));
        users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "Student account created", "data", publicUser(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value="Authorization", required=false) String authorization) {
        User user = userFrom(authorization);
        return user == null ? error(HttpStatus.UNAUTHORIZED, "Please sign in") : ResponseEntity.ok(Map.of("success", true, "message", "Current user", "data", publicUser(user)));
    }

    @PostMapping("/logout")
    public Map<String,Object> logout(@RequestHeader(value="Authorization", required=false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) sessions.remove(authorization.substring(7));
        return Map.of("success", true, "message", "Signed out", "data", Map.of());
    }

    private User userFrom(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return null;
        Integer id = sessions.userId(authorization.substring(7));
        return id == null ? null : users.findById(id).filter(u -> "ACTIVE".equals(u.getStatus())).orElse(null);
    }
    private boolean matches(String raw, String hash) {
        if (hash == null || hash.length() != 60) return false;
        try { return BCrypt.checkpw(raw, hash); }
        catch (IllegalArgumentException | IndexOutOfBoundsException ex) { return false; }
    }
    private Map<String,Object> publicUser(User u) {
        return Map.of("id", u.getId(), "fullName", u.getFullName(), "email", u.getEmail(), "role", u.getRole(),
                "phone", u.getPhone() == null ? "" : u.getPhone(), "department", u.getDepartment() == null ? "" : u.getDepartment(),
                "studentId", u.getStudentId() == null ? "" : u.getStudentId());
    }
    private ResponseEntity<Map<String,Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message, "error", message));
    }
}
