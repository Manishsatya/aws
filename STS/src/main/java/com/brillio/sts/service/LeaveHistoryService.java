package com.brillio.sts.service;

import com.brillio.sts.model.LeaveHistory;
import com.brillio.sts.model.Accounts;
import com.brillio.sts.repo.LeaveHistoryRepository;
import com.brillio.sts.repo.AccountsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveHistoryService {
    
    @Autowired
    private LeaveHistoryRepository leaveHistoryRepository;

    @Autowired
    private AccountsRepository accountsRepository;
    
    public int calculateDays(Date leaveStartDate, Date leaveEndDate) {
		long ms = leaveEndDate.getTime() - leaveStartDate.getTime();
		long diff = ((ms) / (1000 * 60 * 60 * 24)) + 1;
		int days = (int)diff;
		return days;
	}
    
    public String applyLeave(LeaveHistory leaveHistory) {
        Date today = new Date();
        
 

        int leaveDays = calculateDays(leaveHistory.getLeaveStartDate(), leaveHistory.getLeaveEndDate());
        int daysToStart = calculateDays(today, leaveHistory.getLeaveStartDate());
        int daysToEnd = calculateDays(today, leaveHistory.getLeaveEndDate());

        if (leaveDays < 1) {
            return "Leave start date must not be greater than leave end date.";
        }
        if (daysToStart < 0) {
            return "Leave start date cannot be in the past.";
        }
        if (daysToEnd < 0) {
            return "Leave end date cannot be in the past.";
        }

        Optional<Accounts> accountOpt = accountsRepository.findById(leaveHistory.getEngineerId());
        if (accountOpt.isEmpty()) {
            return "Employee not found.";
        }

        Accounts account = accountOpt.get();
        
        leaveHistory.setLeaveNoOfDays(leaveDays);
        leaveHistory.setLeaveStatus("PENDING");
        leaveHistoryRepository.save(leaveHistory);

        accountsRepository.save(account);

        return "Leave Applied Successfully.";
    }
    
    public boolean isEngineerOnLeave(int engineerId, Date date) {
        
        List<LeaveHistory> leaveRecords = leaveHistoryRepository.findByEngineerIdAndLeaveStatus(engineerId, "APPROVED");

        for (LeaveHistory leave : leaveRecords) {
            if (!date.before(leave.getLeaveStartDate()) && !date.after(leave.getLeaveEndDate())) {
                return true; // Engineer is on leave for the given date
            }
        }
        return false; // Engineer is available
    }

    
    public List<LeaveHistory> getLeavesByPincode(int pincode) {
        List<Accounts> engineers = accountsRepository.findByRoleAndPincode("ENGINEER", pincode);
        List<Integer> engineerIds = engineers.stream().map(Accounts::getId).collect(Collectors.toList());
        
        if (engineerIds.isEmpty()) {
            return List.of(); // Return empty list if no engineers found in the pincode
        }
        
        return leaveHistoryRepository.findByLeaveStatusAndEngineerIdIn("PENDING", engineerIds);
    }
    
    public String approveOrRejectLeave(int leaveId, String status, String comments) {
        LeaveHistory leaveHistory = leaveHistoryRepository.findById(leaveId).orElse(null);
        if (leaveHistory == null) {
            return "Leave request not found";
        }
        leaveHistory.setLeaveStatus(status);
        leaveHistory.setAdminComments(comments);
        leaveHistoryRepository.save(leaveHistory);
        return "Leave status updated successfully";
    }
}
