import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

	// instance variables
	private Socket socket;
	private SharedState mySharedStateObject;
	private String myServerThreadName;

	// log
	private static BufferedWriter bw;

	// constructor, to setup socket, input output buffer and lock reference
	public Server(Socket _socket, SharedState SharedObject) {
		socket = _socket;
		mySharedStateObject = SharedObject;
	}

	// thread run method
	@Override
	public void run() {
		try {
			String input, tokens[];
			int balance;
			boolean result;

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			if ((input = in.readLine()) != null) {
				myServerThreadName = input.toUpperCase();
			}
			System.out.println(myServerThreadName + " initialising.");

			/*
			 * Commands Format:
			 * 
			 * Deposit: d amount Example: d 200
			 * 
			 * Withdraw: w amount Example: w 100
			 * 
			 * Transfer: t amount receiver Example: t 10 C
			 * 
			 * Balance Inquiry: Example: i
			 */

			while ((input = in.readLine()) != null) {
				log("Command: " + input);
				input = input.trim();
				tokens = input.split(" ");

				balance = -1;
				result = false;

				try {
					// Get a lock first
					mySharedStateObject.acquireLock();
					// Process operation
					switch (tokens[0].toUpperCase()) {
					case "D":
						result = mySharedStateObject.deposit(myServerThreadName, Integer.parseInt(tokens[1]));
						break;
					case "W":
						result = mySharedStateObject.withdraw(myServerThreadName, Integer.parseInt(tokens[1]));
						break;
					case "T":
						result = mySharedStateObject.transfer(myServerThreadName, Integer.parseInt(tokens[1]),
								tokens[2]);
						break;
					case "I":
						balance = mySharedStateObject.getBalance(myServerThreadName);
						break;
					}
					mySharedStateObject.releaseLock();
				} catch (InterruptedException e) {
					System.err.println("Failed to get lock when reading:" + e);
				}
				// return result:
				if (result) {
					out.println("ok");
				} else if (balance != -1) {
					out.println(String.format("%d", balance));
				} else {
					out.println("failed");
				}
			}
			out.close();
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// server main
	public static void main(String[] args) throws IOException {
		ServerSocket ServerSocket = null;
		String ServerName = "WB Bank Server";
		int ServerNumber = 4545;

		try {
			bw = new BufferedWriter(new FileWriter(new File("log_server.txt")));
		} catch (Exception e) {
			System.err.println("Couldn't open log file for writing");
		}
		// Create the shared object in the global scope...
		SharedState ourSharedStateObject = new SharedState(bw);

		// Start server: create socket for listening
		try {
			ServerSocket = new ServerSocket(ServerNumber);
			System.out.println(ServerName + " started");
		} catch (IOException e) {
			System.err.println("Could not start " + ServerName + " specified port.");
			System.exit(-1);
		}

		// Got to do this in the correct order with only clients
		new Server(ServerSocket.accept(), ourSharedStateObject).start();
		new Server(ServerSocket.accept(), ourSharedStateObject).start();
		new Server(ServerSocket.accept(), ourSharedStateObject).start();

		ServerSocket.close();
	}

	// log
	private void log(String str) {
		try {
			bw.write(str);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}