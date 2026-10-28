package com.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer bookId;
    private String bookTitle;
    private String bookIsbn;
    private Integer issuedBy;
    private String issuedByName;
    private Integer returnedBy;
    private String returnedByName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed field
    private Integer overdueDays;
    private Double fineAmount;

    public enum Status {
        ISSUED, RETURNED, OVERDUE, LOST
    }

    public Transaction() {}

    public Integer getId()                         { return id; }
    public void setId(Integer id)                  { this.id = id; }
    public Integer getUserId()                     { return userId; }
    public void setUserId(Integer userId)          { this.userId = userId; }
    public String getUserName()                    { return userName; }
    public void setUserName(String name)           { this.userName = name; }
    public String getUserEmail()                   { return userEmail; }
    public void setUserEmail(String email)         { this.userEmail = email; }
    public Integer getBookId()                     { return bookId; }
    public void setBookId(Integer bookId)          { this.bookId = bookId; }
    public String getBookTitle()                   { return bookTitle; }
    public void setBookTitle(String title)         { this.bookTitle = title; }
    public String getBookIsbn()                    { return bookIsbn; }
    public void setBookIsbn(String isbn)           { this.bookIsbn = isbn; }
    public Integer getIssuedBy()                   { return issuedBy; }
    public void setIssuedBy(Integer issuedBy)      { this.issuedBy = issuedBy; }
    public String getIssuedByName()                { return issuedByName; }
    public void setIssuedByName(String name)       { this.issuedByName = name; }
    public Integer getReturnedBy()                 { return returnedBy; }
    public void setReturnedBy(Integer retBy)       { this.returnedBy = retBy; }
    public String getReturnedByName()              { return returnedByName; }
    public void setReturnedByName(String name)     { this.returnedByName = name; }
    public LocalDate getIssueDate()                { return issueDate; }
    public void setIssueDate(LocalDate date)       { this.issueDate = date; }
    public LocalDate getDueDate()                  { return dueDate; }
    public void setDueDate(LocalDate date)         { this.dueDate = date; }
    public LocalDate getReturnDate()               { return returnDate; }
    public void setReturnDate(LocalDate date)      { this.returnDate = date; }
    public Status getStatus()                      { return status; }
    public void setStatus(Status status)           { this.status = status; }
    public String getNotes()                       { return notes; }
    public void setNotes(String notes)             { this.notes = notes; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)     { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)     { this.updatedAt = dt; }
    public Integer getOverdueDays()                { return overdueDays; }
    public void setOverdueDays(Integer days)       { this.overdueDays = days; }
    public Double getFineAmount()                  { return fineAmount; }
    public void setFineAmount(Double amount)       { this.fineAmount = amount; }
}
