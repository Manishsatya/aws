package com.brillio.sts.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brillio.sts.model.Tickets;
@Repository
public interface TicketsRepository extends JpaRepository<Tickets, Integer>{
//	to fetch pending tickets of same pincode
	List<Tickets> findByStatusAndUserIdIn(String status, List<Integer> userId);
	List<Tickets> findByUserIdAndStatusIn(int userId, List<String> status);
	List<Tickets> findByEngineerIdAndStatus(int engineerId, String status);
	List<Tickets>findByPincode(int pincode);
	List<Tickets> findByStatusAndPincodeOrderByPriorityAsc(String status, int pincode);
	List<Tickets> findByPincodeOrderByEngineerId(int pincode);
	// Count tickets for each engineerId for a given pincode and sort by count (Ascending)
    List<Tickets> findByPincodeOrderByEngineerIdAsc(int pincode);
 
    // Count the number of tickets assigned to an engineer in a particular pincode
    long countByEngineerIdAndStatus(int engineerId, String status);
	List<Tickets> findByEngineerIdIn(List<Integer> engineerIds);

	List<Tickets> findByStatusAndPincode(String status, int pincode);
	
	boolean existsByConnectionIdAndStatusIn(int connectionId, List<String> statuses);
	 

}
