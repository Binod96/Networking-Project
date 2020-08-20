import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

public class UserA {
	private static final int trialCount = 100;

	static void runAutomatedTransactions(Client clientA) {
		// this method will do random transactions
		// keep transaction record locally
		// and at the end, check with server if end amount is correct
		Random rand = new Random(System.currentTimeMillis()); // seeded random

		// run random trials
		for (int i = 0; i < trialCount; i++) {
			switch (rand.nextInt() % 3) {
			case 0:
				// add money:
				clientA.Add_money(rand.nextInt() % 10 + 10);
				break;
			case 1:
				// withdraw money:
				clientA.Subtract_money(rand.nextInt() % 10 + 10);
				break;
			case 2:
				// transfer money:
				clientA.Transfer_money(rand.nextInt() % 10 + 10, rand.nextBoolean() ? "B" : "C");
				break;

			}
		}
	}

	static void Testing(Client clientA) {
		// this method will test the three operations of adding, subtracting and
		// transferring money
		// checks the account balance
		// keep a log in a file locally
		clientA.Balance();
	}

	public static void main(String[] args) {
		String serverAddr = "localhost";
		int serverPort = 4545;

		Client clientA = new Client(serverAddr, serverPort, "A");
		try {
			clientA.initialize();
		} catch (UnknownHostException e) {
			System.err.println("\"" + serverAddr + "\" not available");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for port " + serverPort);
			System.exit(1);
		}

		// Run transactions:

		runAutomatedTransactions(clientA);

		// Run Testing: Creates a log file locally
		Testing(clientA);

		// Tidy up - not really needed due to true condition in while loop
		try {
			clientA.closeUp();
		} catch (IOException e) {
			System.err.println("Couldn't close socket");
			e.printStackTrace();
		}
	}

}
