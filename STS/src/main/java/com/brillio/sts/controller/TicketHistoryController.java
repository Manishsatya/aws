package com.brillio.sts.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brillio.sts.model.TicketHistory;
import com.brillio.sts.service.TicketHistoryService;
@RestController
@RequestMapping(value = "/ticketHistory")
@CrossOrigin(origins="http://localhost:3000")
public class TicketHistoryController {
	@Autowired
	private TicketHistoryService ticketHistoryService;
	@GetMapping(value = "/showTicketsHistory")
	public List<TicketHistory> showTicketsHistory(){
		return ticketHistoryService.showTicketHistory();
	}
	@GetMapping("/userTicketHistory/{userId}")
    public List<TicketHistory> getTicketHistoryByUserId(@PathVariable int userId) {
        return ticketHistoryService.getTicketHistoryByUserId(userId);
    }
	@GetMapping("/engineerTicketHistory/{engineerId}")
    public List<TicketHistory> getTicketHistoryByEngineerId(@PathVariable int engineerId) {
        return ticketHistoryService.getTicketHistoryByEngineerId(engineerId);
    }
	@GetMapping("/pincodeTicketHistory/{pincode}")
    public List<TicketHistory> getTicketHistoryByPincode(@PathVariable int pincode) {
        return ticketHistoryService.getTicketHistoryByPincode(pincode);
    }
 
 

}