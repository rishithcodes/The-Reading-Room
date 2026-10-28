package com.readingroom;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @Column(name = "user_id", nullable = false) private Integer userId;
    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(nullable = false) private String type = "GENERAL";
    @Column(name = "is_read", nullable = false) private boolean read;
    @Transient private Integer referenceId;
    @Transient private String referenceType;
    @Column(name = "created_at") private LocalDateTime createdAt;
    protected Notification() {}
    public Integer getId(){return id;} public Integer getUserId(){return userId;} public void setUserId(Integer v){userId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public String getType(){return type;} public void setType(String v){type=v;} public boolean isRead(){return read;} public void setRead(boolean v){read=v;}
    public Integer getReferenceId(){return referenceId;} public void setReferenceId(Integer v){referenceId=v;} public String getReferenceType(){return referenceType;} public void setReferenceType(String v){referenceType=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
