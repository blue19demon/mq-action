package com.producter.queue;

import java.util.Arrays;

import com.slack.api.core.Connection;
import com.slack.api.core.ConnectionFactory;
import com.slack.api.core.MessageProducer;
import com.slack.api.core.Session;
import com.slack.api.core.SocketMQConnectionFactory;
import com.slack.api.domain.Message;
import com.slack.api.domain.Queue;
import com.test.api.User;
import com.test.api.User.Pac;

public class MQProducerQueue {

	public static void main(String[] args) {
        ConnectionFactory connectionFactory = new SocketMQConnectionFactory("tcp://169.254.253.85:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("test-queue");
        MessageProducer producer = session.createProducer(queue);
        for (int i = 1; i <=100; i++) {
        	Message<?> textMessage = session.createMessage(new User("张三",i,Arrays.asList(new Pac("哈哈",Arrays.asList(1,2,3)))));
            producer.send(textMessage);
		}
        producer.close();
        session.close();
        connection.close();
	}
}
