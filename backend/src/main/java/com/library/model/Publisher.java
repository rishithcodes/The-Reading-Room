package com.library.model;

import java.time.LocalDateTime;

public class Publisher {
    private Integer id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String website;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Publisher() {}
    public Publisher(Integer id, String name) { this.id = id; this.name = name; }

    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getAddress()                 { return address; }
    public void setAddress(String address)     { this.address = address; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }
    public String getWebsite()                 { return website; }
    public void setWebsite(String website)     { this.website = website; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }
}
