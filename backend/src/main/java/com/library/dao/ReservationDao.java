package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public List<Reservation> findAll(Integer userId, Integer bookId, String status, int page, int pageSize) {
        List<Reservation> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM reservations WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (userId != null) { sql.append(" AND user_id = ?"); params.add(userId); }
        if (bookId != null) { sql.append(" AND book_id = ?"); params.add(bookId); }
        if (status != null && !status.isEmpty()) { sql.append(" AND status = ?"); params.add(status); }
        
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public long countAll(Integer userId, Integer bookId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM reservations WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (userId != null) { sql.append(" AND user_id = ?"); params.add(userId); }
        if (bookId != null) { sql.append(" AND book_id = ?"); params.add(bookId); }
        if (status != null && !status.isEmpty()) { sql.append(" AND status = ?"); params.add(status); }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public int insert(Reservation res) {
        String sql = "INSERT INTO reservations (user_id, book_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, res.getUserId());
            stmt.setInt(2, res.getBookId());
            stmt.setString(3, res.getStatus().name());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return -1;
    }

    public boolean updateStatus(int id, Reservation.Status status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean hasActiveReservation(int userId, int bookId) {
        String sql = "SELECT 1 FROM reservations WHERE user_id = ? AND book_id = ? AND status = 'ACTIVE' LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            return stmt.executeQuery().next();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean fulfillOldest(Connection conn, int bookId) throws SQLException {
        String sqlSelect = "SELECT id FROM reservations WHERE book_id = ? AND status = 'ACTIVE' ORDER BY created_at ASC LIMIT 1";
        try (PreparedStatement stmt1 = conn.prepareStatement(sqlSelect)) {
            stmt1.setInt(1, bookId);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                int resId = rs.getInt(1);
                String sqlUpdate = "UPDATE reservations SET status = 'FULFILLED' WHERE id = ?";
                try (PreparedStatement stmt2 = conn.prepareStatement(sqlUpdate)) {
                    stmt2.setInt(1, resId);
                    return stmt2.executeUpdate() > 0;
                }
            }
        }
        return false;
    }

    public long countActive() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setBookId(rs.getInt("book_id"));
        r.setStatus(Reservation.Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
        Timestamp tsU = rs.getTimestamp("updated_at");
        if (tsU != null) r.setUpdatedAt(tsU.toLocalDateTime());
        return r;
    }
}
