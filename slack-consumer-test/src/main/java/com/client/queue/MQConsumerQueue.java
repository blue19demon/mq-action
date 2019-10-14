package com.client.queue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.slack.api.core.Connection;
import com.slack.api.core.ConnectionFactory;
import com.slack.api.core.MessageConsumer;
import com.slack.api.core.Session;
import com.slack.api.core.SocketMQConnectionFactory;
import com.slack.api.domain.Message;
import com.slack.api.domain.Queue;
import com.slack.api.listener.MessageListener;
import com.test.api.User;

public class MQConsumerQueue {
	public static void main(String[] args) throws IOException {
        ConnectionFactory connectionFactory = new SocketMQConnectionFactory("tcp://169.254.253.85:61616");
        Connection connection = connectionFactory.createConnection(7777);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("test-queue");
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(new MessageListener<User>() {
            @Override
            public void onMessage(Message<User> message) {
            	SimpleDateFormat smf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            	System.out.println(smf.format(new Date())+"->收到消息:"+JSONObject.toJSONString(message.getContext()));
            }
        });
      	System.in.read();
        consumer.close();
        session.close();
        connection.close();
	}
}
