package com.library.service;

import com.library.config.DatabaseConfig;
import com.library.dao.*;
import com.library.model.User;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DashboardService {
    private final UserDao userDao = new UserDao();
    private final FineDao fineDao = new FineDao();
    private final ReservationDao reservationDao = new ReservationDao();
    private final TransactionDao transactionDao = new TransactionDao();

    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT SUM(total_copies) FROM books")) {
                ResultSet rs = stmt.executeQuery();
                data.put("totalBooks", rs.next() ? rs.getInt(1) : 0);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        
        data.put("availableBooks", 0); // Placeholder
        data.put("issuedBooks", 0);
        data.put("overdueBooks", transactionDao.countOverdue());
        data.put("totalStudents", userDao.countByRole(User.Role.STUDENT));
        data.put("pendingFines", fineDao.countPending());
        data.put("pendingFinesAmount", fineDao.getTotalPendingAmount());
        data.put("activeReservations", reservationDao.countActive());
        data.put("booksPerCategory", new ArrayList<>());
        data.put("monthlyStats", new ArrayList<>());
        data.put("mostBorrowed", new ArrayList<>());
        return data;
    }

    public Map<String, Object> getStudentDashboard(int userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("activeBorrows", transactionDao.countActiveByUser(userId));
        data.put("dueSoonBooks", new ArrayList<>());
        data.put("overdueBorrows", new ArrayList<>());
        data.put("pendingFines", new ArrayList<>());
        data.put("activeReservations", new ArrayList<>());
        data.put("recentHistory", new ArrayList<>());
        return data;
    }
}
