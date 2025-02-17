package com.brillio.sts.exception;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@NoArgsConstructor
@AllArgsConstructor
@Data 
public class ErrorMessage {
 
	  private int statusCode;
	  private Date timestamp;
	  private String message;
	  private String description;
}