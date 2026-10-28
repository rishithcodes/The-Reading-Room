package com.library.model;

import java.time.LocalDateTime;

public class Author {
    private Integer id;
    private String name;
    private String biography;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Author() {}

    public Author(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId()                     { return id; }
    public void setId(Integer id)              { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getBiography()               { return biography; }
    public void setBiography(String bio)       { this.biography = bio; }
    public String getCountry()                 { return country; }
    public void setCountry(String country)     { this.country = country; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }
}
