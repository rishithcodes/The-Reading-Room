package com.library.model;

import java.time.LocalDateTime;

/**
 * User entity model representing library system users.
 */
public class User {

    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private Role role;
    private String department;
    private String studentId;
    private Status status;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN, LIBRARIAN, STUDENT
    }

    public enum Status {
        ACTIVE, INACTIVE, SUSPENDED
    }

    public User() {}

    public User(Integer id, String fullName, String email, Role role, Status status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }

    public String getFullName()                { return fullName; }
    public void setFullName(String fullName)   { this.fullName = fullName; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }

    public String getPasswordHash()            { return passwordHash; }
    public void setPasswordHash(String hash)   { this.passwordHash = hash; }

    public Role getRole()                      { return role; }
    public void setRole(Role role)             { this.role = role; }

    public String getDepartment()              { return department; }
    public void setDepartment(String dept)     { this.department = dept; }

    public String getStudentId()               { return studentId; }
    public void setStudentId(String sid)       { this.studentId = sid; }

    public Status getStatus()                  { return status; }
    public void setStatus(Status status)       { this.status = status; }

    public String getProfileImage()            { return profileImage; }
    public void setProfileImage(String img)    { this.profileImage = img; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }

    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}
