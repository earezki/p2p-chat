package com.chat.p2pchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPChat {

	private static final int PORT = 5789;

	private Logger logger = new Logger();

	public TCPChat(String peerIpAddress) throws Exception {
		connect(peerIpAddress);
	}

	public TCPChat() throws Exception {
		listen();
	}

	private void connect(String peerIpAddress) {
		Socket peerSocket;
		try {
			peerSocket = new Socket(peerIpAddress, PORT);
		} catch (Exception e) {
			logger.log("Error: Connection cannot be established");
			return;
		}
		new Chat(logger).startChat(peerSocket);
	}

	private void listen() throws Exception {
		logger.log("Wating for other peer program...");
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			logger.log("Error: port in use");
			return;
		}
		Socket peerSocket = serverSocket.accept();
		new Chat(logger).startChat(peerSocket);
		serverSocket.close();
	}

}
