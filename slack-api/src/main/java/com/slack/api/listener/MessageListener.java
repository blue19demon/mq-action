package com.slack.api.listener;

import com.slack.api.domain.Message;
import com.slack.api.exception.JMSException;

public interface MessageListener<T> {

	void onMessage(Message<T> message) throws JMSException;

}
