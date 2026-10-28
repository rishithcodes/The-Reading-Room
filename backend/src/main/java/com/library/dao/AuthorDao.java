package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Author;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
    public List<Author> findAll(String search, int page, int pageSize) {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors WHERE name LIKE ? LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, search != null ? "%" + search + "%" : "%");
            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                authors.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return authors;
    }

    public long countAll(String search) {
        String sql = "SELECT COUNT(*) FROM authors WHERE name LIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, search != null ? "%" + search + "%" : "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public Author findById(int id) {
        String sql = "SELECT * FROM authors WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public int insert(Author author) {
        String sql = "INSERT INTO authors (name, biography) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getBiography());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return -1;
    }

    public boolean update(Author author) {
        String sql = "UPDATE authors SET name = ?, biography = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, author.getName());
            stmt.setString(2, author.getBiography());
            stmt.setInt(3, author.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    
    public boolean hasBooks(int authorId) {
        String sql = "SELECT 1 FROM books WHERE author_id = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            return stmt.executeQuery().next();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Author mapRow(ResultSet rs) throws SQLException {
        Author a = new Author();
        a.setId(rs.getInt("id"));
        a.setName(rs.getString("name"));
        a.setBiography(rs.getString("biography"));
        return a;
    }
}
