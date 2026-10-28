import re

with open("src/main/java/com/library/dao/NotificationDao.java", "r") as f:
    content = f.read()

content = re.sub(
    r"public int insertWithConn\(Connection conn, Notification n\) throws SQLException \{\n\s*String sql = [^\n]+\n\s*try \(Connection[^\n]+\n\s*PreparedStatement stmt = [^\n]+\) \{",
    r"""public int insertWithConn(Connection conn, Notification n) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, type, reference_id, reference_type, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {""",
    content, flags=re.DOTALL
)

content = re.sub(
    r"\}\s*catch\s*\(SQLException\s*e\)\s*\{\s*throw\s*new\s*RuntimeException\(e\);\s*\}\s*return\s*-1;\s*\}\s*public\s*List",
    r"} return -1; } public List",
    content
)

with open("src/main/java/com/library/dao/NotificationDao.java", "w") as f:
    f.write(content)
