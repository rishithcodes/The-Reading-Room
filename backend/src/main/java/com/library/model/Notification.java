package com.library.model;

import java.time.LocalDateTime;

public class Notification {
    private Integer id;
    private Integer userId;
    private String title;
    private String message;
    private Type type;
    private Boolean isRead;
    private Integer referenceId;
    private String referenceType;
    private LocalDateTime createdAt;

    public enum Type {
        ISSUE, RETURN, DUE_SOON, OVERDUE, FINE, RESERVATION, GENERAL
    }

    public Notification() {}

    public Integer getId()                          { return id; }
    public void setId(Integer id)                   { this.id = id; }
    public Integer getUserId()                      { return userId; }
    public void setUserId(Integer userId)           { this.userId = userId; }
    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }
    public String getMessage()                      { return message; }
    public void setMessage(String message)          { this.message = message; }
    public Type getType()                           { return type; }
    public void setType(Type type)                  { this.type = type; }
    public Boolean getIsRead()                      { return isRead; }
    public void setIsRead(Boolean isRead)           { this.isRead = isRead; }
    public Integer getReferenceId()                 { return referenceId; }
    public void setReferenceId(Integer refId)       { this.referenceId = refId; }
    public String getReferenceType()                { return referenceType; }
    public void setReferenceType(String refType)    { this.referenceType = refType; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)      { this.createdAt = dt; }
}
