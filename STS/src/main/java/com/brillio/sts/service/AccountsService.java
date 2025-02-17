package com.brillio.sts.service;

import java.util.Arrays;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brillio.sts.exception.AccountNotFoundException;
import com.brillio.sts.exception.SecurityAnswerMismatchException;
import com.brillio.sts.model.Accounts;
import com.brillio.sts.model.Connections;
import com.brillio.sts.model.Tickets;
import com.brillio.sts.repo.AccountsRepository;
import com.brillio.sts.repo.ConnectionsRepository;

import jakarta.transaction.Transactional;

/**
 * Service Class for Accounts Done by @author Manish.Chapala
 */

@Service
@Transactional
public class AccountsService implements UserDetailsService{
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private ConnectionsRepository connectionsRepository;
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	@Autowired
	private ConnectionsService connectionsService;
	
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private EmailService emailService;
	
	
	/**
	 * Displays All the accounts in the Database
	 */
	public Optional<Accounts> getAccountById(int userId) {
        return accountsRepository.findById(userId);
    }
	
	
	public List<Accounts> showAccounts(){
		return accountsRepository.findAll();
	}
	
	/**
	 * Displays a particular account in the Database based on the ID
	 */	
	public Accounts searchById(int id) {
        Optional<Accounts> account = accountsRepository.findById(id);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account with ID " + id + " not found.");
        }
        return account.get();
    }
	
	/**
	 * Gets the Max value of Id From the database and Adds +1 
	 */	
	public List<Accounts> getUsersByPincode(int pincode){
		return accountsRepository.findByRoleAndPincodeAndAccountStatus("USER", pincode,"APPROVED");
	}
	
	
	public List<Accounts> getEngineerByPincode(int pincode){
		return accountsRepository.findByRoleAndPincodeAndAccountStatus("ENGINEER", pincode, "APPROVED");
	}
	
	public Integer getNextAccountId() {
	    Integer maxId = accountsRepository.findMaxAccountId();
	    if(maxId != null){
	    	return maxId + 1;
	    }else {
	    	return 1;
	    }
	}
	
	/**
	 * To Register an Account based on the condition.
	 */
	
	public String addAccount(Accounts account, Connections connection) {
	    List<Accounts> accList = accountsRepository.findAll();
	    account.setAccountStatus("PENDING");
	    account.setPassword(encoder.encode(account.getPassword()));

	    // If no accounts exist, make the first user an approved ADMIN
	    if (accList.isEmpty()) {
	        account.setAccountStatus("APPROVED");
	        account.setRole("ADMIN");
	        account = accountsRepository.save(account);
	        return "First account created successfully as ADMIN with APPROVED status.";
	    }

	    // Handling ADMIN registration requests
	    if ("ADMIN".equalsIgnoreCase(account.getRole())) {
	        Accounts firstAdmin = accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN");
	        
	        if (firstAdmin != null) {
	            Accounts existingAdmin = accountsRepository.findFirstByRoleAndPincodeOrderByIdAsc("ADMIN", account.getPincode());
	            
	            if (existingAdmin != null && "APPROVED".equals(existingAdmin.getAccountStatus())) {
	                return "An approved ADMIN already exists for this pincode. Cannot register another.";
	            }

	            account = accountsRepository.save(account);
	            return "Admin approval request sent to the first registered ADMIN.";
	        }
	    }

	    // Handling ENGINEER and USER registrations
	    if ("ENGINEER".equalsIgnoreCase(account.getRole()) || "USER".equalsIgnoreCase(account.getRole())) {
	        Accounts adminWithSamePincode = accountsRepository.findFirstByRoleAndPincodeOrderByIdAsc("ADMIN", account.getPincode());

	        if (adminWithSamePincode != null && "APPROVED".equals(adminWithSamePincode.getAccountStatus())) {
	            account = accountsRepository.save(account);
	            
	            if ("USER".equalsIgnoreCase(account.getRole()) && connection != null) {
	                connection.setUserId(account.getId());
	                connection.setStatus("INACTIVE");
	                connectionsRepository.save(connection);
	            }

	            return "Approval request sent to the ADMIN with the same pincode.";
	        }
	    }

	    return "Account creation failed: No eligible APPROVED ADMIN found for approval.";
	}


	
	/**
	 * Displays All the accounts in the Database that matches the mentioned role.
	 */
	public List<Accounts> searchByRole(String role) {
		return accountsRepository.findByRole(role);
	}
	
	/**
	 * Displays All the accounts in the Database that matches the mentioned Pincode.
	 */
	
	public List<Accounts> showAccountsByPincode(int pincode) {
		return accountsRepository.getByPincode(pincode);
	}

	 @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        Optional<Accounts> userDetail = accountsRepository.findByEmail(username);
	        if (!userDetail.isPresent()) {
	            throw new UsernameNotFoundException("Email not found: " + username);
	        }
	        return new AccountDetails(userDetail.get());
	    }
	
	/**
	 * Receives the requests of the 
	 * @param role
	 * @param email
	 * @return
	 */
	 public List<Accounts> getApprovalRequests(String role, String email) {
		    // Fetch the admin details using the email
		    Accounts currentAdmin = accountsRepository.findByEmail(email)
		            .orElseThrow(() -> new AccountNotFoundException("Admin not found with email: " + email));

		    if ("ADMIN".equalsIgnoreCase(role)) {
		        // Check if the current admin is the first registered admin
		        Accounts firstAdmin = accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN");

		        if (firstAdmin != null && firstAdmin.getEmail().equals(email)) {
		            // First admin sees all pending admin requests
		            return accountsRepository.findByAccountStatusAndRole("PENDING", "ADMIN");
		        }

		        // Regular admins see pending ENGINEERS & USERS within their pincode
		        return accountsRepository.findByAccountStatusAndPincodeAndRoleIn(
		                "PENDING", currentAdmin.getPincode(), Arrays.asList("ENGINEER", "USER"));
		    }
		    
		    return Collections.emptyList();

//		    throw new UnauthorizedAccessException("Only admins can view approval requests.");
		}

	
	public Accounts searchByEmail(String email) {
	    Optional<Accounts> account = accountsRepository.findByEmail(email);
	    if (!account.isPresent()) {
	        throw new AccountNotFoundException("Account with email " + email + " not found.");
	    }
	    return account.get();
	}
	

	public void updateAccountStatus(int id, String status) {

	    Optional<Accounts> accountOpt = accountsRepository.findById(id);
        if (!accountOpt.isPresent()) {
            throw new AccountNotFoundException("Account with ID " + id + " not found.");
        }
        
        Accounts account = accountOpt.get();
	    
	    account.setAccountStatus(status); // Set the status based on the parameter
	    accountsRepository.save(account);
	 // Fetch user email
	    String userEmail = account.getEmail();
	    String subject;
	    String body;

	    // If the status is APPROVED and the role is USER, create a ticket
	    if ("APPROVED".equals(status) && "USER".equals(account.getRole())) {
	    	List<Connections> connections = connectionsService.searchByUserId(id);

	    	if (!connections.isEmpty()) { // Ensure the list is not empty
	    	    Connections connection = connections.get(0); // Fetch the first connection

	    	    Tickets ticket = new Tickets();
	    	    ticket.setDescription("My First Connection");

	    	    // Raise installation ticket
	    	    String result = ticketsService.raiseTicketInstallation(connection, ticket);
	    	    System.out.println(result); // Debugging log
	    	}   subject = "Your Account Has Been Approved!";
            body = "<h3>Hello " + account.getFirstName() + ",</h3>"
                    + "<p>Congratulations! Your account has been <b>APPROVED</b>.</p>"
                    + "<p>A new ticket has been created for your connection.</p>"
                    + "<p>Thank you for choosing our service.</p>";

            boolean emailSent = emailService.sendEmail(userEmail, subject, body);
            System.out.println("📧 Email sent to user: " + emailSent);
        } else {
            System.out.println(" No connections found for user ID: " + id);
        }
	 //  If role is ADMIN or ENGINEER (Send approval email)
	    if ("APPROVED".equals(status) && ("ADMIN".equals(account.getRole()) || "ENGINEER".equals(account.getRole()))) {
	        subject = "🎉 Your Account Has Been Approved!";
	        body = "<h3>Hello " + account.getFirstName() + ",</h3>"
	                + "<p>Congratulations! Your account has been <b>APPROVED</b> as a <b>" + account.getRole() + "</b>.</p>"
	                + "<p>You now have full access to the system.</p>"
	                + "<p>Thank you for joining our service.</p>";
 
	        boolean emailSent = emailService.sendEmail(userEmail, subject, body);
	        System.out.println("📧 Approval email sent to " + account.getRole() + ": " + emailSent);
	    }
	    
	    
	    if("REJECTED".equals(status) && "USER".equals(account.getRole())) {
	    	List<Connections> connections = connectionsService.searchByUserId(id);
	    	
	    	if (!connections.isEmpty()) {
	    		Connections connection = connections.get(0);
	    		
	    		connectionsRepository.deleteById(connection.getConnectionId());
	    	}
	    	   // Send rejection email
	        subject = "⚠️ Your Account Has Been Rejected";
	        body = "<h3>Hello " + account.getFirstName() + ",</h3>"
	                + "<p>Unfortunately, your account request has been <b>REJECTED</b>.</p>"
	                + "<p>If you believe this was a mistake, please contact our support team.</p>"
	                + "<p>Thank you.</p>";
 
	        boolean emailSent = emailService.sendEmail(userEmail, subject, body);
	        System.out.println("📧 Rejection email sent: " + emailSent);
	    
	    }
	}
	
	
	// Fetch security question for a given email
	public String getSecurityQuestionByEmail(String email) {
	    Optional<Accounts> accountOpt = accountsRepository.findByEmail(email);
	    
	    if (!accountOpt.isPresent() || !"APPROVED".equals(accountOpt.get().getAccountStatus())) {
	        throw new AccountNotFoundException("Account with email " + email + " not found.");
	    }

	    return accountOpt.get().getSecurityQuestion();
	}



    // Verify security answer
	public boolean verifySecurityAnswer(String email, String answer) {
        Optional<Accounts> account = accountsRepository.findByEmail(email);
        if (!account.isPresent()) {
            throw new AccountNotFoundException("Account with email " + email + " not found.");
        }
        if (!account.get().getSecurityAnswer().equalsIgnoreCase(answer)) {
            throw new SecurityAnswerMismatchException("Security answer is incorrect.");
        }
        return true;
    }
    
    
    // Update password if security answer is correct
	public boolean updatePassword(String email, String newPassword) {
        Optional<Accounts> accountOpt = accountsRepository.findByEmail(email);
        if (!accountOpt.isPresent()) {
            throw new AccountNotFoundException("Account with email " + email + " not found.");
        }
        accountOpt.get().setPassword(encoder.encode(newPassword));
        accountsRepository.save(accountOpt.get());
        return true;
    }
	
	public long getPendingEngineersAndUsersCount(int pincode) {
	    return accountsRepository.countByAccountStatusAndRoleInAndPincode("PENDING", List.of("ENGINEER", "USER"), pincode);
	}

	public long getPendingEngineersCount(int pincode) {
	    return accountsRepository.countByAccountStatusAndRoleAndPincode("PENDING", "ENGINEER", pincode);
	}

	public long getPendingUsersCount(int pincode) {
	    return accountsRepository.countByAccountStatusAndRoleAndPincode("PENDING", "USER", pincode);
	}
    

}
