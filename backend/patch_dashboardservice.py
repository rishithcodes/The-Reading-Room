with open("src/main/java/com/library/service/DashboardService.java", "r") as f:
    content = f.read()

content = content.replace("userDao.countByRole(\"STUDENT\")", "userDao.countByRole(User.Role.STUDENT)")
content = content.replace("transactionDao.countActiveBorrows(userId)", "transactionDao.countActiveByUser(userId)")

with open("src/main/java/com/library/service/DashboardService.java", "w") as f:
    f.write(content)
