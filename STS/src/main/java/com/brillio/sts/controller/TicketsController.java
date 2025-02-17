package com.brillio.sts.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brillio.sts.exception.AccountNotFoundException;
import com.brillio.sts.exception.InvalidReassignmentException;
import com.brillio.sts.exception.TicketAlreadyExistsException;
import com.brillio.sts.exception.TicketNotFoundException;
import com.brillio.sts.exception.UnauthorizedEngineerException;
import com.brillio.sts.model.Connections;
import com.brillio.sts.model.Tickets;
import com.brillio.sts.service.TicketsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RestController
@RequestMapping(value="/tickets")
public class TicketsController {
	
	@Autowired
	TicketsService ticketService;
	
	@GetMapping("/pending/{pincode}")
    public ResponseEntity<?> getPendingTicketsByPincode(@PathVariable int pincode) {
        try {
            List<Tickets> tickets = ticketService.getPendingTicketsByPincode(pincode);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
	@GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTickets(@PathVariable int userId) {
        try {
            List<Tickets> tickets = ticketService.getTicketsByUserId(userId);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
	@GetMapping("/getByEngineerId/{engineerId}")
    public List<Tickets> getTicketsByEngineerId(@PathVariable int engineerId) {
        return ticketService.getTicketsByEngineerId(engineerId);
    }
	@GetMapping("/getByEngineerIds/{engineerId}")
    public List<Tickets> getTicketsByEngineerIds(@PathVariable int engineerId) {
        return ticketService.getTicketsByEngineerIds(engineerId);
    }
		
	@PutMapping("/updateStatus/{ticketId}")
    public ResponseEntity<String> updateTicketStatus(
            @PathVariable int ticketId,
            @RequestParam int engineerId,
            @RequestParam String status) {
        try {
            ticketService.updateTicketStatus(ticketId, engineerId, status);
            return ResponseEntity.ok("Ticket status updated successfully");
        } catch (TicketNotFoundException | UnauthorizedEngineerException e) {
            return ResponseEntity.status(403).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
	@PostMapping("/raiseInstallationTicket")
    public ResponseEntity<String> raiseInstallationTicket(@RequestBody Map<String, Object> requestData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Connections connection = objectMapper.convertValue(requestData.get("connection"), Connections.class);
            Tickets ticket = objectMapper.convertValue(requestData.get("ticket"), Tickets.class);
            String response = ticketService.raiseTicketInstallation(connection, ticket);
            return ResponseEntity.ok(response);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
	@GetMapping("/getBestEngineer/{ticketId}")
	  public ResponseEntity<Map<String, Double>> getBestEngineerLocation(@PathVariable int ticketId) {
			Map<String, Double> response = ticketService.getBestEngineerLocation(ticketId);
			  return ResponseEntity.ok(response);
	}
	
	@PostMapping("/raiseTicketsFault")
    public ResponseEntity<String> raiseTicketsFault(@RequestBody Tickets ticket) {
        try {
            String response = ticketService.raiseTicketFault(ticket);
            return ResponseEntity.ok(response);
        } catch (TicketAlreadyExistsException | AccountNotFoundException e) {
            return ResponseEntity.status(409).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
	
	@GetMapping("/deferred/{pincode}")
    public List<Tickets> getDeferredTicketsByPincode(@PathVariable int pincode) {
        return ticketService.getDeferredTicketsByPincode(pincode);
    }

	@GetMapping("/rejected/{pincode}")
    public List<Tickets> getRejectedTicketsByPincode(@PathVariable int pincode) {
        return ticketService.getRejectedTicketsByPincode(pincode);
    }
	
	 @PutMapping("/reassign/{ticketId}")
     public ResponseEntity<String> reassignEngineer(@PathVariable int ticketId) {
         try {
             String result = ticketService.reassignEngineer(ticketId);
             return ResponseEntity.ok(result);
         } catch (TicketNotFoundException | InvalidReassignmentException e) {
             return ResponseEntity.status(400).body("Error: " + e.getMessage());
         } catch (AccountNotFoundException e) {
             return ResponseEntity.status(404).body("Error: " + e.getMessage());
         } catch (Exception e) {
             return ResponseEntity.badRequest().body("Error: " + e.getMessage());
         }
     }

}
