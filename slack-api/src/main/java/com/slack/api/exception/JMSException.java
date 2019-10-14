package com.slack.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class JMSException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer code;
	private String message;
	
}
