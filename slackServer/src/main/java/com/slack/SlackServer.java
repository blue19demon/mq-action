package com.slack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.slack.api.exception.JMSException;
import com.slack.api.server.DeliverMode;
import com.slack.api.server.JMSClientCommitBackMessage;
import com.slack.api.server.JMSClientConnMessage;
import com.slack.api.server.JMSMessage;
import com.slack.api.server.StartBanner;

public class SlackServer {
	private static Logger logger = Logger.getLogger(SlackServer.class);
	private static List<JMSClientConnMessage> jmsClientConnLists = new ArrayList<JMSClientConnMessage>();
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	private static Integer port = 61616;

	private StartBanner startBanner;
	public static void main(String[] args) {
		
		System.setProperty("conf", "conf.properties");
		String conf = System.getProperty("conf");
		Properties properties = new Properties();
		try {
			File confFile = new File(System.getProperty("user.dir") + File.separator + conf);
			properties.load(new FileInputStream(confFile));
			if (properties.containsKey("slack.port")) {
				port = Integer.valueOf(properties.get("slack.port").toString());
			}
			new SlackServer()/* .setStartBanner(null) */.start(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SlackServer setStartBanner(StartBanner startBanner) {
		this.startBanner = startBanner;
		return this;
	}

	private void start(int port) {
		if(startBanner!=null) {
			logger.info("\r\n" + 
					startBanner.banner()+
					" \r\n" + 
					"");
		}else {
			logger.info("\n    __  __      __\r\n" + 
					"   / / / /___ _/ /___\r\n" + 
					"  / /_/ / __ `/ / __ \\\r\n" + 
					" / __  / /_/ / / /_/ /\r\n" + 
					"/_/ /_/\\__,_/_/\\____/\r\n" + 
					" \r\n" + 
					"");
		}
		ServerSocket ss = null;
		try {
			InetAddress address = InetAddress.getLocalHost();
			ss = new ServerSocket(port);
			String serAdd="server started at tcp://%s:%s";
			logger.info(String.format(serAdd,address.getHostAddress(),port));
			while (true) {
				Socket socket = ss.accept();
				executorService.execute(new ServerHandler(socket, jmsClientConnLists));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class ServerHandler implements Runnable {
	private static Logger logger = Logger.getLogger(ServerHandler.class);
	private Socket socket;
	private List<JMSClientConnMessage> jmsClientConnLists;

	public ServerHandler(Socket socket, List<JMSClientConnMessage> jmsClientConnLists) {
		super();
		this.socket = socket;
		this.jmsClientConnLists = jmsClientConnLists;
	}

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			Object receiver = ois.readObject();
			if (receiver instanceof JMSClientConnMessage) {
				callbackJMSClientConnectionMessage((JMSClientConnMessage) receiver);
			} else if (receiver instanceof JMSMessage) {
				callbackJSMMessage((JMSMessage) receiver);
			} else if (receiver instanceof JMSClientCommitBackMessage) {
				clientCommitBackMessage((JMSClientCommitBackMessage) receiver);
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

	private void clientCommitBackMessage(JMSClientCommitBackMessage receiver) {
		logger.info("JMSClientCommitBackMessage=" + receiver);
	}

	private void callbackJMSClientConnectionMessage(JMSClientConnMessage receiver) {
		try {
			logger.info("ClientConnectionMessage=" + receiver);
			if (!jmsClientConnLists.contains(receiver)) {
				jmsClientConnLists.add(receiver);
			}
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject("regist OK");
			oos.flush();
			oos.close();
			logger.info("regist OK");
		} catch (IOException e) {
			throw new JMSException(409, e.getMessage());
		}
	}

	private void callbackJSMMessage(JMSMessage jmsMessage) throws IOException {
		logger.info("jmsMessage=" + jmsMessage);
		if(jmsClientConnLists.isEmpty()) {
			return;
		}
		if (jmsMessage.getDeliverMode() == DeliverMode.QUEUE) {
			// queue
			try {
				for (int i = 0; i < jmsClientConnLists.size(); i++) {
					ObjectOutputStream oos = null;
					//此处应该轮询，收到回应就不在发送
					JMSClientConnMessage client = jmsClientConnLists.get(i);
					socket = new Socket(client.getIp(), client.getPort());
					oos = new ObjectOutputStream(socket.getOutputStream());
					logger.info(client+"send start");
					oos.writeObject(jmsMessage);
					oos.flush();
					logger.info(client+"send end and OK");
					//监听
					ObjectInputStream ois = null;
					ois = new ObjectInputStream(socket.getInputStream());
					Object receiver = ois.readObject();
					if(receiver instanceof JMSClientCommitBackMessage) {
						logger.info("JMSClientCommitBackMessage=" + receiver);
						JMSClientCommitBackMessage commitBack = (JMSClientCommitBackMessage) receiver;
						if(commitBack.getMsgId().equals(jmsMessage.getMsgId())
								&&commitBack.getName().equals(jmsMessage.getName())) {
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			// topic
			if (jmsMessage.getDeliverMode() == DeliverMode.TOPIC) {
				for (JMSClientConnMessage client : jmsClientConnLists) {
					try {
						ObjectOutputStream oos = null;
						socket = new Socket(client.getIp(), client.getPort());
						oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(jmsMessage);
						oos.flush();
						logger.info("send OK");
					} catch (Exception e) {
						e.printStackTrace();
						continue;
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
		}
	}
}
