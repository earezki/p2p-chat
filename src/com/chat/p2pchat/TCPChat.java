package com.chat.p2pchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPChat {

	private static final int PORT = 5789;

	private Logger logger = new Logger();

	public TCPChat(String peerIpAddress) throws Exception {
		connect(peerIpAddress);
	}

	public TCPChat() throws Exception {
		listen();
	}

	private void connect(String peerIpAddress) throws UnknownHostException, IOException {
		logger.log("Connecting to peer!");
		Socket peerSocket = new Socket(peerIpAddress, PORT);
		new Chat(logger).startChat(peerSocket);
	}

	private void listen() throws Exception {
		logger.log("Wating for a peer!");
		ServerSocket serverSocket = new ServerSocket(PORT);
		Socket peerSocket = serverSocket.accept();
		new Chat(logger).startChat(peerSocket);
		serverSocket.close();
	}

}
