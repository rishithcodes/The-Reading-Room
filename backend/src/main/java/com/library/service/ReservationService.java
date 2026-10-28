package com.library.service;

import com.library.dao.ReservationDao;
import com.library.dao.UserDao;
import com.library.model.Reservation;
import com.library.model.User;
import java.util.HashMap;
import java.util.Map;

public class ReservationService {
    private final ReservationDao reservationDao = new ReservationDao();
    private final UserDao userDao = new UserDao();

    public Map<String, Object> getAll(Integer userId, Integer bookId, String status, int page, int pageSize) {
        Map<String, Object> res = new HashMap<>();
        res.put("reservations", reservationDao.findAll(userId, bookId, status, page, pageSize));
        res.put("totalCount", reservationDao.countAll(userId, bookId, status));
        return res;
    }
    
    public Map<String, Object> getMy(int userId, String status, int page, int pageSize) {
        return getAll(userId, null, status, page, pageSize);
    }

    public Reservation getById(int id) { 
        // Need to know what findById returns, assuming Optional<Reservation> since all DAOs return Optional for single records
        return reservationDao.findById(id); 
    }

    public void createReservation(int userId, int bookId) {
        User u = userDao.findById(userId).orElse(null);
        if (u == null || u.getStatus() != User.Status.ACTIVE) throw new RuntimeException("User not active");
        if (reservationDao.hasActiveReservation(userId, bookId)) throw new RuntimeException("Already have active reservation");
        Reservation r = new Reservation();
        r.setUserId(userId);
        r.setBookId(bookId);
        r.setStatus(Reservation.Status.ACTIVE);
        reservationDao.insert(r);
    }

    public void cancelReservation(int reservationId, int requestingUserId, User.Role role) {
        Reservation r = reservationDao.findById(reservationId);
        if (r == null) throw new RuntimeException("Not found");
        if (r.getUserId() != requestingUserId && role != User.Role.ADMIN && role != User.Role.LIBRARIAN) {
            throw new RuntimeException("Not authorized to cancel this reservation");
        }
        reservationDao.updateStatus(reservationId, Reservation.Status.CANCELLED);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeCount", reservationDao.countActive());
        return stats;
    }
}
