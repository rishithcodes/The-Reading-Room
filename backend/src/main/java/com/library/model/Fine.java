package com.library.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Fine {
    private Integer id;
    private Integer transactionId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer bookId;
    private String bookTitle;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Integer overdueDays;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING, PARTIALLY_PAID, PAID, WAIVED
    }

    public Fine() {}

    public Integer getId()                           { return id; }
    public void setId(Integer id)                    { this.id = id; }
    public Integer getTransactionId()                { return transactionId; }
    public void setTransactionId(Integer txId)       { this.transactionId = txId; }
    public Integer getUserId()                       { return userId; }
    public void setUserId(Integer userId)            { this.userId = userId; }
    public String getUserName()                      { return userName; }
    public void setUserName(String name)             { this.userName = name; }
    public String getUserEmail()                     { return userEmail; }
    public void setUserEmail(String email)           { this.userEmail = email; }
    public Integer getBookId()                       { return bookId; }
    public void setBookId(Integer bookId)            { this.bookId = bookId; }
    public String getBookTitle()                     { return bookTitle; }
    public void setBookTitle(String title)           { this.bookTitle = title; }
    public BigDecimal getAmount()                    { return amount; }
    public void setAmount(BigDecimal amount)         { this.amount = amount; }
    public BigDecimal getPaidAmount()                { return paidAmount; }
    public void setPaidAmount(BigDecimal paid)       { this.paidAmount = paid; }
    public Integer getOverdueDays()                  { return overdueDays; }
    public void setOverdueDays(Integer days)         { this.overdueDays = days; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public String getNotes()                         { return notes; }
    public void setNotes(String notes)               { this.notes = notes; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)       { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)       { this.updatedAt = dt; }
}
