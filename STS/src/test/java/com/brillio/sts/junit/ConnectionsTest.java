package com.brillio.sts.junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
 
import org.junit.jupiter.api.Test;
 
import com.brillio.sts.model.Connections;
 
public class ConnectionsTest {
 
    // ✅ 1. Test Getters and Setters (Positive)
    @Test
    public void testGettersAndSetters() {
        Connections connection = new Connections();
        Date startDate = new Date();
        Date expiryDate = new Date();
 
        connection.setConnectionId(1);
        connection.setUserId(101);
        connection.setConnectionType("DTH");
        connection.setStartDate(startDate);
        connection.setValidityPeriod(12);
        connection.setExpiryDate(expiryDate);
        connection.setEndDate(null);
        connection.setStatus("ACTIVE");
 
        assertEquals(1, connection.getConnectionId());
        assertEquals(101, connection.getUserId());
        assertEquals("DTH", connection.getConnectionType());
        assertEquals(startDate, connection.getStartDate());
        assertEquals(12, connection.getValidityPeriod());
        assertEquals(expiryDate, connection.getExpiryDate());
        assertNull(connection.getEndDate());
        assertEquals("ACTIVE", connection.getStatus());
    }
 
    // ✅ 2. Test No-Args Constructor (Positive)
    @Test
    public void testNoArgsConstructor() {
        Connections newConnection = new Connections();
        assertNotNull(newConnection);
    }
 
    // ✅ 3. Test All-Args Constructor (Positive)
    @Test
    public void testAllArgsConstructor() {
        Date startDate = new Date();
        Date expiryDate = new Date();
 
        Connections newConnection = new Connections(1, 101, "WIFI", startDate, 6, expiryDate, null, "INACTIVE");
 
        assertEquals(1, newConnection.getConnectionId());
        assertEquals(101, newConnection.getUserId());
        assertEquals("WIFI", newConnection.getConnectionType());
        assertEquals(startDate, newConnection.getStartDate());
        assertEquals(6, newConnection.getValidityPeriod());
        assertEquals(expiryDate, newConnection.getExpiryDate());
        assertNull(newConnection.getEndDate());
        assertEquals("INACTIVE", newConnection.getStatus());
    }
 
    // ✅ 4. Test toString() Method (Positive)
    @Test
    public void testToString() {
        Date startDate = new Date();
        Date expiryDate = new Date();
 
        Connections newConnection = new Connections(1, 101, "WIFI", startDate, 6, expiryDate, null, "INACTIVE");
 
        String expectedString = "Connections(connectionId=1, userId=101, connectionType=WIFI, startDate="
                + startDate + ", validityPeriod=6, expiryDate=" + expiryDate + ", endDate=null, status=INACTIVE)";
 
        assertEquals(expectedString, newConnection.toString());
    }
 
    // ❌ 5. Negative Test: Getters and Setters with Invalid Data
    @Test
    public void testNegativeGettersAndSetters() {
        Connections connection = new Connections();
        connection.setConnectionId(-1); // ❌ Negative ID
        connection.setUserId(-101); // ❌ Negative User ID
        connection.setConnectionType(""); // ❌ Empty Connection Type
        connection.setStartDate(null); // ❌ Null Start Date
        connection.setValidityPeriod(-6); // ❌ Negative Validity Period
        connection.setExpiryDate(null); // ❌ Null Expiry Date
        connection.setEndDate(new Date()); // ❌ Should be null
        connection.setStatus("Unknown"); // ❌ Invalid Status
 
        assertNotEquals(1, connection.getConnectionId());
        assertNotEquals(101, connection.getUserId());
        assertNotEquals("DTH", connection.getConnectionType());
        assertNull(connection.getStartDate());
        assertNotEquals(12, connection.getValidityPeriod());
        assertNull(connection.getExpiryDate());
        assertNotNull(connection.getEndDate()); // ❌ Should be null
        assertNotEquals("ACTIVE", connection.getStatus());
    }
 
    // ❌ 6. Negative Test: toString() should not match incorrect format
    @Test
    public void testNegativeToString() {
        Date startDate = null; // ❌ Null Timestamp
        Connections connection = new Connections(-1, -101, "", startDate, -6, null, new Date(), "Unknown");
 
        String unexpectedResult = "Connections [connectionId=-1, userId=-101, connectionType=, startDate=null, "
                + "validityPeriod=-6, expiryDate=null, endDate=someDate, status=Unknown]";
 
        assertNotEquals(unexpectedResult, connection.toString());
    }
 
    // ❌ 7. Negative Test: Constructor with Invalid Data
    @Test
    public void testNegativeConstructor() {
        Date startDate = null; // ❌ Null Start Date
        Connections connection = new Connections(-1, -101, "", startDate, -6, null, new Date(), "Unknown");
 
        assertNotEquals(1, connection.getConnectionId());
        assertNotEquals(101, connection.getUserId());
        assertNotEquals("DTH", connection.getConnectionType());
        assertNull(connection.getStartDate());
        assertNotEquals(12, connection.getValidityPeriod());
        assertNull(connection.getExpiryDate());
        assertNotNull(connection.getEndDate()); // ❌ Should be null
        assertNotEquals("ACTIVE", connection.getStatus());
    }
}
