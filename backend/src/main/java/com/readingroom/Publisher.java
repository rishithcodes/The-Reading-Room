package com.readingroom;
import jakarta.persistence.*;
@Entity @Table(name="publishers")
public class Publisher {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false) private String name;
 private String address; private String email; private String phone; private String website;
 protected Publisher(){}
 public Integer getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getWebsite(){return website;} public void setWebsite(String v){website=v;}
}
