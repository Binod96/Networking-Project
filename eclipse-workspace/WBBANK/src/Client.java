import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private final int serverPort;
	private final String serverAddress;

	// Set up the socket, in and out variables
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private boolean connected;
	private String id;

	// log: writer
	private BufferedWriter bw;

	// constructor: sets defaults
	public Client(String address, int port, String clientName) {
		serverAddress = address;
		serverPort = port;
		// set all to null
		clientSocket = null;
		in = null;
		out = null;
		connected = false;
		id = clientName;
		try {
			bw = new BufferedWriter(new FileWriter(new File("log_" + id + ".txt")));
		} catch (Exception e) {
			System.err.println("Couldn't open log file for writing");
		}
	}

	// initialize socket
	public void initialize() throws UnknownHostException, IOException {

		clientSocket = new Socket(serverAddress, serverPort);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		System.out.println("Initialised " + id + " client and IO connections");
		out.println(id.toUpperCase());
	}

	// close socket and streams
	public void closeUp() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		bw.close();
	}

	// check if value osf connected
	public boolean isConnected() {
		return connected;
	}

	// Transaction options:
	// inserting money
	public boolean Add_money(int amount) {
		log("Add " + amount);
		String command = String.format("d %d", amount);
		send(command);
		try {
			command = receive();
			if (command.equalsIgnoreCase("ok")) {
				System.out.println("Client " + id + " successfuly added " + amount + " to its account");
				log("Add success");
				return true;
			} else {
				System.out.println("Client " + id + " failed to add" + amount + " to its account");
				log("Add failed");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Client " + id + " got exception while adding money to its account");
			e.printStackTrace();
			log("Add failed exception");
			return false;
		}
	}

	// subtracting money
	public boolean Subtract_money(int amount) {
		log("Subtract " + amount);
		String command = String.format("w %d", amount);
		send(command);
		try {
			command = receive();
			if (command.equalsIgnoreCase("ok")) {
				System.out.println("Client " + id + " successfuly subtracted " + amount + " from its account");
				log("Subtract success");
				return true;
			} else {
				System.out.println("Client " + id + " failed to subtract " + amount + " from its account");
				log("Subtract failed");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Client " + id + " got exception while subtracting money to its account");
			e.printStackTrace();
			log("Subtract failed exception");
			return false;
		}
	}

	// transfer money
	public boolean Transfer_money(int amount, String to) {
		log("Transfer " + amount + " to " + to);
		String command = String.format("t %d %s", amount, to);
		send(command);
		try {
			command = receive();
			if (command.equalsIgnoreCase("ok")) {
				System.out.println(
						"Client " + id + " successfuly transferred " + amount + " to Client " + to + "'s account");
				log("Transfer success");
				return true;
			} else {
				System.out
						.println("Client " + id + " failed to transfer " + amount + " to Client " + to + "'s account");
				log("Transfer failed");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Client " + id + " got exception while transferring money to " + to + "'s account");
			e.printStackTrace();
			log("Transfer failed exception");
			return false;
		}
	}

	// inquire balance
	public int Balance() {
		log("Inquire balance");
		String command = String.format("i");
		send(command);
		try {
			command = receive();
			if (Integer.parseInt(command) == -1) {
				log("Inquiry failed");
				return -1;
			}
			log("Inquiry success, balance: " + command);
			return Integer.parseInt(command);
		} catch (Exception e) {
			log("Inquiry failed exception");
			return -1;
		}
	}

	// Underlying communication
	private String receive() throws IOException {
		String fromServer = in.readLine();
		fromServer = fromServer.trim();
		return fromServer;
	}

	// sending from
	private void send(String fromUser) {
		if (fromUser != null)
			if (fromUser.length() > 0)
				out.println(fromUser);
	}

	private void log(String str) {
		try {
			bw.write(str);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}