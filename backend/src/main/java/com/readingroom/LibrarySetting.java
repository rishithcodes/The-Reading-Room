package com.readingroom;
import jakarta.persistence.*;
@Entity @Table(name="settings")
public class LibrarySetting {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(name="setting_key",nullable=false,unique=true) private String key;
 @Column(name="setting_value") private String value;
 private String description;
 protected LibrarySetting(){}
 public Integer getId(){return id;} public String getKey(){return key;} public void setKey(String v){key=v;} public String getValue(){return value;} public void setValue(String v){value=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
}
