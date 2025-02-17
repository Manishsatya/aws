package com.brillio.sts.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ACCOUNTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Accounts {

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "PINCODE")
    private int pincode;

    @Column(name = "SECURITY_QUESTION")
    private String securityQuestion;

    @Column(name = "SECURITY_ANSWER")
    private String securityAnswer;

    @Column(name = "ACCOUNT_STATUS")
    private String accountStatus;
    
    @Column(name = "GENDER")
    private String gender;
    
    @Column(name = "LONGITUDE")
    private double longitude;
    
    @Column(name = "LATITUDE")
    private double latitude;
    
}


