package com.chat;

import com.chat.p2pchat.TCPChat;

public class Application {

	public static void main(String[] args) throws Exception {
		if (args != null && args.length > 0) {
			String peerIpAddress = args[0];
			new TCPChat(peerIpAddress);
		} else {
			new TCPChat();
		}
	}
}
