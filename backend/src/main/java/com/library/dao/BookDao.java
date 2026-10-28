package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Book operations with full JOIN support.
 */
public class BookDao {

    private static final Logger logger = LoggerFactory.getLogger(BookDao.class);

    private static final String SELECT_WITH_JOINS =
        "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
        "FROM books b " +
        "LEFT JOIN authors a ON b.author_id = a.id " +
        "LEFT JOIN categories c ON b.category_id = c.id " +
        "LEFT JOIN publishers p ON b.publisher_id = p.id ";

    public Optional<Book> findById(int id) {
        String sql = SELECT_WITH_JOINS + "WHERE b.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding book by id: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Book> findByIsbn(String isbn) {
        String sql = SELECT_WITH_JOINS + "WHERE b.isbn = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding book by ISBN: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Book> findAll(String search, Integer categoryId, Integer authorId,
                               Integer publisherId, String availability, String language,
                               Integer yearFrom, Integer yearTo,
                               String sortBy, String sortDir, int page, int pageSize) {

        StringBuilder sql = new StringBuilder(SELECT_WITH_JOINS + "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        appendFilters(sql, params, search, categoryId, authorId, publisherId,
                availability, language, yearFrom, yearTo);

        // Sort
        String col = toSortColumn(sortBy);
        String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(col).append(" ").append(dir);

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all books: {}", e.getMessage(), e);
        }
        return books;
    }

    public long countAll(String search, Integer categoryId, Integer authorId,
                          Integer publisherId, String availability, String language,
                          Integer yearFrom, Integer yearTo) {

        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM books b LEFT JOIN authors a ON b.author_id=a.id " +
            "LEFT JOIN categories c ON b.category_id=c.id " +
            "LEFT JOIN publishers p ON b.publisher_id=p.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, search, categoryId, authorId, publisherId,
                availability, language, yearFrom, yearTo);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting books: {}", e.getMessage(), e);
        }
        return 0;
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                                String search, Integer categoryId, Integer authorId,
                                Integer publisherId, String availability,
                                String language, Integer yearFrom, Integer yearTo) {
        if (search != null && !search.isBlank()) {
            sql.append(" AND (b.title LIKE ? OR b.isbn LIKE ? OR a.name LIKE ?)");
            String like = "%" + search.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (categoryId != null)  { sql.append(" AND b.category_id=?");     params.add(categoryId); }
        if (authorId != null)    { sql.append(" AND b.author_id=?");        params.add(authorId); }
        if (publisherId != null) { sql.append(" AND b.publisher_id=?");     params.add(publisherId); }
        if (language != null && !language.isBlank()) {
            sql.append(" AND b.language=?"); params.add(language);
        }
        if (yearFrom != null)    { sql.append(" AND b.publication_year>=?"); params.add(yearFrom); }
        if (yearTo != null)      { sql.append(" AND b.publication_year<=?"); params.add(yearTo); }
        if ("available".equalsIgnoreCase(availability)) {
            sql.append(" AND b.available_copies > 0");
        } else if ("unavailable".equalsIgnoreCase(availability)) {
            sql.append(" AND b.available_copies = 0");
        }
    }

    public int insert(Book book) {
        String sql = "INSERT INTO books (isbn, title, description, author_id, category_id, publisher_id, " +
                     "publication_year, language, total_copies, available_copies, rack_number, cover_image, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getDescription());
            ps.setInt(4, book.getAuthorId());
            ps.setInt(5, book.getCategoryId());
            if (book.getPublisherId() != null) ps.setInt(6, book.getPublisherId());
            else ps.setNull(6, Types.INTEGER);
            if (book.getPublicationYear() != null) ps.setInt(7, book.getPublicationYear());
            else ps.setNull(7, Types.INTEGER);
            ps.setString(8, book.getLanguage() != null ? book.getLanguage() : "English");
            ps.setInt(9, book.getTotalCopies() != null ? book.getTotalCopies() : 1);
            ps.setInt(10, book.getAvailableCopies() != null ? book.getAvailableCopies() : 1);
            ps.setString(11, book.getRackNumber());
            ps.setString(12, book.getCoverImage());
            ps.setString(13, book.getStatus() != null ? book.getStatus().name() : "AVAILABLE");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error inserting book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create book: " + e.getMessage(), e);
        }
        return -1;
    }

    public boolean update(Book book) {
        String sql = "UPDATE books SET isbn=?, title=?, description=?, author_id=?, category_id=?, " +
                     "publisher_id=?, publication_year=?, language=?, total_copies=?, rack_number=?, " +
                     "cover_image=?, status=?, updated_at=NOW() WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getDescription());
            ps.setInt(4, book.getAuthorId());
            ps.setInt(5, book.getCategoryId());
            if (book.getPublisherId() != null) ps.setInt(6, book.getPublisherId());
            else ps.setNull(6, Types.INTEGER);
            if (book.getPublicationYear() != null) ps.setInt(7, book.getPublicationYear());
            else ps.setNull(7, Types.INTEGER);
            ps.setString(8, book.getLanguage());
            ps.setInt(9, book.getTotalCopies());
            ps.setString(10, book.getRackNumber());
            ps.setString(11, book.getCoverImage());
            ps.setString(12, book.getStatus().name());
            ps.setInt(13, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating book: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting book: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Atomically decreases available_copies by 1 (used during issue).
     */
    public boolean decrementAvailable(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id=? AND available_copies > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Atomically increases available_copies by 1 (used during return).
     */
    public boolean incrementAvailable(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id=? AND available_copies < total_copies";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Get total, available, and issued book statistics.
     */
    public int[] getBookStats() {
        String sql = "SELECT SUM(total_copies), SUM(available_copies), SUM(total_copies - available_copies) FROM books";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{
                    rs.getInt(1), rs.getInt(2), rs.getInt(3)
                };
            }
        } catch (SQLException e) {
            logger.error("Error getting book stats: {}", e.getMessage(), e);
        }
        return new int[]{0, 0, 0};
    }

    /**
     * Get books by category for chart.
     */
    public List<Object[]> getBooksPerCategory() {
        String sql = "SELECT c.name, COUNT(b.id) AS book_count FROM categories c " +
                     "LEFT JOIN books b ON c.id = b.category_id GROUP BY c.id, c.name ORDER BY book_count DESC";
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{rs.getString(1), rs.getInt(2)});
            }
        } catch (SQLException e) {
            logger.error("Error getting books per category: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * Get most borrowed books.
     */
    public List<Object[]> getMostBorrowed(int limit) {
        String sql = "SELECT b.title, COUNT(t.id) AS borrow_count FROM books b " +
                     "JOIN transactions t ON b.id=t.book_id GROUP BY b.id, b.title " +
                     "ORDER BY borrow_count DESC LIMIT ?";
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{rs.getString(1), rs.getInt(2)});
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting most borrowed: {}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * Find books by category (for recommendations).
     */
    public List<Book> findByCategoryId(int categoryId, int excludeUserId, int limit) {
        String sql = SELECT_WITH_JOINS +
                "WHERE b.category_id=? AND b.available_copies > 0 " +
                "AND b.id NOT IN (SELECT book_id FROM transactions WHERE user_id=? AND status IN ('ISSUED','OVERDUE')) " +
                "ORDER BY b.created_at DESC LIMIT ?";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, excludeUserId);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding books by category: {}", e.getMessage(), e);
        }
        return books;
    }

    public boolean hasActiveTransactions(int bookId) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE book_id=? AND status IN ('ISSUED','OVERDUE')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking active transactions: {}", e.getMessage(), e);
        }
        return false;
    }

    private String toSortColumn(String sortBy) {
        if (sortBy == null) return "b.created_at";
        return switch (sortBy.toLowerCase()) {
            case "title" -> "b.title";
            case "author" -> "a.name";
            case "year" -> "b.publication_year";
            case "available" -> "b.available_copies";
            default -> "b.created_at";
        };
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setAuthorId(rs.getInt("author_id"));
        b.setAuthorName(rs.getString("author_name"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        int pubId = rs.getInt("publisher_id");
        if (!rs.wasNull()) b.setPublisherId(pubId);
        b.setPublisherName(rs.getString("publisher_name"));
        int year = rs.getInt("publication_year");
        if (!rs.wasNull()) b.setPublicationYear(year);
        b.setLanguage(rs.getString("language"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setRackNumber(rs.getString("rack_number"));
        b.setCoverImage(rs.getString("cover_image"));
        b.setStatus(Book.Status.valueOf(rs.getString("status")));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) b.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) b.setUpdatedAt(ua.toLocalDateTime());
        return b;
    }
}
