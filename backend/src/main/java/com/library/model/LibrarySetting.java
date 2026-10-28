package com.library.model;

import java.time.LocalDateTime;

public class LibrarySetting {
    private Integer id;
    private String key;
    private String value;
    private String description;
    private LocalDateTime updatedAt;

    public LibrarySetting() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
