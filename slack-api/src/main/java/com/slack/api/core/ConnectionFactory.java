package com.slack.api.core;

public interface ConnectionFactory {

	Connection createConnection(Integer listenerPort);

	Connection createConnection();

}
