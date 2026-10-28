package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User operations.
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    /**
     * Find user by email (used for login).
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Find user by ID.
     */
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Find all users with optional search and role filter.
     */
    public List<User> findAll(String search, String role, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (full_name LIKE ? OR email LIKE ? OR student_id LIKE ?)");
            String like = "%" + search.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role.toUpperCase());
        }
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users: {}", e.getMessage(), e);
        }
        return users;
    }

    /**
     * Count all users matching filter (for pagination).
     */
    public long countAll(String search, String role) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (full_name LIKE ? OR email LIKE ? OR student_id LIKE ?)");
            String like = "%" + search.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role.toUpperCase());
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting users: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Insert a new user and return the generated ID.
     */
    public int insert(User user) {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role, department, student_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getDepartment());
            ps.setString(7, user.getStudentId());
            ps.setString(8, user.getStatus() != null ? user.getStatus().name() : "ACTIVE");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Update user profile (without changing password or role).
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, department=?, student_id=?, status=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getDepartment());
            ps.setString(5, user.getStudentId());
            ps.setString(6, user.getStatus().name());
            ps.setInt(7, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update only the password hash.
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deactivate (soft delete) a user.
     */
    public boolean deactivate(int userId) {
        String sql = "UPDATE users SET status='INACTIVE', updated_at=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deactivating user: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get count of active borrowed books for a user.
     */
    public int getActiveBorrowCount(int userId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id=? AND status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error getting borrow count: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Count users by role.
     */
    public long countByRole(User.Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role=? AND status='ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting by role: {}", e.getMessage(), e);
        }
        return 0;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setDepartment(rs.getString("department"));
        u.setStudentId(rs.getString("student_id"));
        u.setStatus(User.Status.valueOf(rs.getString("status")));
        u.setProfileImage(rs.getString("profile_image"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) u.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) u.setUpdatedAt(ua.toLocalDateTime());
        return u;
    }
}
