package com.slack.api.core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.alibaba.fastjson.JSONObject;
import com.slack.api.domain.CommunicationWay;
import com.slack.api.domain.Message;
import com.slack.api.exception.JMSException;
import com.slack.api.server.JMSMessage;

public class MessageProducer {
	
	private Session session;
	
	private CommunicationWay communicationWay;
	
	private Socket socket=null;
	public MessageProducer(Session session, CommunicationWay communicationWay) {
		super();
		this.session = session;
		this.communicationWay = communicationWay;
	}

	public void send(Message<?> message) {
		ObjectOutputStream oos = null;
		try {
			ServerAddress serverAddress = session.getServerAddress();
			socket=new Socket(serverAddress.getIp(), serverAddress.getPort());
			oos = new ObjectOutputStream(socket.getOutputStream());
			JMSMessage jmsMessage = new JMSMessage();
			jmsMessage.setDeliverMode(session.getDeliverMode());
			jmsMessage.setAutoAcknowledge(session.getAutoAcknowledge());
			jmsMessage.setName(communicationWay.name());
			jmsMessage.setContent(JSONObject.toJSONString(message.getContext()));
			oos.writeObject(jmsMessage);
			oos.flush();
		} catch (Exception e) {
			throw new JMSException(402,e.getMessage());
		}
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
