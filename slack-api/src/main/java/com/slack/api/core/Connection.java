package com.slack.api.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.slack.api.exception.JMSException;
import com.slack.api.server.AcknowledgeMode;
import com.slack.api.server.JMSClientConnMessage;

public class Connection {
	private static Logger logger= Logger.getLogger(Connection.class);
	private ServerAddress serverAddress;
	
	private Integer listenerPort;
	
	private ServerSocket serverSocket;
	
	
	public Connection(ServerAddress serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}

	public Connection(ServerAddress serverAddress, Integer listenerPort) {
		super();
		this.serverAddress = serverAddress;
		this.listenerPort = listenerPort;
	}

	public Object start() {
		if(listenerPort!=null) {
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			Socket socket=null;
			try {
				socket=new Socket(serverAddress.getIp(), serverAddress.getPort());
				oos = new ObjectOutputStream(socket.getOutputStream());
				JMSClientConnMessage jmClientConnMessage = new JMSClientConnMessage();
				jmClientConnMessage.setIp(getHostAddress());
				jmClientConnMessage.setPort(listenerPort);
				oos.writeObject(jmClientConnMessage);
				oos.flush();
				ois = new ObjectInputStream(socket.getInputStream());
				Object receive = ois.readObject();
				return receive;
			} catch (Exception e) {
				throw new JMSException(402,e.getMessage());
			}finally {
				if(socket!=null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	private String getHostAddress() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			String hostAddress = address.getHostAddress();
			return hostAddress;
		} catch (UnknownHostException e) {
			throw new JMSException(401,e.getMessage());
		}
	}

	public Session createSession(boolean isPersistent, AcknowledgeMode autoAcknowledge) {
		//生产者
		if(listenerPort==null) {
			try {
				return new Session(serverAddress,isPersistent,autoAcknowledge);
			} catch (Exception e) {
				e.printStackTrace();
				throw new JMSException(402,"创建Session错误");
			}
		}else {
			try {
				serverSocket = new ServerSocket(listenerPort);
				logger.info("started at "+listenerPort);
				return new Session(serverAddress,serverSocket,isPersistent,autoAcknowledge);
			} catch (Exception e) {
				e.printStackTrace();
				throw new JMSException(402,"创建Session错误");
			}
		}
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

}
