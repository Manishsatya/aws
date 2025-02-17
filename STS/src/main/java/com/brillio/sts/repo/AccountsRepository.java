package com.brillio.sts.repo;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brillio.sts.model.Accounts;
@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Integer>{
	List<Accounts> findByRole(String role);
	
	List<Accounts> getByPincode(int pincode);
	
	Accounts findFirstByRoleOrderByIdAsc(String role);
	
	Accounts findFirstByRoleAndPincodeOrderByIdAsc(String role, Integer pincode);
	
	@Query("SELECT MAX(a.id) FROM Accounts a")
    Integer findMaxAccountId();
		
	Optional<Accounts> findByEmail(String email);
	
	List<Accounts> findByAccountStatusAndRole(String status, String role);

	List<Accounts> findByAccountStatusAndPincodeAndRoleIn(String status, int pincode, List<String> role);
	
	List<Accounts> findByPincode(int pincode); 
	
	//It will return a list of Accounts entities that match the provided role and pincode parameters.
	List<Accounts> findByRoleAndPincode(String role,int pincode);
	
	List<Accounts> findByRoleAndPincodeAndAccountStatus(String role,int pincode,String accountStatus);
	
	List<Integer> findIdsByRoleAndPincode(String role,int pincode);
	
	long countByAccountStatusAndRoleInAndPincode(String accountStatus, List<String> roles, int pincode);

	long countByAccountStatusAndRoleAndPincode(String accountStatus, String role, int pincode);
	

}
