package com.brillio.sts.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Date;
 
@Entity
@Table(name = "TICKETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tickets {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private int ticketId;
 
    @Column(name = "USER_ID")
    private int userId;
 
    @Column(name = "CONNECTION_ID")
    private int connectionId;
 
   
    @Column(name = "CONNECTION_TYPE")
    private String connectionType;
 
    
    @Column(name = "SERVICE_TYPE")
    private String serviceType;
 
    @Column(name = "DESCRIPTION")
    private String description;
 
    @Column(name = "PINCODE")
    private int pincode;
 
//    @Column(name = "LONGITUDE")
//    private Double longitude;
 
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "ADDRESS")
    private String address;
 
    
    @Column(name = "PRIORITY")
    private String priority;
 
    @Column(name = "ENGINEER_ID")
    private Integer engineerId;
 
    @Column(name = "ASSIGNMENT_DATE")
    private Date assignmentDate;
 
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
 
    @Column(name = "UPDATED_AT")
    private Date updatedAt;
    
    @Column(name = "LONGITUDE")
    private double longitude;
    
    @Column(name = "LATITUDE")
    private double latitude;
}
 
