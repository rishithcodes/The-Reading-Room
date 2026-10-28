package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Fine;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineDao {
    public Optional<Fine> findById(int id) {
        String sql = "SELECT * FROM fines WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public Optional<Fine> findByTransactionId(int transactionId) {
        String sql = "SELECT * FROM fines WHERE transaction_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    public List<Fine> findAll(String search, String status, Integer userId, int page, int pageSize) {
        List<Fine> fines = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.* FROM fines f JOIN users u ON f.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
            params.add("%" + search + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND f.status = ?");
            params.add(status);
        }
        if (userId != null) {
            sql.append(" AND f.user_id = ?");
            params.add(userId);
        }
        
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fines.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return fines;
    }

    public long countAll(String search, String status, Integer userId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM fines f JOIN users u ON f.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND u.full_name LIKE ?");
            params.add("%" + search + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND f.status = ?");
            params.add(status);
        }
        if (userId != null) {
            sql.append(" AND f.user_id = ?");
            params.add(userId);
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public int insert(Fine fine) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return insertWithConn(conn, fine);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertWithConn(Connection conn, Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (transaction_id, user_id, amount, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, fine.getTransactionId());
            stmt.setInt(2, fine.getUserId());
            stmt.setBigDecimal(3, fine.getAmount());
            stmt.setString(4, fine.getStatus() != null ? fine.getStatus().name() : Fine.Status.PENDING.name());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public boolean updateStatus(int fineId, Fine.Status status) {
        String sql = "UPDATE fines SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, fineId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean updatePaidAmount(int fineId, BigDecimal newPaidAmount, Fine.Status newStatus) {
        String sql = "UPDATE fines SET paid_amount = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPaidAmount);
            stmt.setString(2, newStatus.name());
            stmt.setInt(3, fineId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public long countPending() {
        String sql = "SELECT COUNT(*) FROM fines WHERE status IN ('PENDING', 'PARTIALLY_PAID')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public BigDecimal getTotalPendingAmount() {
        String sql = "SELECT SUM(amount - paid_amount) FROM fines WHERE status IN ('PENDING', 'PARTIALLY_PAID')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal(1);
                return val != null ? val : BigDecimal.ZERO;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return BigDecimal.ZERO;
    }

    public List<Object[]> getFineCollectionByMonth() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') as month, SUM(paid_amount) as amount FROM fines WHERE paid_amount > 0 GROUP BY month ORDER BY month DESC LIMIT 12";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getString("month"), rs.getBigDecimal("amount")});
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine();
        f.setId(rs.getInt("id"));
        f.setTransactionId(rs.getInt("transaction_id"));
        f.setUserId(rs.getInt("user_id"));
        f.setAmount(rs.getBigDecimal("amount"));
        f.setPaidAmount(rs.getBigDecimal("paid_amount"));
        f.setStatus(Fine.Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) f.setCreatedAt(ts.toLocalDateTime());
        Timestamp tsU = rs.getTimestamp("updated_at");
        if (tsU != null) f.setUpdatedAt(tsU.toLocalDateTime());
        return f;
    }
}
