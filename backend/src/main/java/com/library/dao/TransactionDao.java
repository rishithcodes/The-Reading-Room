package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for Transaction (book issue/return) operations.
 */
public class TransactionDao {

    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private static final String SELECT_WITH_JOINS =
        "SELECT t.*, u.full_name AS user_name, u.email AS user_email, " +
        "b.title AS book_title, b.isbn AS book_isbn, " +
        "ib.full_name AS issued_by_name, rb.full_name AS returned_by_name, " +
        "CASE WHEN t.status IN ('ISSUED','OVERDUE') AND CURDATE() > t.due_date " +
        "     THEN DATEDIFF(CURDATE(), t.due_date) ELSE 0 END AS overdue_days " +
        "FROM transactions t " +
        "JOIN users u ON t.user_id = u.id " +
        "JOIN books b ON t.book_id = b.id " +
        "LEFT JOIN users ib ON t.issued_by = ib.id " +
        "LEFT JOIN users rb ON t.returned_by = rb.id ";

    public Optional<Transaction> findById(int id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_WITH_JOINS + "WHERE t.id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding transaction by id: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Find active (ISSUED or OVERDUE) transaction for user + book.
     */
    public Optional<Transaction> findActiveByUserAndBook(int userId, int bookId) {
        String sql = SELECT_WITH_JOINS + "WHERE t.user_id=? AND t.book_id=? AND t.status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active transaction: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Transaction> findAll(String search, String status, Integer userId,
                                      Integer bookId, String dateFrom, String dateTo,
                                      int page, int pageSize) {
        StringBuilder sql = new StringBuilder(SELECT_WITH_JOINS + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, search, status, userId, bookId, dateFrom, dateTo);
        sql.append(" ORDER BY t.created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding transactions: {}", e.getMessage(), e);
        }
        return list;
    }

    public long countAll(String search, String status, Integer userId, Integer bookId,
                          String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM transactions t JOIN users u ON t.user_id=u.id " +
            "JOIN books b ON t.book_id=b.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, search, status, userId, bookId, dateFrom, dateTo);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting transactions: {}", e.getMessage(), e);
        }
        return 0;
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                                String search, String status, Integer userId,
                                Integer bookId, String dateFrom, String dateTo) {
        if (search != null && !search.isBlank()) {
            sql.append(" AND (u.full_name LIKE ? OR b.title LIKE ? OR b.isbn LIKE ?)");
            String like = "%" + search.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (status != null && !status.isBlank())  { sql.append(" AND t.status=?"); params.add(status.toUpperCase()); }
        if (userId != null)   { sql.append(" AND t.user_id=?");  params.add(userId); }
        if (bookId != null)   { sql.append(" AND t.book_id=?");  params.add(bookId); }
        if (dateFrom != null && !dateFrom.isBlank()) { sql.append(" AND t.issue_date>=?"); params.add(Date.valueOf(dateFrom)); }
        if (dateTo != null && !dateTo.isBlank())     { sql.append(" AND t.issue_date<=?"); params.add(Date.valueOf(dateTo)); }
    }

    /**
     * Insert a new transaction (called within a DB transaction).
     */
    public int insert(Connection conn, Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, book_id, issued_by, issue_date, due_date, status, notes) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getUserId());
            ps.setInt(2, t.getBookId());
            if (t.getIssuedBy() != null) ps.setInt(3, t.getIssuedBy());
            else ps.setNull(3, Types.INTEGER);
            ps.setDate(4, Date.valueOf(t.getIssueDate()));
            ps.setDate(5, Date.valueOf(t.getDueDate()));
            ps.setString(6, t.getStatus().name());
            ps.setString(7, t.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Mark transaction as returned (called within a DB transaction).
     */
    public boolean markReturned(Connection conn, int txId, int returnedBy, java.time.LocalDate returnDate) throws SQLException {
        String sql = "UPDATE transactions SET status='RETURNED', return_date=?, returned_by=?, updated_at=NOW() WHERE id=? AND status IN ('ISSUED','OVERDUE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, returnedBy);
            ps.setInt(3, txId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Update overdue transactions (status → OVERDUE where due_date < today).
     */
    public int updateOverdueStatuses() {
        String sql = "UPDATE transactions SET status='OVERDUE', updated_at=NOW() " +
                     "WHERE status='ISSUED' AND due_date < CURDATE()";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating overdue statuses: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Count overdue books.
     */
    public long countOverdue() {
        String sql = "SELECT COUNT(*) FROM transactions WHERE status='OVERDUE'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Error counting overdue: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Count issued books for a user.
     */
    public int countActiveByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id=? AND status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting active by user: {}", e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Monthly issue/return counts for charts (last 6 months).
     */
    public List<Object[]> getMonthlyStats() {
        String sql = "SELECT DATE_FORMAT(issue_date, '%Y-%m') AS month, " +
                     "SUM(CASE WHEN status IN ('ISSUED','OVERDUE') THEN 1 ELSE 0 END) AS issued, " +
                     "SUM(CASE WHEN status='RETURNED' THEN 1 ELSE 0 END) AS returned " +
                     "FROM transactions WHERE issue_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                     "GROUP BY month ORDER BY month";
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{rs.getString(1), rs.getInt(2), rs.getInt(3)});
            }
        } catch (SQLException e) {
            logger.error("Error getting monthly stats: {}", e.getMessage(), e);
        }
        return result;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setUserName(rs.getString("user_name"));
        t.setUserEmail(rs.getString("user_email"));
        t.setBookId(rs.getInt("book_id"));
        t.setBookTitle(rs.getString("book_title"));
        t.setBookIsbn(rs.getString("book_isbn"));
        int issuedBy = rs.getInt("issued_by");
        if (!rs.wasNull()) t.setIssuedBy(issuedBy);
        t.setIssuedByName(rs.getString("issued_by_name"));
        int returnedBy = rs.getInt("returned_by");
        if (!rs.wasNull()) t.setReturnedBy(returnedBy);
        t.setReturnedByName(rs.getString("returned_by_name"));
        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) t.setIssueDate(issueDate.toLocalDate());
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) t.setDueDate(dueDate.toLocalDate());
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) t.setReturnDate(returnDate.toLocalDate());
        t.setStatus(Transaction.Status.valueOf(rs.getString("status")));
        t.setNotes(rs.getString("notes"));
        t.setOverdueDays(rs.getInt("overdue_days"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) t.setUpdatedAt(ua.toLocalDateTime());
        return t;
    }
}
