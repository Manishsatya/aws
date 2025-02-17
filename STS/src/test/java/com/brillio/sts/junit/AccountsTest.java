package com.brillio.sts.junit;

import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.brillio.sts.model.Accounts;

public class AccountsTest {

    private Accounts account;

    @BeforeEach
    void setUp() {
        account = new Accounts(1, "John", "Doe", "john.doe@example.com", "password123", "USER",
                "9876543210", "123 Street, City", 123456, "What is your pet's name?", "Fluffy",
                "ACTIVE", "Male", 78.9629, 20.5937);
    }

    // Positive Test: No-Args Constructor
    @Test
    void testNoArgsConstructor() {
        Accounts emptyAccount = new Accounts();
        assertNotNull(emptyAccount);
    }

    // Negative Test: No-Args Constructor
    @Test
    void testNoArgsConstructorNegative() {
        Accounts emptyAccount = null;
        assertNull(emptyAccount);
    }

    // Positive Test: All-Args Constructor
    @Test
    void testAllArgsConstructor() {
        Accounts newAccount = new Accounts(2, "Jane", "Smith", "jane.smith@example.com", "pass456", "ADMIN",
                "1234567890", "456 Avenue, City", 654321, "What is your favorite color?", "Blue",
                "INACTIVE", "Female", 77.5559127, 12.9309217);

        assertEquals(2, newAccount.getId());
        assertEquals("Jane", newAccount.getFirstName());
        assertEquals("Smith", newAccount.getLastName());
        assertEquals("jane.smith@example.com", newAccount.getEmail());
        assertEquals("pass456", newAccount.getPassword());
        assertEquals("ADMIN", newAccount.getRole());
        assertEquals("1234567890", newAccount.getPhone());
        assertEquals("456 Avenue, City", newAccount.getAddress());
        assertEquals(654321, newAccount.getPincode());
        assertEquals("What is your favorite color?", newAccount.getSecurityQuestion());
        assertEquals("Blue", newAccount.getSecurityAnswer());
        assertEquals("INACTIVE", newAccount.getAccountStatus());
        assertEquals("Female", newAccount.getGender());
        assertEquals(77.5559127, newAccount.getLongitude(), 0.0001);
        assertEquals(12.9309217, newAccount.getLatitude(), 0.0001);
    }

    // Negative Test: All-Args Constructor with Invalid Data
    @Test
    void testAllArgsConstructorNegative() {
        Accounts newAccount = new Accounts(-1, "", "", "invalidEmail", "", "UNKNOWN",
                "000", "", -123456, "", "",
                "INVALID", "", -200.0, -200.0);

        assertNotEquals(1, newAccount.getId());
        assertNotEquals("John", newAccount.getFirstName());
        assertNotEquals("Doe", newAccount.getLastName());
        assertNotEquals("john.doe@example.com", newAccount.getEmail());
        assertNotEquals("password123", newAccount.getPassword());
        assertNotEquals("USER", newAccount.getRole());
        assertNotEquals("9876543210", newAccount.getPhone());
        assertNotEquals("123 Street, City", newAccount.getAddress());
        assertNotEquals(123456, newAccount.getPincode());
        assertNotEquals("What is your pet's name?", newAccount.getSecurityQuestion());
        assertNotEquals("Fluffy", newAccount.getSecurityAnswer());
        assertNotEquals("ACTIVE", newAccount.getAccountStatus());
        assertNotEquals("Male", newAccount.getGender());
        assertNotEquals(78.9629, newAccount.getLongitude(), 0.0001);
        assertNotEquals(20.5937, newAccount.getLatitude(), 0.0001);
    }

    // Positive Test: ToString()
    @Test
    void testToString() {
        String expectedString = "Accounts(id=1, firstName=John, lastName=Doe, email=john.doe@example.com, " +
                "password=password123, role=USER, phone=9876543210, address=123 Street, City, pincode=123456, " +
                "securityQuestion=What is your pet's name?, securityAnswer=Fluffy, accountStatus=ACTIVE, " +
                "gender=Male, longitude=78.9629, latitude=20.5937)";

        assertEquals(expectedString, account.toString());
    }

