package ca.polymtl.inf3405.lab1.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class UserManager extends HashMap<String, String> {

	private static final long serialVersionUID = -5489169356428721835L;
	private static final String USER_FILE_NAME = "users.txt";

	private static UserManager _instance;

	private File file;

	public UserManager(String userFileName) {
		super();
		file = new File(userFileName);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create user file. Exiting with code 0.");
				System.exit(0);
			}
	}

	private void readFile() {
		try {
			Scanner fileReader = new Scanner(file);
			while (fileReader.hasNextLine()) {
				String pair = fileReader.nextLine();
				String[] values = pair.split(";");
				this.put(values[0], values[1]);
			}
			fileReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static UserManager instance() {
		if (_instance == null) {
			_instance = new UserManager(USER_FILE_NAME);
			_instance.readFile();
		}

		return _instance;
	}

	public static boolean verifyAuthentication(String username, String password) {
		return instance().check(username, password);
	}

	private boolean check(String username, String password) {
		if (instance().containsKey(username)) {
			return instance().get(username).equals(password);
		} else {
			return createNewUser(username, password);
		}
	}

	private boolean createNewUser(String username, String password) {
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true));
			fileWriter.write(username + ";" + password + "\n");
			fileWriter.flush();
			fileWriter.close();
			
			this.put(username, password);
			
		} catch (IOException e) {
			System.err.println("Error while creating new user: " + username + ". Error: " + e);
			return false;
		}

		return true;
	}

}
