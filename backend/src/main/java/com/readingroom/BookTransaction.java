package com.readingroom;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="transactions")
public class BookTransaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(name="user_id") private Integer userId;
 @Column(name="book_id") private Integer bookId;
 @Column(name="issued_by") private Integer issuedBy;
 @Column(name="returned_to") private Integer returnedTo;
 @Column(name="issue_date") private LocalDate issueDate;
 @Column(name="due_date") private LocalDate dueDate;
 @Column(name="return_date") private LocalDate returnDate;
 private String status;
 protected BookTransaction(){}
 public Integer getId(){return id;} public Integer getUserId(){return userId;} public void setUserId(Integer v){userId=v;} public Integer getBookId(){return bookId;} public void setBookId(Integer v){bookId=v;} public Integer getIssuedBy(){return issuedBy;} public void setIssuedBy(Integer v){issuedBy=v;} public Integer getReturnedTo(){return returnedTo;} public void setReturnedTo(Integer v){returnedTo=v;} public LocalDate getIssueDate(){return issueDate;} public void setIssueDate(LocalDate v){issueDate=v;} public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate v){dueDate=v;} public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate v){returnDate=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
