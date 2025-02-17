package com.brillio.sts.service;
 
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.sts.exception.AccountNotFoundException;
import com.brillio.sts.exception.ConnectionNotFoundException;
import com.brillio.sts.model.Accounts;
import com.brillio.sts.model.Connections;
import com.brillio.sts.repo.ConnectionsRepository;
 
import jakarta.transaction.Transactional;
 
@Service
@Transactional
public class ConnectionsService {
	
	@Autowired
	private ConnectionsRepository connectionsRepository;
	
	public List<Connections> showConnections(){
		return connectionsRepository.findAll();
	}
	
	public Connections searchById(int connectionId) {

		 Optional<Connections> connection = connectionsRepository.findById(connectionId);
		 if (!connection.isPresent()) {
	            throw new ConnectionNotFoundException("Account with ID " +connectionId  + " not found.");
	        }
	        return connection.get();
	    }
	
	
	
	public Connections addConnections(Connections connection) {
	    connection.setStatus("INACTIVE");  // Keep consistent status handling
	    return connectionsRepository.save(connection);
	}

	
	public List<Connections> searchByUserId(int userId){
		return connectionsRepository.findByuserId(userId);
	}	
	
	public List<Connections> searchByUserAndStatus(int userId){
		
		return connectionsRepository.findByuserIdAndStatus(userId,"ACTIVE");
	}
	
	public Date setExpiryDate(Connections connection) {
	    if (connection.getStartDate() != null) {
	        LocalDate startDate = connection.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        LocalDate expiryDate = startDate.plusMonths(connection.getValidityPeriod());
	        Date expiry = Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//	        connection.setExpiryDate(expiry);
	        return expiry;
	    }
	    return null; // Return null if expiry date cannot be set
	}

}