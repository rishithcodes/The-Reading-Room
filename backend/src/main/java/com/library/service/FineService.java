package com.library.service;

import com.library.dao.FineDao;
import com.library.model.Fine;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FineService {
    private final FineDao fineDao = new FineDao();

    public Map<String, Object> getAllFines(String search, String status, Integer userId, int page, int pageSize) {
        Map<String, Object> res = new HashMap<>();
        res.put("fines", fineDao.findAll(search, status, userId, page, pageSize));
        res.put("totalCount", fineDao.countAll(search, status, userId));
        return res;
    }

    public Fine getFineById(int id) { return fineDao.findById(id).orElse(null); }
    
    public Fine getFineByTransaction(int transactionId) { return fineDao.findByTransactionId(transactionId).orElse(null); }

    public void recordPayment(int fineId, BigDecimal amount, int receivedById) {
        Fine fine = fineDao.findById(fineId).orElseThrow(() -> new RuntimeException("Fine not found"));
        if (fine.getStatus() == Fine.Status.PAID || fine.getStatus() == Fine.Status.WAIVED) {
            throw new RuntimeException("Fine is already paid or waived");
        }
        BigDecimal newPaid = fine.getPaidAmount().add(amount);
        Fine.Status newStatus = newPaid.compareTo(fine.getAmount()) >= 0 ? Fine.Status.PAID : Fine.Status.PARTIALLY_PAID;
        fineDao.updatePaidAmount(fineId, newPaid, newStatus);
    }

    public void waiveFine(int fineId) {
        fineDao.updateStatus(fineId, Fine.Status.WAIVED);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingCount", fineDao.countPending());
        stats.put("pendingAmount", fineDao.getTotalPendingAmount());
        stats.put("monthlyCollection", fineDao.getFineCollectionByMonth());
        return stats;
    }
}
