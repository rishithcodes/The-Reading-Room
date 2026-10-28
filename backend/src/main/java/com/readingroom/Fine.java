package com.readingroom;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="fines")
public class Fine {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(name="transaction_id") private Integer transactionId;
 @Column(name="user_id") private Integer userId;
 private BigDecimal amount;
 @Column(name="paid_amount") private BigDecimal paidAmount;
 @Column(name="overdue_days") private Integer overdueDays;
 private String status;
 protected Fine(){}
 public Integer getId(){return id;} public Integer getTransactionId(){return transactionId;} public void setTransactionId(Integer v){transactionId=v;} public Integer getUserId(){return userId;} public void setUserId(Integer v){userId=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public BigDecimal getPaidAmount(){return paidAmount;} public void setPaidAmount(BigDecimal v){paidAmount=v;} public Integer getOverdueDays(){return overdueDays;} public void setOverdueDays(Integer v){overdueDays=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
