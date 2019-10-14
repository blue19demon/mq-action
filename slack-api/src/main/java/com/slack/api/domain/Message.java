package com.slack.api.domain;

import java.io.Serializable;

import com.slack.api.server.AcknowledgeMode;
import com.slack.api.server.DeliverMode;

import lombok.Data;
@Data
public class Message<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DeliverMode deliverMode;
	private AcknowledgeMode autoAcknowledge;
	private Long timestamp;
	private String msgId;
	private T context;
}
