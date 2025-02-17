package com.brillio.sts.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brillio.sts.model.Accounts;
import com.brillio.sts.model.Connections;


@Repository
public interface ConnectionsRepository extends JpaRepository<Connections, Integer>{
	List<Connections> findByuserId(int userId);
	List<Connections> findByuserIdAndStatus(int userId, String Status);
	@Query("SELECT MAX(c.id) FROM Connections c")
    Integer findMaxConnectionId();
	
}
