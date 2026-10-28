package com.library.service;

import com.library.dao.NotificationDao;
import com.library.model.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDao notificationDao = new NotificationDao();

    public List<Notification> getForUser(int userId, int page, int pageSize) {
        return notificationDao.findByUserId(userId, page, pageSize);
    }

    public long getUnreadCount(int userId) { return notificationDao.countUnread(userId); }

    public void markRead(int notificationId) { notificationDao.markAsRead(notificationId); }

    public void markAllRead(int userId) { notificationDao.markAllAsRead(userId); }

    public void createNotification(int userId, String title, String message, Notification.Type type, Integer refId, String refType) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setReferenceId(refId);
        n.setReferenceType(refType);
        notificationDao.insert(n);
    }
}
