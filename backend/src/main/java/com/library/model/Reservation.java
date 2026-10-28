package com.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDateTime reservationDate;
    private LocalDate expiryDate;
    private Status status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE, FULFILLED, CANCELLED, EXPIRED
    }

    public Reservation() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }
    public Integer getUserId()                      { return userId; }
    public void setUserId(Integer userId)           { this.userId = userId; }
    public String getUserName()                     { return userName; }
    public void setUserName(String name)            { this.userName = name; }
    public String getUserEmail()                    { return userEmail; }
    public void setUserEmail(String email)          { this.userEmail = email; }
    public Integer getBookId()                      { return bookId; }
    public void setBookId(Integer bookId)           { this.bookId = bookId; }
    public String getBookTitle()                    { return bookTitle; }
    public void setBookTitle(String title)          { this.bookTitle = title; }
    public String getBookIsbn()                     { return bookIsbn; }
    public void setBookIsbn(String isbn)            { this.bookIsbn = isbn; }
    public LocalDateTime getReservationDate()       { return reservationDate; }
    public void setReservationDate(LocalDateTime d) { this.reservationDate = d; }
    public LocalDate getExpiryDate()                { return expiryDate; }
    public void setExpiryDate(LocalDate date)       { this.expiryDate = date; }
    public Status getStatus()                       { return status; }
    public void setStatus(Status status)            { this.status = status; }
    public String getNotes()                        { return notes; }
    public void setNotes(String notes)              { this.notes = notes; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)      { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)      { this.updatedAt = dt; }
}
