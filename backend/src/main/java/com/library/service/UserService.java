package com.library.service;

import com.library.dao.UserDao;
import com.library.model.User;
import com.library.util.PasswordUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final UserDao userDao = new UserDao();

    public Map<String, Object> getAllUsers(String search, String role, int page, int pageSize) {
        List<User> users = userDao.findAll(search, role, page, pageSize);
        for (User u : users) stripPassword(u);
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("totalCount", userDao.countAll(search, role));
        return result;
    }

    public User getUserById(int id) {
        User u = userDao.findById(id).orElse(null);
        if (u != null) stripPassword(u);
        return u;
    }

    public void createUser(User user, String plainPassword) {
        if (plainPassword != null && !plainPassword.isEmpty()) {
            user.setPasswordHash(PasswordUtil.hash(plainPassword));
        }
        userDao.insert(user);
    }

    public void updateUser(User user) {
        userDao.update(user);
    }

    public void deactivateUser(int id) {
        User user = userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.INACTIVE);
        userDao.update(user);
    }

    public void changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDao.findById(userId).orElse(null);
        if (user == null || !PasswordUtil.verify(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPasswordHash(PasswordUtil.hash(newPassword));
        userDao.update(user);
    }

    private void stripPassword(User user) {
        user.setPasswordHash(null);
    }
}
