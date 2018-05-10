package ca.polymtl.inf3405.tp1.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

	private final static String INVALID_IP_SYNTAX = "Ip must be 4 bytes separated by dots";

	private static String ipv4;
	private static int port;

	public static void main(String[] args) {
		System.out.println("Welcome to the Sobel filter applier - Server Side");

		Scanner reader = new Scanner(System.in);

		do {
			System.out.println("Please enter a valid IP adress for the server:");
			ipv4 = reader.nextLine();
		} while (!isValidIp(ipv4));

		System.out.println("Thank you, your ip is " + ipv4);

		port = readPortInput(reader);
		
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port, Integer.MAX_VALUE, Inet4Address.getByName(ipv4));
			System.out.println("Listening on " + ipv4 + ":" + port);
			
			while (true) {
				Socket newClient = listener.accept();
				System.out.println("new connection from " + newClient.getPort());
				
				ClientHandler connection = new ClientHandler(newClient);
				connection.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static int readPortInput(Scanner reader) {
		try {
			port = reader.nextInt();
			if (port < 5000 || port > 5050) {
				System.out.println("Port must be a number between 5000 and 5050");
				return readPortInput(reader);
			}
			
			return port;
			
		} catch (InputMismatchException ex) {
			System.out.println("Port must be a number between 5000 and 5050");
			reader.next();
			return readPortInput(reader);
		}
		
	}

	private static boolean isValidIp(String ipv4) {
		String[] bytes = ipv4.split("\\.");
		if (bytes.length != 4) {
			System.out.println(INVALID_IP_SYNTAX);
			return false;
		}

		for (String b : bytes) {
			try {
				int number = Integer.parseInt(b);
				if (number > 255 || number < 0)
					throw new Exception("Invalid Argument");

			} catch (Exception ex) {
				System.out.println(INVALID_IP_SYNTAX);
				return false;
			}
		}

		return true;
	}

}
