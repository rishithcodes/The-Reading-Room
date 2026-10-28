package com.library.model;

import java.time.LocalDateTime;

public class Book {
    private Integer id;
    private String isbn;
    private String title;
    private String description;
    private Integer authorId;
    private String authorName;
    private Integer categoryId;
    private String categoryName;
    private Integer publisherId;
    private String publisherName;
    private Integer publicationYear;
    private String language;
    private Integer totalCopies;
    private Integer availableCopies;
    private String rackNumber;
    private String coverImage;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        AVAILABLE, UNAVAILABLE, DISCONTINUED
    }

    public Book() {}

    public Integer getId()                         { return id; }
    public void setId(Integer id)                  { this.id = id; }
    public String getIsbn()                        { return isbn; }
    public void setIsbn(String isbn)               { this.isbn = isbn; }
    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }
    public String getDescription()                 { return description; }
    public void setDescription(String desc)        { this.description = desc; }
    public Integer getAuthorId()                   { return authorId; }
    public void setAuthorId(Integer authorId)      { this.authorId = authorId; }
    public String getAuthorName()                  { return authorName; }
    public void setAuthorName(String authorName)   { this.authorName = authorName; }
    public Integer getCategoryId()                 { return categoryId; }
    public void setCategoryId(Integer categoryId)  { this.categoryId = categoryId; }
    public String getCategoryName()                { return categoryName; }
    public void setCategoryName(String catName)    { this.categoryName = catName; }
    public Integer getPublisherId()                { return publisherId; }
    public void setPublisherId(Integer pubId)      { this.publisherId = pubId; }
    public String getPublisherName()               { return publisherName; }
    public void setPublisherName(String pubName)   { this.publisherName = pubName; }
    public Integer getPublicationYear()            { return publicationYear; }
    public void setPublicationYear(Integer year)   { this.publicationYear = year; }
    public String getLanguage()                    { return language; }
    public void setLanguage(String language)       { this.language = language; }
    public Integer getTotalCopies()                { return totalCopies; }
    public void setTotalCopies(Integer total)      { this.totalCopies = total; }
    public Integer getAvailableCopies()            { return availableCopies; }
    public void setAvailableCopies(Integer avail)  { this.availableCopies = avail; }
    public String getRackNumber()                  { return rackNumber; }
    public void setRackNumber(String rack)         { this.rackNumber = rack; }
    public String getCoverImage()                  { return coverImage; }
    public void setCoverImage(String img)          { this.coverImage = img; }
    public Status getStatus()                      { return status; }
    public void setStatus(Status status)           { this.status = status; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)     { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)     { this.updatedAt = dt; }
}
