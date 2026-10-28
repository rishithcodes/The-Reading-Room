import re

with open("src/main/java/com/library/dao/FineDao.java", "r") as f:
    content = f.read()

# Add Optional import if missing
if "java.util.Optional" not in content:
    content = content.replace("import java.util.List;", "import java.util.List;\nimport java.util.Optional;")

# Replace findById
content = re.sub(
    r"public Fine findById\(int id\) \{.*?if \(rs\.next\(\)\) return mapRow\(rs\);\n\s*\} catch \(SQLException e\) \{ throw new RuntimeException\(e\); \}\n\s*return null;\n\s*\}",
    """public Optional<Fine> findById(int id) {
        String sql = "SELECT * FROM fines WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }""",
    content, flags=re.DOTALL
)

# Replace findByTransactionId
content = re.sub(
    r"public Fine findByTransactionId\(int transactionId\) \{.*?if \(rs\.next\(\)\) return mapRow\(rs\);\n\s*\} catch \(SQLException e\) \{ throw new RuntimeException\(e\); \}\n\s*return null;\n\s*\}",
    """public Optional<Fine> findByTransactionId(int transactionId) {
        String sql = "SELECT * FROM fines WHERE transaction_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }""",
    content, flags=re.DOTALL
)

# Fix UNPAID to PENDING
content = content.replace("UNPAID", "PENDING")

# Add insertWithConn
content = content.replace(
    "public int insert(Connection conn, Fine fine) throws SQLException {",
    """public int insert(Fine fine) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return insertWithConn(conn, fine);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertWithConn(Connection conn, Fine fine) throws SQLException {"""
)

with open("src/main/java/com/library/dao/FineDao.java", "w") as f:
    f.write(content)
