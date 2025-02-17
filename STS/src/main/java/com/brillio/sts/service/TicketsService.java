package com.brillio.sts.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.sts.exception.AccountNotFoundException;
import com.brillio.sts.exception.ConnectionNotFoundException;
import com.brillio.sts.exception.EngineerNotFoundException;
import com.brillio.sts.exception.InvalidReassignmentException;
import com.brillio.sts.exception.TicketAlreadyExistsException;
import com.brillio.sts.exception.TicketNotFoundException;
import com.brillio.sts.exception.UnauthorizedEngineerException;
import com.brillio.sts.model.Accounts;
import com.brillio.sts.model.Connections;
import com.brillio.sts.model.Tickets;
import com.brillio.sts.repo.AccountsRepository;
import com.brillio.sts.repo.ConnectionsRepository;
import com.brillio.sts.repo.TicketsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TicketsService {
	
	@Autowired
	private ConnectionsService connectionsService;
	
	@Autowired
	private TicketsRepository ticketsRepository;
	
	@Autowired
	private ConnectionsRepository connectionsRepository;
	
	@Autowired
	private AccountsRepository accountsRepository;
		
	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private LeaveHistoryService leaveHistoryService;
	
	@Autowired
    private TicketHistoryService ticketHistoryService;
	
	@Autowired
	private EmailService emailService;
	
	
//	to fetch pending tickets of same pincode
	public List<Tickets> getPendingTicketsByPincode(int pincode) {
		
		return ticketsRepository.findByStatusAndPincodeOrderByPriorityAsc("IN_PROGRESS", pincode);
    }
	
	public List<Tickets> getTicketsByUserId(int userId) {
        return ticketsRepository.findByUserIdAndStatusIn(userId, Arrays.asList("PENDING", "IN_PROGRESS", "DEFERRED"));
    }
	
	public List<Tickets> getTicketsByEngineerId(int engineerId){
		return ticketsRepository.findByEngineerIdAndStatus(engineerId, "PENDING");
	}
	
	public List<Tickets> getTicketsByEngineerIds(int engineerId){
		return ticketsRepository.findByEngineerIdAndStatus(engineerId, "IN_PROGRESS");
	}
	
	public void updateTicketStatus(int ticketId, int engineerId, String status) {
		 Tickets ticket = ticketsRepository.findById(ticketId).orElse(null);

		    if (ticket == null) {
		        throw new TicketNotFoundException("Ticket not found");
		    }
		    if (ticket.getEngineerId() != engineerId) {
		        throw new UnauthorizedEngineerException("Engineer not authorized for this ticket");
		    }
 
        ticket.setStatus(status);
        ticket.setUpdatedAt(new Date());
        ticketsRepository.save(ticket);
 
        // Call TicketHistoryService when status is "COMPLETED" or "FAILED"
        if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
            ticketHistoryService.finalizeTicketStatus(ticket);
            if ("COMPLETED".equals(status)) {
            	int connectionId = ticket.getConnectionId();
            	Connections connection = connectionsRepository.findById(connectionId).orElse(null);

                if (connection == null) {
                    throw new ConnectionNotFoundException("Connection not found for ID: " + connectionId);
                }
                connection.setStatus("ACTIVE");
                connection.setStartDate(new Date());
                connection.setExpiryDate(connectionsService.setExpiryDate(connection));;
                connectionsRepository.save(connection);
            }       
        
        } 
    }
	
	  public String raiseTicketInstallation(Connections connection, Tickets ticket) {
	        // Fetch user details
		  Accounts account = accountsService.getAccountById(connection.getUserId()).orElse(null);

		    if (account == null) {
		        throw new AccountNotFoundException("User account not found");
		    }

	        // Save the connection first
	        Connections savedConnection = connectionsService.addConnections(connection);
	 
	        // Populate ticket details
	        ticket.setUserId(connection.getUserId());
	        ticket.setConnectionId(savedConnection.getConnectionId());
	        ticket.setConnectionType(connection.getConnectionType());
	        ticket.setPincode(account.getPincode());
	        ticket.setAddress(account.getAddress());
	        ticket.setLatitude(account.getLatitude());
	        ticket.setLongitude(account.getLongitude());
	        ticket.setServiceType("INSTALLATION");
	 
	        // Assign Engineer based on workload and distance
	        Integer assignedEngineerId = getBestEngineer(ticket.getLatitude(), ticket.getLongitude(), ticket.getPincode(), null);
	 
	        if (assignedEngineerId != null) {
	            ticket.setEngineerId(assignedEngineerId);
	            ticket.setStatus("PENDING");
	        }
	 
	        // Assign priority
	        String priority = assignPriority(ticket.getServiceType(), ticket.getConnectionType());
	        if (priority == null) {
	            throw new IllegalArgumentException("Invalid service type or connection type for priority assignment.");
	        }
	        ticket.setPriority(priority);
	 
	        // Save ticket
	        ticketsRepository.save(ticket);
	        // Send Email Notification
		    String subject = "🔔 Ticket Raised Successfully";
		    String body = "<h2>Your ticket has been successfully raised!</h2>"
		            + "<p><strong>Service Type:</strong> " + ticket.getServiceType() + "</p>"
		            + "<p><strong>Connection Type:</strong> " + ticket.getConnectionType() + "</p>"
		            + "<p><strong>Priority:</strong> " + ticket.getPriority() + "</p>"
		            + "<p><strong>Assigned Engineer:</strong> " + (assignedEngineerId != null ? assignedEngineerId : "Not Assigned") + "</p>"
		            + "<p><strong>Status:</strong> " + ticket.getStatus() + "</p>"
		            + "<br><p>Thank you for using our service!</p>";
	 
		    boolean emailSent = emailService.sendEmail(account.getEmail(), subject, body);
	 
		    if (emailSent) {
		        System.out.println("✅ Email notification sent to user: " + account.getEmail());
		    } else {
		        System.out.println("❌ Failed to send email to user: " + account.getEmail());
		    }
	 
	        return "Connection Created & Ticket Raised Successfully";
	    }
	 
	    //Raise Fault Ticket (Now Uses Workload & Distance-Based Assignment)
