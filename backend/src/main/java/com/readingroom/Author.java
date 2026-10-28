package com.readingroom;
import jakarta.persistence.*;
@Entity @Table(name="authors")
public class Author {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false) private String name;
 private String biography; private String country;
 protected Author(){}
 public Integer getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getBiography(){return biography;} public void setBiography(String v){biography=v;} public String getCountry(){return country;} public void setCountry(String v){country=v;}
}
