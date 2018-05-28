package ca.polymtl.inf3405.lab1.client;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ca.polymtl.inf3405.lab1.shared.InputRequest;

public class Client {

	private static String ipv4;
	private static int port;

	public static void main(String[] args) {
		System.out.println("Welcome to the Sobel filter applier - Client Side");

		Scanner reader = new Scanner(System.in);

		ipv4 = InputRequest.readIpInput(reader);
		port = InputRequest.readPortInput(reader);

		try (
			Socket socket = new Socket(Inet4Address.getByName(ipv4), port);
			OutputStream outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();
			Scanner socketReader = new Scanner(inputStream);
			PrintStream socketWriter = new PrintStream(outputStream);
		) {

			String handshake = socketReader.nextLine();
			if (handshake.equals("Handshake"))
				System.out.printf("[%s:%d – %s] Successful connection to server.\n", socket.getInetAddress(),
						socket.getPort(), LocalDateTime.now());

			authenticate(reader, socketReader, socketWriter, socket);

			File file = enterFile(reader, socket);
			String[] imageNameSplit = file.getName().split("\\.");
			
			System.out.println("Please enter the new name for your image:");
			String newImageName = reader.nextLine();
			String[] newImageNameSplit = file.getName().split("\\.");

			BufferedImage image = ImageIO.read(file);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(image, imageNameSplit[imageNameSplit.length - 1], byteArrayOutputStream);

			socketWriter.println(file.getName());

			byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

			outputStream.write(size);
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();
			System.out.printf("[%s:%d - %s] Your selected image has been sent!\n", socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now());

			byte[] sizeAr = new byte[4];
			socket.getInputStream().read(sizeAr);
			int newImageSize = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

			byte[] imageAr = new byte[newImageSize];
			socket.getInputStream().read(imageAr);

			System.out.printf("[%s:%d - %s] Your filtered image has been received\n", socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now());
			BufferedImage newImage = ImageIO.read(new ByteArrayInputStream(imageAr));

			File outputfile = new File(newImageName);
			ImageIO.write(newImage, newImageNameSplit[newImageNameSplit.length - 1], outputfile);

			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(new JLabel(new ImageIcon(newImage)));
			frame.pack();
			frame.setVisible(true);

		} catch (IOException e) {
			System.out.printf("Error while communicating to the server: \"%s\"\n", e.getMessage());
			e.printStackTrace();
			System.out.println("Exiting program.");
			System.exit(0);
		}

	}

	private static File enterFile(Scanner keyboard, Socket socket) {
		System.out.println("Enter image file name:");
		File file = new File(keyboard.nextLine());
		
		if (!file.exists()) {
			System.out.printf("[%s:%d - %s] Inavalid file.\n", socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now());
			file = enterFile(keyboard, socket);
		}
		
		return file;
	}

	private static void authenticate(Scanner keyboard, Scanner socketReader, PrintStream socketWriter, Socket socket) {

		System.out.println("Please, enter your username:");
		String user = keyboard.nextLine();
		System.out.println("Enter your password:");
		String password = keyboard.nextLine();

		socketWriter.println(user);
		socketWriter.println(password);

		if (socketReader.nextLine().equals("BAD CREDENTIALS")) {
			System.out.printf("[%s:%d - %s] Invalid credentials. Please try again.\n", socket.getInetAddress(),
					socket.getPort(), LocalDateTime.now());
			authenticate(keyboard, socketReader, socketWriter, socket);
		} else {
			System.out.printf("[%s:%d - %s] You are now connected as %s\n", socket.getInetAddress(), socket.getPort(),
					LocalDateTime.now(), user);
		}

	}

}
