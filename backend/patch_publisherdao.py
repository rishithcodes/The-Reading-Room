import re

with open("src/main/java/com/library/dao/PublisherDao.java", "r") as f:
    content = f.read()

content = content.replace("import java.util.List;", "import java.util.List;\nimport java.util.Optional;")

content = re.sub(
    r"public Publisher findById\(int id\) \{.*?if \(rs\.next\(\)\) return mapRow\(rs\);\n\s*\} catch \(SQLException e\) \{ throw new RuntimeException\(e\); \}\n\s*return null;\n\s*\}",
    """public Optional<Publisher> findById(int id) {
        String sql = "SELECT * FROM publishers WHERE id = ?";
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

with open("src/main/java/com/library/dao/PublisherDao.java", "w") as f:
    f.write(content)
