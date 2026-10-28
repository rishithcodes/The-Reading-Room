import re

with open("src/main/java/com/library/dao/NotificationDao.java", "r") as f:
    content = f.read()

# Fix isRead to getIsRead()
content = content.replace("n.isRead()", "n.getIsRead() != null ? n.getIsRead() : false")

# Add insertWithConn
content = content.replace(
    "public int insert(Notification n) {",
    """public int insert(Notification n) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return insertWithConn(conn, n);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertWithConn(Connection conn, Notification n) throws SQLException {"""
)
# We also need to fix the try with resources for insertWithConn not to close connection
content = content.replace(
    """public int insertWithConn(Connection conn, Notification n) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, type, reference_id, reference_type, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {""",
    """public int insertWithConn(Connection conn, Notification n) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, title, message, type, reference_id, reference_type, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {"""
)
# Fix the catch block for insertWithConn since it now throws SQLException
# Wait, replacing the whole try block is better.
