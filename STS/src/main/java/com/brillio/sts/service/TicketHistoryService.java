package com.brillio.sts.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.brillio.sts.model.TicketHistory;

import com.brillio.sts.model.Tickets;

import com.brillio.sts.repo.TicketHistoryRepository;

import com.brillio.sts.repo.TicketsRepository;


import jakarta.transaction.Transactional;

@Service

@Transactional

public class TicketHistoryService {

	@Autowired

	private TicketHistoryRepository ticketHistoryRepository;

	@Autowired

	private  TicketsRepository ticketsRepository;

//	public TicketHistoryService(TicketHistoryRepository ticketHistoryRepository, TicketsRepository ticketsRepository) {

//        this.ticketHistoryRepository = ticketHistoryRepository;

//        this.ticketsRepository = ticketsRepository;

//    }

	public List<TicketHistory> showTicketHistory(){

		return ticketHistoryRepository.findAll();

	}

	public List<TicketHistory> getTicketHistoryByUserId(int userId) {


        return ticketHistoryRepository.findByUserId(userId);

    }

	public List<TicketHistory> getTicketHistoryByEngineerId(int engineerId) {


        return ticketHistoryRepository.findByEngineerId(engineerId);

    }

	public List<TicketHistory> getTicketHistoryByPincode(int pincode) {

        // Step 1: Find ticket IDs assigned to this user

        List<Integer> ticketIds = ticketsRepository.findByPincode(pincode)

                                                   .stream()

                                                   .map(Tickets::getTicketId)  // Ensure field name is ticketId

                                                   .collect(Collectors.toList());

        // Step 2: If no tickets found, return an empty list

        if (ticketIds.isEmpty()) {

            return List.of();

        }

        // Step 3: Fetch history records for these ticket IDs

        return ticketHistoryRepository.findByTicketIdIn(ticketIds);

    }
	
	
	public void finalizeTicketStatus(Tickets ticket) {
        TicketHistory history = new TicketHistory();
        history.setTicketId(ticket.getTicketId());
        history.setUserId(ticket.getUserId());
        history.setEngineerId(ticket.getEngineerId());
        history.setStatus(ticket.getStatus());
        history.setConnectionId(ticket.getConnectionId());
        history.setConnectionType(ticket.getConnectionType());
        history.setServiceType(ticket.getServiceType());
        history.setTicketRaisedDate(ticket.getCreatedAt());
        history.setLastUpdatedDate(ticket.getUpdatedAt());
        
 
        ticketHistoryRepository.save(history);
    }
}


 
	