package com.brillio.sts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;
@RestControllerAdvice
public class ControllerExceptionHandler {
	
	
	 @ExceptionHandler(AccountNotFoundException.class)
	    @ResponseStatus(value = HttpStatus.NOT_FOUND)
	    public ErrorMessage handleAccountNotFoundException(AccountNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.NOT_FOUND.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false));
	    }

	    @ExceptionHandler(SecurityAnswerMismatchException.class)
	    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleSecurityAnswerMismatchException(SecurityAnswerMismatchException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.BAD_REQUEST.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false));
	    }
	
	
	 @ExceptionHandler(TicketNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ErrorMessage handleTicketNotFoundException(TicketNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.NOT_FOUND.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }

	    @ExceptionHandler(ConnectionNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ErrorMessage handleConnectionNotFoundException(ConnectionNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.NOT_FOUND.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }
	    
	    @ExceptionHandler(UnauthorizedEngineerException.class)
	    @ResponseStatus(HttpStatus.FORBIDDEN)
	    public ErrorMessage handleUnauthorizedEngineerException(UnauthorizedEngineerException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.FORBIDDEN.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }

	    @ExceptionHandler(TicketAlreadyExistsException.class)
	    @ResponseStatus(HttpStatus.CONFLICT)
	    public ErrorMessage handleTicketAlreadyExistsException(TicketAlreadyExistsException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.CONFLICT.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }
	    
	    @ExceptionHandler(InvalidReassignmentException.class)
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public ErrorMessage handleInvalidReassignmentException(InvalidReassignmentException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.BAD_REQUEST.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }
	    
	    @ExceptionHandler(EngineerNotFoundException.class)
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ErrorMessage handleEngineerNotFoundException(EngineerNotFoundException ex, WebRequest request) {
	        return new ErrorMessage(
	                HttpStatus.NOT_FOUND.value(),
	                new Date(),
	                ex.getMessage(),
	                request.getDescription(false)
	        );
	    }
	
	

}
