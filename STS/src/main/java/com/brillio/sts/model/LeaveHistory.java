package com.brillio.sts.model;

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
@Table(name = "LEAVE_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveHistory {
	
	@Id
	@Column(name = "LEAVE_ID")
	private int leaveId;
	@Column(name = "LEAVE_NO_OF_DAYS")
	private int leaveNoOfDays;
	@Column(name = "ADMIN_COMMENTS")
	private String adminComments;
	@Column(name = "ENGINEER_ID")
	private int engineerId;
	@Column(name = "LEAVE_START_DATE")
	private Date leaveStartDate;
	@Column(name = "LEAVE_END_DATE")
	private Date leaveEndDate;
	@Column(name = "LEAVE_TYPE")
	private String leaveType;
	@Column(name = "LEAVE_STATUS")
	private String leaveStatus;
	@Column(name = "LEAVE_REASON")
	private String leaveReason;

}
