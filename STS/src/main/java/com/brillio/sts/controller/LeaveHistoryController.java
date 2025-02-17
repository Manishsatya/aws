package com.brillio.sts.controller;

import com.brillio.sts.model.LeaveHistory;
import com.brillio.sts.service.LeaveHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/leave")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveHistoryController {
    
    @Autowired
    private LeaveHistoryService leaveHistoryService;
    
    @PostMapping("/applyLeave")
    public ResponseEntity<String> applyLeave(@RequestBody LeaveHistory leaveHistory) {
        return ResponseEntity.ok(leaveHistoryService.applyLeave(leaveHistory));
    }
    
    @GetMapping("/getLeavesByPincode/{pincode}")
    public ResponseEntity<List<LeaveHistory>> getLeavesByPincode(@PathVariable int pincode) {
        return ResponseEntity.ok(leaveHistoryService.getLeavesByPincode(pincode));
    }
    
    @PutMapping("/updateStatus")
    public ResponseEntity<String> approveOrRejectLeave(@RequestParam int leaveId, @RequestParam String status, @RequestParam String comments) {
        return ResponseEntity.ok(leaveHistoryService.approveOrRejectLeave(leaveId, status, comments));
    }
    
    @GetMapping("/isOnLeave")
    public ResponseEntity<Boolean> isEngineerOnLeave(
            @RequestParam int engineerId, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        boolean isOnLeave = leaveHistoryService.isEngineerOnLeave(engineerId, date);
        return ResponseEntity.ok(isOnLeave);
    }
}