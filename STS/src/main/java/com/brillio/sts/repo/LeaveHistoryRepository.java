package com.brillio.sts.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brillio.sts.model.LeaveHistory;

@Repository
public interface LeaveHistoryRepository extends JpaRepository<LeaveHistory, Integer>{
	
	// Find all leave history records for a specific engineer
    List<LeaveHistory> findByEngineerId(int engineerId);

    // Find all pending leave requests for a specific admin's pincode
    List<LeaveHistory> findByLeaveStatusAndEngineerIdIn(String leaveStatus, List<Integer> engineerIds);
    
    List<LeaveHistory> findByEngineerIdAndLeaveStatus(int engineerId, String leaveStatus);

}
