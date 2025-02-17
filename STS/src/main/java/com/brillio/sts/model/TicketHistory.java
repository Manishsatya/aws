package com.brillio.sts.model;
 
import java.time.LocalDateTime;
import java.util.Date;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Entity
@Table(name = "TICKET_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketHistory {
    @Id
    @Column(name = "HISTORY_ID")
    private int historyId;
    @Column(name = "TICKET_ID")
    private int ticketId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "USER_ID ")
    private int userId;
    @Column(name = "ENGINEER_ID ")
    private int engineerId;
    @Column(name = "CONNECTION_ID")
    private int connectionId;
    @Column(name = "CONNECTION_TYPE")
    private String connectionType;
    @Column(name = "SERVICE_TYPE")
    private String serviceType;
    @Column(name = "TICKET_RAISED_DATE")
    private LocalDateTime ticketRaisedDate;
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;
    
}