//	    public String raiseTicketFault(Tickets ticket) {
//	        // Fetch user account details
//	        Accounts account = accountsService.getAccountById(ticket.getUserId())
//	                .orElseThrow(() -> new RuntimeException("User account not found"));
//	 
//	        // Set ticket details
//	        ticket.setPincode(account.getPincode());
//	        ticket.setAddress(account.getAddress());
//	        ticket.setLatitude(account.getLatitude());
//	        ticket.setLongitude(account.getLongitude());
//	        ticket.setServiceType("FAULT");
//	 
//	        // Assign priority
//	        String priority = assignPriority(ticket.getServiceType(), ticket.getConnectionType());
//	        if (priority == null) {
//	            throw new IllegalArgumentException("Invalid service type or connection type for priority assignment.");
//	        }
//	        ticket.setPriority(priority);
//	 
//	        // Assign Engineer based on workload and distance
//	        Integer assignedEngineerId = getBestEngineer(ticket.getLatitude(), ticket.getLongitude(), ticket.getPincode());
//	 
//	        if (assignedEngineerId != null) {
//	            ticket.setEngineerId(assignedEngineerId);
//	            ticket.setStatus("PENDING");
//	        }
//	 
//	        // Save ticket
//	        ticketsRepository.save(ticket);
//	 
//	        return "Ticket For Fault Raised Successfully";
//	    }
//	 
	  
	  public String raiseTicketFault(Tickets ticket) {
		    // Check if a ticket already exists for the same connectionId with restricted statuses
		    List<String> restrictedStatuses = Arrays.asList("PENDING", "DEFERRED", "IN_PROGRESS");
		    boolean ticketExists = ticketsRepository.existsByConnectionIdAndStatusIn(ticket.getConnectionId(), restrictedStatuses);

		    if (ticketExists) {
		        throw new TicketAlreadyExistsException("A ticket with this connection is already in progress or pending.");
		    }

		    Accounts account = accountsService.getAccountById(ticket.getUserId()).orElse(null);

		    if (account == null) {
		    	 throw new AccountNotFoundException("User account not found");
		    }	    		

		    // Set ticket details
		    ticket.setPincode(account.getPincode());
		    ticket.setAddress(account.getAddress());
		    ticket.setLatitude(account.getLatitude());
		    ticket.setLongitude(account.getLongitude());
		    ticket.setServiceType("FAULT");

		    // Assign priority
		    String priority = assignPriority(ticket.getServiceType(), ticket.getConnectionType());
		    if (priority == null) {
		        throw new IllegalArgumentException("Invalid service type or connection type for priority assignment.");
		    }
		    ticket.setPriority(priority);

		    // Assign Engineer based on workload and distance
		    Integer assignedEngineerId = getBestEngineer(ticket.getLatitude(), ticket.getLongitude(), ticket.getPincode(), null);

		    if (assignedEngineerId == null) {
		        throw new AccountNotFoundException("No available engineers found in pincode: " + ticket.getPincode());
		    }
		    if (assignedEngineerId != null) {
		        ticket.setEngineerId(assignedEngineerId);
		        ticket.setStatus("PENDING");
		    }

		    // Save ticket
		    ticketsRepository.save(ticket);
		 // Send Email Notification
		    String subject = "🚨 Fault Ticket Raised Successfully";
		    String body = "<h2>Your fault ticket has been successfully raised!</h2>"
		            + "<p><strong>Service Type:</strong> " + ticket.getServiceType() + "</p>"
		            + "<p><strong>Connection Type:</strong> " + ticket.getConnectionType() + "</p>"
		            + "<p><strong>Priority:</strong> " + ticket.getPriority() + "</p>"
		            + "<p><strong>Assigned Engineer:</strong> " + (assignedEngineerId != null ? assignedEngineerId : "Not Assigned Yet") + "</p>"
		            + "<p><strong>Status:</strong> " + ticket.getStatus() + "</p>"
		            + "<br><p>We will resolve your issue as soon as possible!</p>";
 
		    boolean emailSent = emailService.sendEmail(account.getEmail(), subject, body);
 
		    if (emailSent) {
		        System.out.println("✅ Email notification sent to user: " + account.getEmail());
		    } else {
		        System.out.println("❌ Failed to send email to user: " + account.getEmail());
		    }
		    return "Ticket For Fault Raised Successfully";
		}
	    
