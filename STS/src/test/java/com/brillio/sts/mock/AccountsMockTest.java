//package com.brillio.sts.mock;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.util.*;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.brillio.sts.exception.AccountNotFoundException;
//import com.brillio.sts.exception.SecurityAnswerMismatchException;
//import com.brillio.sts.model.Accounts;
//import com.brillio.sts.model.Connections;
//import com.brillio.sts.repo.AccountsRepository;
//import com.brillio.sts.repo.ConnectionsRepository;
//import com.brillio.sts.service.AccountDetails;
//import com.brillio.sts.service.AccountsService;
//import com.brillio.sts.service.ConnectionsService;
//import com.brillio.sts.service.EmailService;
//import com.brillio.sts.service.TicketsService;
//
//class AccountsMockTest {
//
//    @Mock
//    private PasswordEncoder encoder;
//
//    @Mock
//    private AccountsRepository accountsRepository;
//
//    @Mock
//    private ConnectionsRepository connectionsRepository;
//
//    @Mock
//    private ConnectionsService connectionsService;
//
//    @Mock
//    private TicketsService ticketsService;
//    
//    @Mock
//    private EmailService emailService;
//
//    @InjectMocks
//    private AccountsService accountsService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAccountById_WhenExists() {
//        Accounts account = new Accounts();
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//
//        Optional<Accounts> result = accountsService.getAccountById(1);
//
//        assertTrue(result.isPresent());
//        assertEquals(account, result.get());
//    }
//
//    @Test
//    void testGetAccountById_WhenNotExists() {
//        when(accountsRepository.findById(1)).thenReturn(Optional.empty());
//
//        Optional<Accounts> result = accountsService.getAccountById(1);
//
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void testShowAccounts() {
//        List<Accounts> accounts = List.of(new Accounts(), new Accounts());
//        when(accountsRepository.findAll()).thenReturn(accounts);
//
//        List<Accounts> result = accountsService.showAccounts();
//
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    void testSearchById_Success() {
//        Accounts account = new Accounts();
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//
//        Accounts result = accountsService.searchById(1);
//
//        assertEquals(account, result);
//    }
//
//    @Test
//    void testSearchById_AccountNotFound() {
//        when(accountsRepository.findById(2)).thenReturn(Optional.empty());
//
//        AccountNotFoundException exception = assertThrows(
//            AccountNotFoundException.class,
//            () -> accountsService.searchById(2)
//        );
//
//        assertEquals("Account with ID 2 not found.", exception.getMessage());
//    }
//
//    @Test
//    void testGetUsersByPincode() {
//        List<Accounts> users = List.of(new Accounts(), new Accounts());
//        when(accountsRepository.findByRoleAndPincodeAndAccountStatus("USER", 12345, "APPROVED"))
//            .thenReturn(users);
//
//        List<Accounts> result = accountsService.getUsersByPincode(12345);
//
//        assertEquals(2, result.size());
//    }
//    
//    @Test
//    void testGetEngineerByPincode() {
//        List<Accounts> engineers = List.of(new Accounts(), new Accounts());
//        when(accountsRepository.findByRoleAndPincodeAndAccountStatus("ENGINEER", 12345, "APPROVED"))
//            .thenReturn(engineers);
//
//        List<Accounts> result = accountsService.getEngineerByPincode(12345);
//
//        assertEquals(2, result.size());
//    }
//
//
//    @Test
//    void testGetNextAccountId() {
//        when(accountsRepository.findMaxAccountId()).thenReturn(5);
//
//        int result = accountsService.getNextAccountId();
//
//        assertEquals(6, result);
//    }
//    
//    @Test
//    void testGetNextAccountId_WhenMaxIdIsNull() {
//        when(accountsRepository.findMaxAccountId()).thenReturn(null);
//
//        Integer result = accountsService.getNextAccountId();
//
//        assertEquals(1, result);
//    }
//
//
//    @Test
//    void testAddAccount_FirstAccount_Admin() {
//        when(accountsRepository.findAll()).thenReturn(Collections.emptyList());
//        when(encoder.encode("password")).thenReturn("encodedPassword");
//        when(accountsRepository.save(any(Accounts.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Accounts account = new Accounts();
//        account.setPassword("password");
//
//        String result = accountsService.addAccount(account, null);
//
//        assertEquals("First account created successfully as ADMIN with APPROVED status.", result);
//    }
//
//    @Test
//    void testAddAccount_AdminApprovalRequest() {
//        when(accountsRepository.findAll()).thenReturn(List.of(new Accounts()));
//        when(accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN")).thenReturn(new Accounts());
//        when(accountsRepository.save(any(Accounts.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Accounts account = new Accounts();
//        account.setRole("ADMIN");
//
//        String result = accountsService.addAccount(account, null);
//
//        assertEquals("Admin approval request sent to the first registered ADMIN.", result);
//    }
//
//    @Test
//    void testAddAccount_UserOrEngineerApprovalRequest() {
//        when(accountsRepository.findAll()).thenReturn(List.of(new Accounts()));
//        when(accountsRepository.findFirstByRoleAndPincodeOrderByIdAsc("ADMIN", 12345)).thenReturn(new Accounts());
//        when(accountsRepository.save(any(Accounts.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(connectionsRepository.save(any(Connections.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Accounts account = new Accounts();
//        account.setRole("USER");
//        account.setPincode(12345);
//
//        Connections connection = new Connections();
//
//        String result = accountsService.addAccount(account, connection);
//
//        assertEquals("Approval request sent to ADMIN with the same pincode.", result);
//    }
//    
//    @Test
//    void testAddAccount_NoEligibleAdminFound() {
//        Accounts account = new Accounts();
//        account.setRole("USER");
//        account.setPincode(12345);
//
//        when(accountsRepository.findAll()).thenReturn(List.of(new Accounts())); // Not the first account
//        when(accountsRepository.findFirstByRoleAndPincodeOrderByIdAsc("ADMIN", 12345)).thenReturn(null); // No ADMIN in the same pincode
//
//        String result = accountsService.addAccount(account, null);
//
//        assertEquals("Account creation failed: No eligible ADMIN found for approval.", result);
//    }
//
//    
//    @Test
//    void testGetApprovalRequests_FirstAdmin() {
//        Accounts firstAdmin = new Accounts();
//        firstAdmin.setEmail("first.admin@example.com");
//        firstAdmin.setRole("ADMIN");
//
//        Accounts pendingAdmin = new Accounts();
//        pendingAdmin.setRole("ADMIN");
//        pendingAdmin.setAccountStatus("PENDING");
//
//        when(accountsRepository.findByEmail("first.admin@example.com")).thenReturn(Optional.of(firstAdmin));
//        when(accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN")).thenReturn(firstAdmin);
//        when(accountsRepository.findByAccountStatusAndRole("PENDING", "ADMIN"))
//            .thenReturn(Collections.singletonList(pendingAdmin));
//
//        List<Accounts> result = accountsService.getApprovalRequests("ADMIN", "first.admin@example.com");
//
//        assertEquals(1, result.size());
//        assertEquals("ADMIN", result.get(0).getRole());
//        assertEquals("PENDING", result.get(0).getAccountStatus());
//    }
//
//    @Test
//    void testGetApprovalRequests_RegularAdminFetchingPendingUsersAndEngineers() {
//        Accounts admin = new Accounts();
//        admin.setEmail("admin@example.com");
//        admin.setRole("ADMIN");
//        admin.setPincode(56789);
//
//        Accounts firstAdmin = new Accounts();
//        firstAdmin.setEmail("first.admin@example.com");
//
//        Accounts pendingEngineer = new Accounts();
//        pendingEngineer.setRole("ENGINEER");
//        pendingEngineer.setAccountStatus("PENDING");
//        pendingEngineer.setPincode(56789);
//
//        Accounts pendingUser = new Accounts();
//        pendingUser.setRole("USER");
//        pendingUser.setAccountStatus("PENDING");
//        pendingUser.setPincode(56789);
//
//        when(accountsRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
//        when(accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN")).thenReturn(firstAdmin);
//        when(accountsRepository.findByAccountStatusAndPincodeAndRoleIn(
//            "PENDING", 
//            56789, 
//            Arrays.asList("ENGINEER", "USER"))
//        ).thenReturn(Arrays.asList(pendingEngineer, pendingUser));
//
//        List<Accounts> result = accountsService.getApprovalRequests("ADMIN", "admin@example.com");
//
//        assertEquals(2, result.size());
//        assertTrue(result.contains(pendingEngineer));
//        assertTrue(result.contains(pendingUser));
//    }
//
//    @Test
//    void testGetApprovalRequests_NonAdminUser() {
//        Accounts user = new Accounts();
//        user.setEmail("user@example.com");
//        user.setRole("USER");
//
//        when(accountsRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
//
//        List<Accounts> result = accountsService.getApprovalRequests("USER", "user@example.com");
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void testGetApprovalRequests_AdminNotFound() {
//        when(accountsRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
//
//        AccountNotFoundException exception = assertThrows(
//            AccountNotFoundException.class, 
//            () -> accountsService.getApprovalRequests("ADMIN", "notfound@example.com")
//        );
//
//        assertEquals("Admin not found with email: notfound@example.com", exception.getMessage());
//    }
//    
//    @Test
//    void testSearchByRole() {
//        List<Accounts> admins = List.of(new Accounts(), new Accounts());
//        when(accountsRepository.findByRole("ADMIN")).thenReturn(admins);
//
//        List<Accounts> result = accountsService.searchByRole("ADMIN");
//
//        assertEquals(2, result.size());
//    }
//    
//    @Test
//    void testShowAccountsByPincode() {
//        List<Accounts> accounts = List.of(new Accounts(), new Accounts());
//        when(accountsRepository.getByPincode(12345)).thenReturn(accounts);
//
//        List<Accounts> result = accountsService.showAccountsByPincode(12345);
//
//        assertEquals(2, result.size());
//    }
//
//
//
//    @Test
//    void testGetApprovalRequests_NoPendingAccounts() {
//        // Mock an admin with email "admin1@example.com"
//        Accounts admin = new Accounts();
//        admin.setEmail("admin1@example.com");
//        admin.setRole("ADMIN");
//        admin.setPincode(54322);
//
//        // Mock a different first admin
//        Accounts firstAdmin = new Accounts();
//        firstAdmin.setEmail("differentAdmin@example.com");
//
//        when(accountsRepository.findByEmail("admin1@example.com")).thenReturn(Optional.of(admin));
//        when(accountsRepository.findFirstByRoleOrderByIdAsc("ADMIN")).thenReturn(firstAdmin); // Ensure different email
//        when(accountsRepository.findByAccountStatusAndPincodeAndRoleIn(eq("PENDING"), eq(54322), anyList()))
//                .thenReturn(Collections.emptyList()); // Ensure non-null return
//
//        List<Accounts> result = accountsService.getApprovalRequests("ADMIN", "admin1@example.com");
//
//        assertTrue(result.isEmpty()); // Ensure the result is an empty list
//    }
//
//
//
//
//    @Test
//    void testUpdateAccountStatus_Approved() {
//        Accounts account = new Accounts();
//        account.setRole("USER");
//        account.setEmail("test@test.com");
//
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//        when(connectionsService.searchByUserId(1)).thenReturn(List.of(new Connections()));
//        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
//
//        accountsService.updateAccountStatus(1, "APPROVED");
//
//        verify(accountsRepository, times(1)).save(account);
//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
//    }
//
//    @Test
//    void testUpdateAccountStatus_Rejected() {
//        Accounts account = new Accounts();
//        account.setRole("USER");
//        account.setEmail("test@test.com");
//        Connections connection = new Connections();
//        connection.setConnectionId(1);
//
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//        when(connectionsService.searchByUserId(1)).thenReturn(List.of(connection));
//        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
//
//        accountsService.updateAccountStatus(1, "REJECTED");
//
//        verify(connectionsRepository, times(1)).deleteById(1);
//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
//    }
//
//    @Test
//    void testUpdateAccountStatus_AccountNotFound() {
//        when(accountsRepository.findById(1)).thenReturn(Optional.empty());
//
//        AccountNotFoundException exception = assertThrows(
//            AccountNotFoundException.class, 
//            () -> accountsService.updateAccountStatus(1, "APPROVED")
//        );
//
//        assertEquals("Account with ID 1 not found.", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"APPROVED", "REJECTED"})
//    void testUpdateAccountStatus_InvalidRole(String status) {
//        Accounts account = new Accounts();
//        account.setRole("ADMIN");
//
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//
//        accountsService.updateAccountStatus(1, status);
//
//        verify(accountsRepository, times(1)).save(account);
//    }
//
//    @Test
//    void testUpdateAccountStatus_Approved_NoConnectionsFound() {
//        Accounts account = new Accounts();
//        account.setRole("USER");
//        account.setEmail("test@test.com");
//
//        when(accountsRepository.findById(1)).thenReturn(Optional.of(account));
//        when(connectionsService.searchByUserId(1)).thenReturn(Collections.emptyList());
//        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);
//
//        accountsService.updateAccountStatus(1, "APPROVED");
//
//        verify(accountsRepository).save(account);
//        verify(connectionsService).searchByUserId(1);
//        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
//        verifyNoInteractions(ticketsService); // Ensure ticket creation is not called
//    }
//
//    
//
//    @Test
//    void testLoadUserByUsername_Success() {
//        Accounts account = new Accounts();
//        account.setEmail("test@test.com");
//        account.setPassword("encodedPassword");
//        account.setRole("USER"); // Make sure role is set!
//
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//
//        UserDetails userDetails = accountsService.loadUserByUsername("test@test.com");
//
//        assertNotNull(userDetails);
//        assertEquals("test@test.com", userDetails.getUsername());
//        assertEquals("encodedPassword", userDetails.getPassword());
//        assertFalse(userDetails.getAuthorities().isEmpty()); // Ensure roles are set
//    }
//
//
//
//    @Test
//    void testLoadUserByUsername_UserNotFound() {
//        when(accountsRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(UsernameNotFoundException.class, 
//            () -> accountsService.loadUserByUsername("notfound@test.com"));
//
//        assertEquals("Email not found: notfound@test.com", exception.getMessage()); // Fixed expected message
//    }
//
//    
//    @Test
//    void testAccountDetails_UserAccountStatusMethods() {
//        Accounts account = new Accounts();
//        account.setEmail("test@test.com");
//        account.setPassword("encodedPassword");
//        account.setRole("USER");
//
//        AccountDetails userDetails = new AccountDetails(account);
//
//        assertTrue(userDetails.isAccountNonExpired());
//        assertTrue(userDetails.isAccountNonLocked());
//        assertTrue(userDetails.isCredentialsNonExpired());
//        assertTrue(userDetails.isEnabled());
//    }
//
//
//    
//    @Test
//    void testSearchByEmail_Success() {
//        Accounts account = new Accounts();
//        account.setEmail("user@example.com");
//
//        when(accountsRepository.findByEmail("user@example.com")).thenReturn(Optional.of(account));
//
//        Accounts result = accountsService.searchByEmail("user@example.com");
//
//        assertNotNull(result);
//        assertEquals("user@example.com", result.getEmail());
//    }
//
//    @Test
//    void testSearchByEmail_AccountNotFound() {
//        when(accountsRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
//
//        AccountNotFoundException exception = assertThrows(
//            AccountNotFoundException.class,
//            () -> accountsService.searchByEmail("nonexistent@example.com")
//        );
//
//        assertEquals("Account with email nonexistent@example.com not found.", exception.getMessage());
//    }
//
//    @Test
//    void testGetSecurityQuestionByEmail_ApprovedAccount() {
//        Accounts account = new Accounts();
//        account.setSecurityQuestion("What is your pet's name?");
//        account.setAccountStatus("APPROVED"); // Ensure status is APPROVED
//
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//
//        String question = accountsService.getSecurityQuestionByEmail("test@test.com");
//
//        assertEquals("What is your pet's name?", question);
//    }
//
//    @Test
//    void testGetSecurityQuestionByEmail_NotApprovedAccount() {
//        Accounts account = new Accounts();
//        account.setSecurityQuestion("What is your pet's name?");
//        account.setAccountStatus("PENDING"); // Not approved
//
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//
//        Exception exception = assertThrows(AccountNotFoundException.class, 
//            () -> accountsService.getSecurityQuestionByEmail("test@test.com"));
//
//        assertEquals("Account with email test@test.com not found.", exception.getMessage());
//    }
//
//    @Test
//    void testGetSecurityQuestionByEmail_NoAccountFound() {
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(AccountNotFoundException.class, 
//            () -> accountsService.getSecurityQuestionByEmail("test@test.com"));
//
//        assertEquals("Account with email test@test.com not found.", exception.getMessage());
//    }
//
//
//    @Test
//    void testVerifySecurityAnswer_Success() {
//        Accounts account = new Accounts();
//        account.setEmail("test@test.com");
//        account.setSecurityAnswer("Dog");
//
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//
//        assertTrue(accountsService.verifySecurityAnswer("test@test.com", "Dog"));
//    }
//
//    @Test
//    void testVerifySecurityAnswer_IncorrectAnswer() {
//        Accounts account = new Accounts();
//        account.setEmail("test@test.com");
//        account.setSecurityAnswer("Dog");
//
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//
//        SecurityAnswerMismatchException exception = assertThrows(
//            SecurityAnswerMismatchException.class,
//            () -> accountsService.verifySecurityAnswer("test@test.com", "Cat")
//        );
//
//        assertEquals("Security answer is incorrect.", exception.getMessage());
//    }
//
//    @Test
//    void testVerifySecurityAnswer_AccountNotFound() {
//        when(accountsRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
//
//        AccountNotFoundException exception = assertThrows(
//            AccountNotFoundException.class,
//            () -> accountsService.verifySecurityAnswer("unknown@test.com", "Dog")
//        );
//
//        assertEquals("Account with email unknown@test.com not found.", exception.getMessage());
//    }
//
//    @Test
//    void testUpdatePassword() {
//        Accounts account = new Accounts();
//        when(accountsRepository.findByEmail("test@test.com")).thenReturn(Optional.of(account));
//        when(encoder.encode("newPass")).thenReturn("encodedPass");
//
//        assertTrue(accountsService.updatePassword("test@test.com", "newPass"));
//    }
//    
//    @Test
//    void testUpdatePassword_AccountNotFound() {
//        String email = "test@example.com";
//        String newPassword = "newPassword123";
//
//        when(accountsRepository.findByEmail(email)).thenReturn(Optional.empty()); // Account not found
//
//        Exception exception = assertThrows(AccountNotFoundException.class, 
//            () -> accountsService.updatePassword(email, newPassword));
//
//        assertEquals("Account with email test@example.com not found.", exception.getMessage());
//
//        verify(accountsRepository, never()).save(any(Accounts.class)); // Ensure save is never called
//    }
//
//
//}
