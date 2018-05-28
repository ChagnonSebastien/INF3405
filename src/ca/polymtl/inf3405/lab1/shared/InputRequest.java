package ca.polymtl.inf3405.lab1.shared;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputRequest {
	
	private final static String INVALID_IP_SYNTAX = "Ip must be 4 bytes separated by dots";
	private final static String INVALID_PORT_SYNTAX = "Port must be a number between 5000 and 5050";

	public static int readPortInput(Scanner reader) {
		try {
			System.out.println("Please enter a valid port number");
			int port = reader.nextInt();
			if (port < 5000 || port > 5050) {
				System.out.println(INVALID_PORT_SYNTAX);
				reader.nextLine();
				return readPortInput(reader);
			}

			reader.nextLine();
			System.out.println("Thank you, your port is " + port);
			return port;

		} catch (InputMismatchException ex) {
			System.out.println(INVALID_PORT_SYNTAX);
			reader.nextLine();
			return readPortInput(reader);
		}

	}

	public static String readIpInput(Scanner reader) {
		String ipv4;
		do {
			System.out.println("Please enter a valid IP adress for the server:");
			ipv4 = reader.nextLine();
		} while (!isValidIp(ipv4));

		System.out.println("Thank you, your ip is " + ipv4);
		return ipv4;
	}

	public static boolean isValidIp(String ipv4) {
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
