package com.brillio.sts.repo;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.brillio.sts.model.TicketHistory;
@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Integer> {
	List<TicketHistory> findByTicketIdIn(List<Integer> ticketId);
	List<TicketHistory> findByEngineerId(int engineerId);
	List<TicketHistory> findByUserId(int userId);
}