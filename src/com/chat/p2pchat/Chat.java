package com.chat.p2pchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Chat {
	private boolean running;
	private Logger logger;

	public Chat(Logger logger) {
		this.logger = logger;
	}

	public void startChat(Socket peerSocket) {
		logger.log("Connection established");
		running = true;
		waitForUserInputs(peerSocket);
		listenToPeerOutputs(peerSocket);
	}

	private void listenToPeerOutputs(Socket peerSocket) {
		DataInputStream input;
		try {
			input = new DataInputStream(peerSocket.getInputStream());
		} catch (IOException e1) {
			throw new RuntimeException();
		}
		while (running) {
			try {
				logger.log(input.readUTF());
			} catch (IOException e) {
				running = false;
			}
		}
	}

	private void waitForUserInputs(Socket peerSocket) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				try {
					DataOutputStream output = new DataOutputStream(peerSocket.getOutputStream());
					logger.log("Waiting for user text");
					while (running) {
						String userInput = scanner.nextLine();

						UserCommand command = parseUserCommand(userInput);
						if (command == UserCommand.SEND_MESSAGE) {
							sendTextMessage(output, userInput);
						} else {
							String filename = parseFilename(userInput);
							String filelocation = parseFileLocation(userInput);
							sendFile(output, filename, filelocation);
						}
						output.flush();
					}
				} catch (IOException e) {
					running = false;
				}

				scanner.close();
			}
		}).start();
	}

	private void sendTextMessage(DataOutputStream output, String message) throws IOException {
		output.writeUTF(message);
	}

	private void sendFile(DataOutputStream output, String filename, String filelocation) throws IOException {
		String fileContent;
		try {
			fileContent = readFile(filename, filelocation);
		} catch (IOException e) {
			logger.log("file does not exist");
			return;
		}
		sendTextMessage(output, fileContent);
	}

	private String readFile(String filelocation, String filename) throws IOException {
		byte[] readAllBytes = Files.readAllBytes(Paths.get(filelocation, filename));
		return new String(readAllBytes);
	}

	private String parseFileLocation(String input) {
		String filelocation = input.split(" ")[2];
		return filelocation;
	}

	private String parseFilename(String input) {
		String filename = input.split(" ")[1];
		return filename;
	}

	private UserCommand parseUserCommand(String input) {
		if (input.startsWith("sendfile")) {
			return UserCommand.SEND_FILE;
		}
		return UserCommand.SEND_MESSAGE;
	}
}