package com.library.service;

import com.library.config.DatabaseConfig;
import com.library.dao.*;
import com.library.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Service for book issue and return operations.
 * All issue/return operations use database transactions for atomicity.
 */
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDao transactionDao = new TransactionDao();
    private final BookDao bookDao = new BookDao();
    private final UserDao userDao = new UserDao();
    private final SettingsDao settingsDao = new SettingsDao();
    private final FineDao fineDao = new FineDao();
    private final NotificationDao notificationDao = new NotificationDao();
    private final ReservationDao reservationDao = new ReservationDao();

    /**
     * Issue a book to a user. Atomic database transaction.
     */
    public Transaction issueBook(int userId, int bookId, int issuedById) {
        Connection conn = null;
        try {
            // Validate user
            User user = userDao.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found."));
            if (user.getStatus() != User.Status.ACTIVE) {
                throw new RuntimeException("User account is not active.");
            }

            // Check borrow limit
            int activeBorrows = transactionDao.countActiveByUser(userId);
            int limit = settingsDao.getIntValue("max_borrow_limit", 5);
            if (activeBorrows >= limit) {
                throw new RuntimeException("Borrow limit of " + limit + " books reached. Please return a book first.");
            }

            // Validate book
            Book book = bookDao.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found."));
            if (book.getAvailableCopies() <= 0) {
                throw new RuntimeException("No copies available. You can reserve this book.");
            }

            // Get loan period from settings
            int loanDays = settingsDao.getIntValue("loan_period_days", 14);

            // --- Begin DB transaction ---
            conn = DatabaseConfig.getTransactionalConnection();

            // Atomically decrement available copies
            if (!bookDao.decrementAvailable(conn, bookId)) {
                throw new RuntimeException("Book became unavailable. Please try again.");
            }

            // Create transaction record
            Transaction tx = new Transaction();
            tx.setUserId(userId);
            tx.setBookId(bookId);
            tx.setIssuedBy(issuedById);
            tx.setIssueDate(LocalDate.now());
            tx.setDueDate(LocalDate.now().plusDays(loanDays));
            tx.setStatus(Transaction.Status.ISSUED);
            int txId = transactionDao.insert(conn, tx);
            tx.setId(txId);
            tx.setBookTitle(book.getTitle());
            tx.setUserName(user.getFullName());

            // Create notification
            Notification n = new Notification();
            n.setUserId(userId);
            n.setTitle("Book Issued Successfully");
            n.setMessage("\"" + book.getTitle() + "\" has been issued to you. Due date: " + tx.getDueDate());
            n.setType(Notification.Type.ISSUE);
            n.setReferenceId(txId);
            n.setReferenceType("transaction");
            n.setIsRead(false);
            notificationDao.insertWithConn(conn, n);

            conn.commit();
            logger.info("Book issued: bookId={} to userId={}, txId={}", bookId, userId, txId);
            return tx;

        } catch (RuntimeException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } catch (Exception e) {
            DatabaseConfig.rollback(conn);
            logger.error("Error issuing book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to issue book: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeConnection(conn);
        }
    }

    /**
     * Return a book. Calculates fine if overdue. Atomic database transaction.
     */
    public Transaction returnBook(int transactionId, int returnedById) {
        Connection conn = null;
        try {
            // Validate transaction
            Transaction tx = transactionDao.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found."));
            if (tx.getStatus() == Transaction.Status.RETURNED) {
                throw new RuntimeException("This book has already been returned.");
            }
            if (tx.getStatus() == Transaction.Status.LOST) {
                throw new RuntimeException("This transaction is marked as LOST.");
            }

            // --- Begin DB transaction ---
            conn = DatabaseConfig.getTransactionalConnection();

            // Mark transaction as returned
            if (!transactionDao.markReturned(conn, transactionId, returnedById, LocalDate.now())) {
                throw new RuntimeException("Failed to process return.");
            }

            // Increment book's available copies
            bookDao.incrementAvailable(conn, tx.getBookId());

            // Calculate fine
            long overdueDays = ChronoUnit.DAYS.between(tx.getDueDate(), LocalDate.now());
            if (overdueDays > 0) {
                BigDecimal finePerDay = settingsDao.getDecimalValue("fine_per_day", new BigDecimal("2.00"));
                BigDecimal fineAmount = finePerDay.multiply(new BigDecimal(overdueDays));

                Fine fine = new Fine();
                fine.setTransactionId(transactionId);
                fine.setUserId(tx.getUserId());
                fine.setAmount(fineAmount);
                fine.setPaidAmount(BigDecimal.ZERO);
                fine.setOverdueDays((int) overdueDays);
                fine.setStatus(Fine.Status.PENDING);
                fineDao.insertWithConn(conn, fine);

                tx.setFineAmount(fineAmount.doubleValue());

                // Fine notification
                Notification nFine = new Notification();
                nFine.setUserId(tx.getUserId());
                nFine.setTitle("Fine Generated");
                nFine.setMessage("A fine of ₹" + fineAmount + " has been generated for " + overdueDays + " overdue days.");
                nFine.setType(Notification.Type.FINE);
                nFine.setReferenceId(transactionId);
                nFine.setReferenceType("transaction");
                nFine.setIsRead(false);
                notificationDao.insertWithConn(conn, nFine);
            }

            // Return notification
            Notification nRet = new Notification();
            nRet.setUserId(tx.getUserId());
            nRet.setTitle("Book Returned Successfully");
            nRet.setMessage("\"" + tx.getBookTitle() + "\" has been returned. Thank you!");
            nRet.setType(Notification.Type.RETURN);
            nRet.setReferenceId(transactionId);
            nRet.setReferenceType("transaction");
            nRet.setIsRead(false);
            notificationDao.insertWithConn(conn, nRet);

            // Fulfill oldest reservation for this book if any
            reservationDao.fulfillOldest(conn, tx.getBookId());

            conn.commit();
            tx.setStatus(Transaction.Status.RETURNED);
            tx.setReturnDate(LocalDate.now());
            logger.info("Book returned: transactionId={}", transactionId);
            return tx;

        } catch (RuntimeException e) {
            DatabaseConfig.rollback(conn);
            throw e;
        } catch (Exception e) {
            DatabaseConfig.rollback(conn);
            logger.error("Error returning book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to return book: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeConnection(conn);
        }
    }

    public Map<String, Object> getAllTransactions(String search, String status, Integer userId,
                                                   Integer bookId, String dateFrom, String dateTo,
                                                   int page, int pageSize) {
        List<Transaction> list = transactionDao.findAll(search, status, userId, bookId, dateFrom, dateTo, page, pageSize);
        long total = transactionDao.countAll(search, status, userId, bookId, dateFrom, dateTo);
        return Map.of("transactions", list, "totalCount", total);
    }

    public Transaction getById(int id) {
        return transactionDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));
    }

    public List<Transaction> getMyTransactions(int userId, String status, int page, int pageSize) {
        return transactionDao.findAll(null, status, userId, null, null, null, page, pageSize);
    }

    public long countMyActive(int userId) {
        return transactionDao.countActiveByUser(userId);
    }
}
