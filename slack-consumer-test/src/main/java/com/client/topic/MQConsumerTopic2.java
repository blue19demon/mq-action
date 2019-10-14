package com.client.topic;

import java.io.IOException;

import com.slack.api.core.Connection;
import com.slack.api.core.ConnectionFactory;
import com.slack.api.core.MessageConsumer;
import com.slack.api.core.Session;
import com.slack.api.core.SocketMQConnectionFactory;
import com.slack.api.domain.Message;
import com.slack.api.domain.Topic;
import com.slack.api.listener.MessageListener;

public class MQConsumerTopic2 {
	public static void main(String[] args) throws IOException {
        ConnectionFactory connectionFactory = new SocketMQConnectionFactory("tcp://192.168.31.128:61616");
        Connection connection = connectionFactory.createConnection(7766);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("test-topic");
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new MessageListener<String>() {
            @Override
            public void onMessage(Message<String> message) {
            	System.out.println("收到消息:"+ message.getContext());
            }
        });
     	System.in.read();
        consumer.close();
        session.close();
        connection.close();
	}
}
