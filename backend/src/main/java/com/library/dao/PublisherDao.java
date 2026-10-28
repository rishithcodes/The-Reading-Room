package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Publisher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PublisherDao {
    public List<Publisher> findAll(String search, int page, int pageSize) {
        List<Publisher> list = new ArrayList<>();
        String sql = "SELECT * FROM publishers WHERE name LIKE ? LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, search != null ? "%" + search + "%" : "%");
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public long countAll(String search) {
        String sql = "SELECT COUNT(*) FROM publishers WHERE name LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, search != null ? "%" + search + "%" : "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public Optional<Publisher> findById(int id) {
        String sql = "SELECT * FROM publishers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public int insert(Publisher publisher) {
        String sql = "INSERT INTO publishers (name, address, email, phone, website) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getEmail());
            stmt.setString(4, publisher.getPhone());
            stmt.setString(5, publisher.getWebsite());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return -1;
    }

    public boolean update(Publisher publisher) {
        String sql = "UPDATE publishers SET name = ?, address = ?, email = ?, phone = ?, website = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getEmail());
            stmt.setString(4, publisher.getPhone());
            stmt.setString(5, publisher.getWebsite());
            stmt.setInt(6, publisher.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM publishers WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean hasBooks(int publisherId) {
        String sql = "SELECT 1 FROM books WHERE publisher_id = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, publisherId);
            return stmt.executeQuery().next();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Publisher mapRow(ResultSet rs) throws SQLException {
        Publisher p = new Publisher();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setAddress(rs.getString("address"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        p.setWebsite(rs.getString("website"));
        return p;
    }
}
