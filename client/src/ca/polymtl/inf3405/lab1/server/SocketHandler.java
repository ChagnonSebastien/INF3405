package ca.polymtl.inf3405.lab1.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SocketHandler implements Runnable {

	private Socket socket;

	public SocketHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		try (InputStream inputStream = socket.getInputStream();
				Scanner scanner = new Scanner(inputStream);
				OutputStream outputStream = socket.getOutputStream();
				PrintStream writer = new PrintStream(socket.getOutputStream());) {
			writer.println("Handshake");
			System.out.printf("[%s:%d - %s] Sending handshake.\n", socket.getInetAddress(), socket.getPort(),
					LocalDateTime.now());

			String userName = authenticate(scanner, writer);
			String fileName = scanner.nextLine();

			System.out.printf("[%s - %s:%d - %s] : Waiting for %s reception.\n", userName, socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now(), fileName);

			byte[] sizeAr = new byte[4];
			inputStream.read(sizeAr);
			int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

			byte[] imageAr = new byte[size];
			inputStream.read(imageAr);

			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
			System.out.printf("[%s - %s:%d - %s] : Initiating treatment of %s\n", userName, socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now(), fileName);

			BufferedImage processedImage = Sobel.process(image);
			System.out.printf("[%s - %s:%d - %s] : Treatement completed. Sending new image.\n", userName,
					socket.getInetAddress(), socket.getPort(), LocalDateTime.now(), fileName);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(processedImage, "jpg", byteArrayOutputStream);

			byte[] newsize = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
			outputStream.write(newsize);
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	private String authenticate(Scanner scanner, PrintStream writer) {
		String user;
		boolean authenticationValid = false;
		do {
			user = scanner.nextLine();
			String password = scanner.nextLine();
			authenticationValid = UserManager.verifyAuthentication(user, password);
			writer.println(authenticationValid ? "GOOD CREDENTIALS" : "BAD CREDENTIALS");
		} while (!authenticationValid);
		System.out.printf("[%s - %s:%d - %s] : Authentification successfull.\n", user, socket.getInetAddress(),
				socket.getPort(), LocalDateTime.now());
		return user;
	}

}
