package com.producter.topic;

import com.slack.api.core.Connection;
import com.slack.api.core.ConnectionFactory;
import com.slack.api.core.MessageProducer;
import com.slack.api.core.Session;
import com.slack.api.core.SocketMQConnectionFactory;
import com.slack.api.domain.Message;
import com.slack.api.domain.Topic;
public class MQProducerTopic {

	public static void main(String[] args) {
        ConnectionFactory connectionFactory = new SocketMQConnectionFactory("tcp://192.168.31.128:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	Topic topic = session.createTopic("test-topic");
        MessageProducer producer = session.createProducer(topic);
        Message<?> textMessage = session.createMessage("我是topic");
        producer.send(textMessage);
        producer.close();
        session.close();
        connection.close();
	}
}
