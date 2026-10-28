with open("src/main/java/com/library/dao/PublisherDao.java", "r") as f:
    content = f.read()

# Fix insert
content = content.replace(
    'String sql = "INSERT INTO publishers (name, contact_info) VALUES (?, ?)";',
    'String sql = "INSERT INTO publishers (name, address, email, phone, website) VALUES (?, ?, ?, ?, ?)";'
)
content = content.replace(
    """stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getContactInfo());
            stmt.executeUpdate();""",
    """stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getEmail());
            stmt.setString(4, publisher.getPhone());
            stmt.setString(5, publisher.getWebsite());
            stmt.executeUpdate();"""
)

# Fix update
content = content.replace(
    'String sql = "UPDATE publishers SET name = ?, contact_info = ? WHERE id = ?";',
    'String sql = "UPDATE publishers SET name = ?, address = ?, email = ?, phone = ?, website = ? WHERE id = ?";'
)
content = content.replace(
    """stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getContactInfo());
            stmt.setInt(3, publisher.getId());""",
    """stmt.setString(1, publisher.getName());
            stmt.setString(2, publisher.getAddress());
            stmt.setString(3, publisher.getEmail());
            stmt.setString(4, publisher.getPhone());
            stmt.setString(5, publisher.getWebsite());
            stmt.setInt(6, publisher.getId());"""
)

# Fix mapRow
content = content.replace(
    """p.setContactInfo(rs.getString("contact_info"));""",
    """p.setAddress(rs.getString("address"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        p.setWebsite(rs.getString("website"));"""
)

with open("src/main/java/com/library/dao/PublisherDao.java", "w") as f:
    f.write(content)
