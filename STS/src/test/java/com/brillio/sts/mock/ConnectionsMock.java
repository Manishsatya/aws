package com.brillio.sts.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import com.brillio.sts.model.Connections;
import com.brillio.sts.repo.ConnectionsRepository;
import com.brillio.sts.service.ConnectionsService;
 
@ExtendWith(MockitoExtension.class) // Enables Mockito integration with JUnit 5
public class ConnectionsMock {
 
    @Mock
    private ConnectionsRepository connectionsRepository; // Mocked repository
 
    @InjectMocks
    private ConnectionsService connectionsService; // Injects mock repository into service
 
    private Connections mockConnection1;
    private Connections mockConnection2;
 
    @BeforeEach
    void setUp() {
        mockConnection1 = new Connections(1, 101, "DTH", null, 12, null, null, "ACTIVE");
        mockConnection2 = new Connections(2, 102, "WIFI", null, 6, null, null, "INACTIVE");
    }
 
    // ✅ 1. Test: Fetch all connections
    @Test
    public void testShowConnections_ShouldReturnConnectionList() {
        when(connectionsRepository.findAll()).thenReturn(Arrays.asList(mockConnection1, mockConnection2));
 
        List<Connections> connectionsList = connectionsService.showConnections();
 
        assertNotNull(connectionsList);
        assertEquals(2, connectionsList.size());
        verify(connectionsRepository, times(1)).findAll();
    }
 
    // ❌ 2. Test: Fetch all connections (Empty List)
    @Test
    public void testShowConnections_ShouldReturnEmptyList() {
        when(connectionsRepository.findAll()).thenReturn(Arrays.asList());
 
        List<Connections> connectionsList = connectionsService.showConnections();
 
        assertNotNull(connectionsList);
        assertEquals(0, connectionsList.size());
        verify(connectionsRepository, times(1)).findAll();
    }
 
    // ✅ 3. Test: Search connection by ID (Exists)
    @Test
    public void testSearchById_ShouldReturnConnection() {
        when(connectionsRepository.findById(1)).thenReturn(Optional.of(mockConnection1));
 
        Connections connection = connectionsService.searchById(1);
 
        assertNotNull(connection);
        assertEquals(101, connection.getUserId());
        verify(connectionsRepository, times(1)).findById(1);
    }
 
 
 
 
    // ✅ 6. Test: Add new connection (Defaults to INACTIVE)
    @Test
    public void testAddConnections_ShouldReturnSavedConnection() {
        Connections newConnection = new Connections(3, 103, "LANDLINE", null, 12, null, null, null);
        when(connectionsRepository.save(any(Connections.class))).thenReturn(newConnection);
 
        Connections savedConnection = connectionsService.addConnections(newConnection);
 
        assertNotNull(savedConnection);
        assertEquals("INACTIVE", savedConnection.getStatus()); // ✅ Ensures default status
        verify(connectionsRepository, times(1)).save(newConnection);
    }
 
    // ✅ 7. Test: Search connections by user ID (Exists)
    @Test
    public void testSearchByUserId_ShouldReturnConnections() {
        when(connectionsRepository.findByuserId(101)).thenReturn(Arrays.asList(mockConnection1));
 
        List<Connections> connections = connectionsService.searchByUserId(101);
 
        assertNotNull(connections);
        assertEquals(1, connections.size());
        verify(connectionsRepository, times(1)).findByuserId(101);
    }
 
    // ❌ 8. Test: Search connections by user ID (Not Found)
    @Test
    public void testSearchByUserId_NoMatchingConnections_ShouldReturnEmptyList() {
        when(connectionsRepository.findByuserId(999)).thenReturn(Arrays.asList());
 
        List<Connections> connections = connectionsService.searchByUserId(999);
 
        assertNotNull(connections);
        assertEquals(0, connections.size());
        verify(connectionsRepository, times(1)).findByuserId(999);
    }
 
    // ✅ 9. Test: Search active connections by user ID
    @Test
    public void testSearchByUserAndStatus_ShouldReturnActiveConnections() {
        when(connectionsRepository.findByuserIdAndStatus(101, "ACTIVE")).thenReturn(Arrays.asList(mockConnection1));
 
        List<Connections> activeConnections = connectionsService.searchByUserAndStatus(101);
 
        assertNotNull(activeConnections);
        assertEquals(1, activeConnections.size());
        assertEquals("ACTIVE", activeConnections.get(0).getStatus());
        verify(connectionsRepository, times(1)).findByuserIdAndStatus(101, "ACTIVE");
    }
 
    // ❌ 10. Test: Search active connections by user ID (Not Found)
    @Test
    public void testSearchByUserAndStatus_NoActiveConnections_ShouldReturnEmptyList() {
        when(connectionsRepository.findByuserIdAndStatus(102, "ACTIVE")).thenReturn(Arrays.asList());
 
        List<Connections> activeConnections = connectionsService.searchByUserAndStatus(102);
 
        assertNotNull(activeConnections);
        assertEquals(0, activeConnections.size());
        verify(connectionsRepository, times(1)).findByuserIdAndStatus(102, "ACTIVE");
    }
}
