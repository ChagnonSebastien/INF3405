package ca.polymtl.inf3405.tp1.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import javax.imageio.ImageIO;

public class ClientHandler implements Runnable {

	private Socket socket;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {

		PrintStream stream;
		try {
			stream = new PrintStream(socket.getOutputStream());
			stream.println("Connecté!");
			System.out.println("Send handshake");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			BufferedImage image = ImageIO.read(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