//	    public Integer getBestEngineer(double userLat, double userLng, int pincode) {
//	        // Step 1: Find engineers in the same pincode
//	        List<Accounts> engineers = accountsRepository.findByRoleAndPincode("ENGINEER", pincode);
//	 
////	        // If no engineers in the same pincode, find engineers from all locations
////	        if (engineers.isEmpty()) {
////	            engineers = accountsRepository.findByRole("ENGINEER");
////	        }
//	 
//	        // Step 2: Find engineers with the least assigned tickets
//	        engineers.sort(Comparator.comparingInt(engineer -> (int) ticketsRepository.countByEngineerIdAndStatus(engineer.getId(), "IN_PROGRESS")));
//	 
//	        int minWorkload = (int) ticketsRepository.countByEngineerIdAndStatus(engineers.get(0).getId(), "IN_PROGRESS");
//	 
//	        List<Accounts> leastBusyEngineers = engineers.stream()
//	                .filter(e -> ticketsRepository.countByEngineerIdAndStatus(e.getId(), "IN_PROGRESS") == minWorkload)
//	                .collect(Collectors.toList());
//	 
//	        // Step 3: If multiple engineers have the same workload, assign based on shortest distance
//	        return leastBusyEngineers.size() == 1
//	                ? leastBusyEngineers.get(0).getId()
//	                : getClosestEngineer(leastBusyEngineers, userLat, userLng);
//	    }
	 
	   // Find Closest Engineer
