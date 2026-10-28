package com.readingroom;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="reservations")
public class Reservation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(name="user_id") private Integer userId;
 @Column(name="book_id") private Integer bookId;
 private String status;
 @Column(name="created_at") private LocalDateTime createdAt;
 @Column(name="expires_at") private LocalDateTime expiresAt;
 protected Reservation(){}
 public Integer getId(){return id;} public Integer getUserId(){return userId;} public void setUserId(Integer v){userId=v;} public Integer getBookId(){return bookId;} public void setBookId(Integer v){bookId=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime v){expiresAt=v;}
}
