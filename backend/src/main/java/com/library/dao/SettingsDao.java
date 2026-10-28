package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.LibrarySetting;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsDao {
    public List<LibrarySetting> findAll() {
        List<LibrarySetting> list = new ArrayList<>();
        String sql = "SELECT * FROM library_settings";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public Optional<LibrarySetting> findByKey(String key) {
        String sql = "SELECT * FROM library_settings WHERE `key` = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public boolean update(String key, String value) {
        String sql = "UPDATE library_settings SET value = ? WHERE `key` = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, key);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getIntValue(String key, int defaultValue) {
        return findByKey(key).map(s -> {
            try { return Integer.parseInt(s.getValue()); } 
            catch (NumberFormatException e) { return defaultValue; }
        }).orElse(defaultValue);
    }

    public BigDecimal getDecimalValue(String key, BigDecimal defaultValue) {
        return findByKey(key).map(s -> {
            try { return new BigDecimal(s.getValue()); }
            catch (Exception e) { return defaultValue; }
        }).orElse(defaultValue);
    }

    private LibrarySetting mapRow(ResultSet rs) throws SQLException {
        LibrarySetting s = new LibrarySetting();
        s.setId(rs.getInt("id"));
        s.setKey(rs.getString("key"));
        s.setValue(rs.getString("value"));
        s.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) s.setUpdatedAt(ts.toLocalDateTime());
        return s;
    }
}
