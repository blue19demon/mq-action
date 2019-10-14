package com.slack.api.server;

import java.io.Serializable;

import lombok.Data;
@Data
public class JMSClientConnMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip;
	private Integer port;
}