//	    private Integer getClosestEngineer(List<Accounts> engineers, double userLat, double userLng) {
//	        return engineers.stream()
//	                .min(Comparator.comparingDouble(e -> calculateDistance(userLat, userLng, e.getLatitude(), e.getLongitude())))
//	                .map(Accounts::getId)
//	                .orElse(null);
//	    }
//
	    // Assign Engineer Based on Workload & Distance
//	    public Integer getBestEngineer(double userLat, double userLng, int pincode) {
//	        Date today = new Date();
//	        
//	        // Step 1: Find available engineers in the same pincode who are not on leave
//	        List<Accounts> availableEngineers = accountsRepository.findByRoleAndPincode("ENGINEER", pincode)
//	                .stream()
//	                .filter(engineer -> !leaveHistoryService.isEngineerOnLeave(engineer.getId(), today))
//	                .collect(Collectors.toList());
//
//	        // If no available engineers, return null
//	        if (availableEngineers.isEmpty()) {
//	            return null;
//	        }
//
//	        // Step 2: Find engineers with the least assigned tickets
//	        availableEngineers.sort(Comparator.comparingInt(engineer -> 
//	            (int) ticketsRepository.countByEngineerIdAndStatus(engineer.getId(), "IN_PROGRESS")
//	        ));
//
//	        int minWorkload = (int) ticketsRepository.countByEngineerIdAndStatus(availableEngineers.get(0).getId(), "IN_PROGRESS");
//
//	        List<Accounts> leastBusyEngineers = availableEngineers.stream()
//	                .filter(e -> ticketsRepository.countByEngineerIdAndStatus(e.getId(), "IN_PROGRESS") == minWorkload)
//	                .collect(Collectors.toList());
//
//	        // Step 3: If multiple engineers have the same workload, assign based on shortest distance
//	        return (leastBusyEngineers.size() == 1) 
//	                ? leastBusyEngineers.get(0).getId() 
//	                : getClosestEngineer(leastBusyEngineers, userLat, userLng);
//	    }
	  
	  public Integer getBestEngineer(double userLat, double userLng, int pincode, Integer excludeEngineerId) {
		    Date today = new Date();
		    
		    // Step 1: Find available engineers in the same pincode who are not on leave
		    List<Accounts> availableEngineers = accountsRepository.findByRoleAndPincode("ENGINEER", pincode)
		            .stream()
		            .filter(engineer -> !leaveHistoryService.isEngineerOnLeave(engineer.getId(), today))
		            // Exclude the given engineer if reassigning
		            .filter(engineer -> excludeEngineerId == null || engineer.getId() != excludeEngineerId)
		            .collect(Collectors.toList());

		    // If no available engineers, return null
		    if (availableEngineers.isEmpty()) {
		        return null;
		    }

		    // Step 2: Find engineers with the least assigned tickets
		    availableEngineers.sort(Comparator.comparingInt(engineer -> 
		        (int) ticketsRepository.countByEngineerIdAndStatus(engineer.getId(), "IN_PROGRESS")
		    ));

		    int minWorkload = (int) ticketsRepository.countByEngineerIdAndStatus(availableEngineers.get(0).getId(), "IN_PROGRESS");

		    List<Accounts> leastBusyEngineers = availableEngineers.stream()
		            .filter(e -> ticketsRepository.countByEngineerIdAndStatus(e.getId(), "IN_PROGRESS") == minWorkload)
		            .collect(Collectors.toList());

		    // Step 3: If multiple engineers have the same workload, assign based on shortest distance
		    return (leastBusyEngineers.size() == 1) 
		            ? leastBusyEngineers.get(0).getId() 
		            : getClosestEngineer(leastBusyEngineers, userLat, userLng);
		}

		// Find Closest Engineer
		private Integer getClosestEngineer(List<Accounts> engineers, double userLat, double userLng) {
		    return engineers.stream()
		            .min(Comparator.comparingDouble(e -> calculateDistance(userLat, userLng, e.getLatitude(), e.getLongitude())))
		            .map(Accounts::getId)
		            .orElse(null);
		}

	    //Haversine Formula for Distance Calculation
	    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
	        final int R = 6371; // Earth radius in km
	        double dLat = Math.toRadians(lat2 - lat1);
	        double dLon = Math.toRadians(lon2 - lon1);
	        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	        return R * c; // Distance in km
	    }
	 
	    //Assign Priority Logic
	    private String assignPriority(String serviceType, String connectionType) {
	        if (serviceType == null || connectionType == null) {
	            return null;
	        }
	        serviceType = serviceType.toUpperCase();
	        connectionType = connectionType.toUpperCase();
	        if ("FAULT".equals(serviceType)) {
	            switch (connectionType) {
	                case "WIFI": return "P1";
	                case "DTH": return "P2";
	                case "LANDLINE": return "P3";
	            }
	        } else if ("INSTALLATION".equals(serviceType)) {
	            switch (connectionType) {
	                case "WIFI": return "P4";
	                case "DTH": return "P5";
	                case "LANDLINE": return "P6";
	            }
	        }
	        return null; // Invalid case
	    }
	    
	    public Map<String, Double> getBestEngineerLocation(int ticketId) 
	            throws TicketNotFoundException, EngineerNotFoundException, AccountNotFoundException {

	        Tickets ticket = ticketsRepository.findById(ticketId).orElse(null);
	        if (ticket == null) {
	            throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
	        }

	        Integer assignedEngineerId = getBestEngineer(ticket.getLatitude(), ticket.getLongitude(), 
	                                                     ticket.getPincode(), ticket.getEngineerId());

	        if (assignedEngineerId == null) {
	            throw new EngineerNotFoundException("No suitable engineer found for ticket ID: " + ticketId);
	        }

	        Accounts engineer = accountsRepository.findById(assignedEngineerId).orElse(null);
	        if (engineer == null) {
	            throw new AccountNotFoundException("Assigned engineer not found with ID: " + assignedEngineerId);
	        }

	        Map<String, Double> locations = new HashMap<>();
	        locations.put("userLat", ticket.getLatitude());
	        locations.put("userLng", ticket.getLongitude());
	        locations.put("engineerLat", engineer.getLatitude());
	        locations.put("engineerLng", engineer.getLongitude());

	        return locations;
	    }


		
		public List<Tickets> getDeferredTicketsByPincode(int pincode) {
	        return ticketsRepository.findByStatusAndPincode("DEFERRED", pincode);
	    }
	
	 public List<Tickets> getRejectedTicketsByPincode(int pincode) {
	        return ticketsRepository.findByStatusAndPincode("REJECTED", pincode);
	    }
	 
	 /**
	  * 
	  * @param ticketId
	  * @return
	  */
	 public String reassignEngineer(int ticketId) {
		    // Get the ticket that needs reassignment
		 Tickets ticket = ticketsRepository.findById(ticketId).orElse(null);

		    if (ticket == null) {
		        throw new TicketNotFoundException("Ticket not found");
		    }
		    // Check if ticket is eligible for reassignment (DEFERRED or REJECTED status)
		    if (!ticket.getStatus().equals("DEFERRED") && !ticket.getStatus().equals("REJECTED")) {
		        throw new InvalidReassignmentException("Ticket is not eligible for reassignment. Status must be DEFERRED or REJECTED");
		    }


		    // Get the current engineer ID
		    int currentEngineerId = ticket.getEngineerId();

		    // Find the best available engineer excluding the current engineer
		    Integer bestEngineerId = getBestEngineer(ticket.getLatitude(), ticket.getLongitude(), ticket.getPincode(), ticket.getEngineerId());

		    if (bestEngineerId == null) {
		        throw new  AccountNotFoundException("No suitable alternative engineer found in pincode: " + ticket.getPincode());
		    }

		    // Update the ticket with the new engineer and reset status to PENDING
		    ticket.setEngineerId(bestEngineerId);
		    ticket.setStatus("PENDING");

		    // Save the updated ticket
		    ticketsRepository.save(ticket);

		    // Return success message with details
		    return String.format("Ticket %d successfully reassigned from engineer %d to engineer %d in pincode %d", 
		                        ticketId, currentEngineerId, bestEngineerId, ticket.getPincode());
		}
	
}
