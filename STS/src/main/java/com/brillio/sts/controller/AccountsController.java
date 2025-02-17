package com.brillio.sts.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.brillio.sts.config.JwtService;
import com.brillio.sts.exception.AccountNotFoundException;
import com.brillio.sts.model.Accounts;
import com.brillio.sts.model.AuthRequest;
import com.brillio.sts.model.Connections;
import com.brillio.sts.service.AccountDetails;
import com.brillio.sts.service.AccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RestController
@RequestMapping(value = "/accounts")
public class AccountsController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private AccountsService accountsService;
	
	
	@GetMapping("/userBypincode/{pincode}")
	public List<Accounts> getUserByPincode(@PathVariable int pincode){
		return accountsService.getUsersByPincode(pincode);
	}
	
	@GetMapping("/engineerBypincode/{pincode}")
	public List<Accounts> getEnginnerByPincode(@PathVariable int pincode){
		return accountsService.getEngineerByPincode(pincode);
	}
	
 	@GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

 	@GetMapping("/admin/adminProfile")
 	@PreAuthorize("hasAuthority('ADMIN')")
 	public Map<String, String> adminProfile() {
 	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 	    String email = authentication.getName(); // Get logged-in email

 	    // Fetch account details by email
 	    Accounts adminAccount = (Accounts) accountsService.loadUserByUsername(email);
 	    
 	    if (adminAccount != null) {
 	        return Map.of("name", adminAccount.getFirstName()); // Return full name
 	    } else {
 	        return Map.of("error", "Admin not found");
 	    }
 	}
    
    @GetMapping("/engineer/engineerProfile")
    @PreAuthorize("hasAuthority('ENGINEER')")
    public String engineerProfile() {
        return "Welcome to Engineer Profile";
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            // Fetch user details from the database
            AccountDetails userDetails = (AccountDetails) accountsService.loadUserByUsername(authRequest.getUsername());

            // Check if the provided role matches the stored role
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority(authRequest.getRole()))) {
                throw new BadCredentialsException("Invalid role provided!");
            }

            // Fetch user account details
            Accounts userAccount = accountsService.searchByEmail(authRequest.getUsername());

            // Check if account is approved
            if (!"APPROVED".equalsIgnoreCase(userAccount.getAccountStatus())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not approved yet!");
            }

            // Authenticate username & password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(authRequest.getUsername());
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user request!");
            }
        } catch (AccountNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!", e);
        }
    }



	
	@GetMapping(value = "/showAccounts")
	public List<Accounts> showAccounts(){
		return accountsService.showAccounts();
	}
	
	@GetMapping(value = "/searchAccount/{id}")
	public ResponseEntity<Accounts> getById(@PathVariable int id){
		try {
			Accounts account = accountsService.searchById(id);
			return new ResponseEntity<Accounts>(account,HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<Accounts>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/nextAccountId")
    public ResponseEntity<Integer> getNextAccountId() {
        return ResponseEntity.ok(accountsService.getNextAccountId());
    }

	@PostMapping("/addAccount")
	public ResponseEntity<String> addAccount(@RequestBody Map<String, Object> payload) {
	    System.out.println("Received Payload: " + payload); // Debugging step

	    // Convert the account and connection data from the payload
	    Accounts account = new ObjectMapper().convertValue(payload.get("account"), Accounts.class);
	    Connections connection = payload.containsKey("connection") 
	        ? new ObjectMapper().convertValue(payload.get("connection"), Connections.class) 
	        : null;

	    System.out.println("Converted Account: " + account); // Debugging step
	    System.out.println("Converted Connection: " + connection); // Debugging step

	    // Call the service
	    String result = accountsService.addAccount(account, connection);
	    return ResponseEntity.ok(result);
	}

	
	@GetMapping("/approvalRequests")
	public List<Accounts> getApprovalRequests(@RequestParam String role, @RequestParam String email) {
	    return accountsService.getApprovalRequests(role, email);
	}
	
	
	@GetMapping(value = "/searchAccountByRole/{role}")
	public List<Accounts> getByRole(@PathVariable String role){
		return accountsService.searchByRole(role);
	}
	
	@GetMapping(value = "/showAccountsByPincode/{pincode}")
	public List<Accounts> getByPincode(@PathVariable int pincode){
		return accountsService.showAccountsByPincode(pincode);
	}
	
	@GetMapping("/search/{email}")
    public ResponseEntity<Accounts> getAccountByEmail(@PathVariable String email) {
        try {
            // Fetch account details
            Accounts account = accountsService.searchByEmail(email);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	@PutMapping("/updateStatus/{id}")
	public ResponseEntity<Void> updateAccountStatus(@PathVariable int id, @RequestBody Map<String, String> requestBody) {
	    String status = requestBody.get("status");
	    accountsService.updateAccountStatus(id, status);
	    return ResponseEntity.ok().build();
	}

	
	@GetMapping("/securityQuestion/{email}")
	public ResponseEntity<String> getSecurityQuestion(@PathVariable String email) {
	    try {
	        String securityQuestion = accountsService.getSecurityQuestionByEmail(email);
	        return ResponseEntity.ok(securityQuestion);
	    } catch (AccountNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}


    // Endpoint to verify the security answer
	 @PostMapping("/verifySecurityAnswer")
	 public boolean verifySecurityAnswer(@RequestBody Map<String, String> body) {
	     String email = body.get("email");
	     String answer = body.get("answer");
	     return accountsService.verifySecurityAnswer(email, answer);
	 }


    // Endpoint to reset the password
	 @PostMapping("/resetPassword")
	 public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> payload) {
	     String email = payload.get("email");
	     String newPassword = payload.get("newPassword");

	     boolean isPasswordUpdated = accountsService.updatePassword(email, newPassword);
	     
	     if (isPasswordUpdated) {
	         return ResponseEntity.ok().build(); // 200 OK for success
	     } else {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD REQUEST for failure
	     }
	 } 

}
