package com.brillio.sts.controller;
 
import java.util.List;
import java.util.NoSuchElementException;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
 
import com.brillio.sts.model.Connections;
 
import com.brillio.sts.service.ConnectionsService;
 
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RestController
@RequestMapping(value = "/connections")
public class ConnectionsController {
	@Autowired
	private ConnectionsService connectionsService;
	
	@GetMapping(value = "/showConnections")
	public List<Connections> showConnections(){
		return connectionsService.showConnections();
	}
	
	@GetMapping(value = "/searchConnection/{id}")
	public ResponseEntity<Connections> getById(@PathVariable int id){
		try {
			Connections connection = connectionsService.searchById(id);
			return new ResponseEntity<Connections>(connection,HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<Connections>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(value="/addConnection")
	public void addConnection(@RequestBody Connections connection) {
		connectionsService.addConnections(connection);
	}
	
	@GetMapping(value = "/searchConnectionUserId/{userId}")
	public List<Connections> searchByUserId(@PathVariable int userId){
		return connectionsService.searchByUserAndStatus(userId);
	}
 
}