    // Negative Test: ToString() With Incorrect Output
    @Test
    void testToStringNegative() {
        String incorrectString = "Accounts(id=2, firstName=Jane, lastName=Smith, email=jane.doe@example.com)";
        assertNotEquals(incorrectString, account.toString());
    }

    // Positive Test: Setters and Getters
    @Test
    void testSettersAndGetters() {
        Accounts accounts = new Accounts();

        accounts.setId(2);
        assertEquals(2, accounts.getId());

        accounts.setFirstName("Jane");
        assertEquals("Jane", accounts.getFirstName());

        accounts.setLastName("Smith");
        assertEquals("Smith", accounts.getLastName());

        accounts.setEmail("jane.smith@example.com");
        assertEquals("jane.smith@example.com", accounts.getEmail());

        accounts.setPassword("newpassword");
        assertEquals("newpassword", accounts.getPassword());

        accounts.setRole("ADMIN");
        assertEquals("ADMIN", accounts.getRole());

        accounts.setPhone("1234567890");
        assertEquals("1234567890", accounts.getPhone());

        accounts.setAddress("456 Avenue, City");
        assertEquals("456 Avenue, City", accounts.getAddress());

        accounts.setPincode(654321);
        assertEquals(654321, accounts.getPincode());

        accounts.setSecurityQuestion("What is your mother's maiden name?");
        assertEquals("What is your mother's maiden name?", accounts.getSecurityQuestion());

        accounts.setSecurityAnswer("Charlie");
        assertEquals("Charlie", accounts.getSecurityAnswer());

        accounts.setAccountStatus("INACTIVE");
        assertEquals("INACTIVE", accounts.getAccountStatus());

        accounts.setGender("Female");
        assertEquals("Female", accounts.getGender());

        accounts.setLongitude(79.0000);
        assertEquals(79.0000, accounts.getLongitude(), 0.0001);

        accounts.setLatitude(21.0000);
        assertEquals(21.0000, accounts.getLatitude(), 0.0001);
    }

    // Negative Test: Setters and Getters with Invalid Values
    @Test
    void testSettersAndGettersNegative() {
        Accounts accounts = new Accounts();

        accounts.setId(-1);
        assertNotEquals(2, accounts.getId());

        accounts.setFirstName("");
        assertNotEquals("Jane", accounts.getFirstName());

        accounts.setLastName("");
        assertNotEquals("Smith", accounts.getLastName());

        accounts.setEmail("invalidEmail");
        assertNotEquals("jane.smith@example.com", accounts.getEmail());

        accounts.setPassword("");
        assertNotEquals("newpassword", accounts.getPassword());

        accounts.setRole("UNKNOWN");
        assertNotEquals("ADMIN", accounts.getRole());

        accounts.setPhone("000");
        assertNotEquals("1234567890", accounts.getPhone());

        accounts.setAddress("");
        assertNotEquals("456 Avenue, City", accounts.getAddress());

        accounts.setPincode(-654321);
        assertNotEquals(654321, accounts.getPincode());

        accounts.setSecurityQuestion("");
        assertNotEquals("What is your mother's maiden name?", accounts.getSecurityQuestion());

        accounts.setSecurityAnswer("");
        assertNotEquals("Charlie", accounts.getSecurityAnswer());

        accounts.setAccountStatus("UNKNOWN");
        assertNotEquals("INACTIVE", accounts.getAccountStatus());

        accounts.setGender("");
        assertNotEquals("Female", accounts.getGender());

        accounts.setLongitude(-300.0000);
        assertNotEquals(79.0000, accounts.getLongitude(), 0.0001);

        accounts.setLatitude(-300.0000);
        assertNotEquals(21.0000, accounts.getLatitude(), 0.0001);
    }
}
