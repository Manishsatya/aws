package com.brillio.sts.model;

import java.util.Date;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CONNECTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@ToString
public class Connections {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTION_ID")
    private int connectionId;

    @Column(name = "USER_ID")
    private int userId;

    @Column(name = "CONNECTION_TYPE")
    private String connectionType;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "VALIDITY_PERIOD")
    private int validityPeriod;

    @Column(name = "EXPIRY_DATE")
    private Date expiryDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "STATUS")
    private String status;
    

}

