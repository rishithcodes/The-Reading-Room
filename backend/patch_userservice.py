with open("src/main/java/com/library/service/UserService.java", "r") as f:
    content = f.read()

content = content.replace("User u = userDao.findById(id);", "User u = userDao.findById(id).orElse(null);")
content = content.replace("User user = userDao.findById(userId);", "User user = userDao.findById(userId).orElse(null);")

# Fix deactivateUser to fetch and update if updateStatus doesn't exist
content = content.replace(
    "userDao.updateStatus(id, User.Status.INACTIVE);",
    """User user = userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.INACTIVE);
        userDao.update(user);"""
)

with open("src/main/java/com/library/service/UserService.java", "w") as f:
    f.write(content)
