package com.slack.api.server;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 响应回复
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JMSClientCommitBackMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msgId;
	//通道名称
	private String name;
}
