package com.slack.api.server;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
@Data
public class JMSMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AcknowledgeMode autoAcknowledge;
	private DeliverMode deliverMode;

	private Long timestamp=System.currentTimeMillis();
	private String msgId=UUID.randomUUID().toString();
	private String name;
	private Object content;
}
