package com.slack.api.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.slack.api.domain.CommunicationWay;
import com.slack.api.exception.JMSException;
import com.slack.api.handler.JMSConsumerHandler;
import com.slack.api.listener.MessageListener;

public class MessageConsumer {
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	private CommunicationWay communicationWay;

	private ServerSocket serverSocket;

	public MessageConsumer(CommunicationWay communicationWay, ServerSocket serverSocket) {
		super();
		this.communicationWay = communicationWay;
		this.serverSocket = serverSocket;
	}
	public void close() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setMessageListener(MessageListener<?> messageListener) {
		try {
			for(;;) {
				Socket socket = serverSocket.accept();
				executorService.execute(new JMSConsumerHandler(communicationWay,socket,messageListener));
			}
		} catch (IOException e) {
			throw new JMSException(410,e.getMessage());
		}
	}

}
