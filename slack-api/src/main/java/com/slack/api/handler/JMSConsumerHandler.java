package com.slack.api.handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.slack.api.domain.CommunicationWay;
import com.slack.api.domain.Message;
import com.slack.api.exception.JMSException;
import com.slack.api.listener.MessageListener;
import com.slack.api.server.JMSClientCommitBackMessage;
import com.slack.api.server.JMSMessage;

public class JMSConsumerHandler<T> implements Runnable {
	private static Logger logger = Logger.getLogger(JMSConsumerHandler.class);
	private CommunicationWay communicationWay;

	private Socket socket;

	private MessageListener<T> messageListener;

	public JMSConsumerHandler(CommunicationWay communicationWay, Socket socket, MessageListener<T> messageListener) {
		super();
		this.communicationWay = communicationWay;
		this.socket = socket;
		this.messageListener = messageListener;
	}

	@Override
	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			Object msg = ois.readObject();
			logger.info(JSONObject.toJSONString(msg));
			if (msg instanceof JMSMessage) {
				JMSMessage jmsMessage = (JMSMessage) msg;
				// 队列名称匹配
				if (communicationWay.name().equals(jmsMessage.getName())) {
					Message<T> message = new Message<T>();
					message.setContext(
							JSONObject.parseObject(String.valueOf(jmsMessage.getContent()), new TypeReference<T>() {
							}));
					message.setAutoAcknowledge(jmsMessage.getAutoAcknowledge());
					message.setDeliverMode(jmsMessage.getDeliverMode());
					message.setMsgId(jmsMessage.getMsgId());
					message.setTimestamp(jmsMessage.getTimestamp());
					messageListener.onMessage(message);
					ObjectOutputStream oos = null;
					oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(new JMSClientCommitBackMessage(jmsMessage.getMsgId(), jmsMessage.getName()));
					oos.flush();
				}
			} else {
				logger.info("client:" + JSONObject.toJSONString(msg));
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new JMSException(409, e.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
