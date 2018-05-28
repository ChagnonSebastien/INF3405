package ca.polymtl.inf3405.lab1.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

import ca.polymtl.inf3405.lab1.shared.InputRequest;

public class Server {

	private static String ipv4;
	private static int port;

	public static void main(String[] args) {
		System.out.println("Welcome to the Sobel filter applier - Server Side");

		Scanner reader = new Scanner(System.in);
		ipv4 = InputRequest.readIpInput(reader);
		port = InputRequest.readPortInput(reader);
		
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(port, 20, Inet4Address.getByName(ipv4));
			System.out.println("Listening on " + ipv4 + ":" + port);		
			
			while (true) {
				Socket newClient = listener.accept();
				System.out.printf("[%s:%d – %s] La nouvelle connection est établie.\n", newClient.getInetAddress(), newClient.getPort(), LocalDateTime.now());
				SocketHandler connection = new SocketHandler(newClient);
				new Thread(connection).start();
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

}
