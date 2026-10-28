package com.readingroom;
import jakarta.persistence.*;
@Entity @Table(name="books")
public class Book {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 private String isbn;
 @Column(nullable=false) private String title;
 @Column(columnDefinition="TEXT") private String description;
 @Column(name="author_id") private Integer authorId;
 @Column(name="category_id") private Integer categoryId;
 @Column(name="publisher_id") private Integer publisherId;
 @Column(name="publication_year") private Integer publicationYear;
 private String language;
 @Column(name="total_copies") private Integer totalCopies;
 @Column(name="available_copies") private Integer availableCopies;
 @Column(name="rack_number") private String rackNumber;
 private String status;
 protected Book(){}
 public Integer getId(){return id;} public String getIsbn(){return isbn;} public void setIsbn(String v){isbn=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public Integer getAuthorId(){return authorId;} public void setAuthorId(Integer v){authorId=v;} public Integer getCategoryId(){return categoryId;} public void setCategoryId(Integer v){categoryId=v;} public Integer getPublisherId(){return publisherId;} public void setPublisherId(Integer v){publisherId=v;} public Integer getPublicationYear(){return publicationYear;} public void setPublicationYear(Integer v){publicationYear=v;} public String getLanguage(){return language;} public void setLanguage(String v){language=v;} public Integer getTotalCopies(){return totalCopies;} public void setTotalCopies(Integer v){totalCopies=v;} public Integer getAvailableCopies(){return availableCopies;} public void setAvailableCopies(Integer v){availableCopies=v;} public String getRackNumber(){return rackNumber;} public void setRackNumber(String v){rackNumber=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
