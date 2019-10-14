package com.slack.api.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SocketMQConnectionFactory implements ConnectionFactory {

	private String tcpUrl;
	
	public SocketMQConnectionFactory(String tcpUrl) {
		super();
		this.tcpUrl = tcpUrl;
	}

	@Override
	public Connection createConnection(Integer listenerPort) {
		ServerAddress serverAddress = parse();
		return new Connection(serverAddress,listenerPort);
	}

	@Override
	public Connection createConnection() {
		ServerAddress serverAddress = parse();
		return new Connection(serverAddress);
	}

	public ServerAddress parse() {
		String reg = "((\\d+\\.){3}\\d+)\\:(\\d+)";
		tcpUrl=tcpUrl.split("//")[1];
		String ip = tcpUrl.replaceAll(reg, "$1");
		String port = tcpUrl.replaceAll(reg, "$3");
		return new ServerAddress(ip, Integer.parseInt(port));
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ServerAddress{
	private String ip;
	private Integer port;
}