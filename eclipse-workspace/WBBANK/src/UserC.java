import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

public class UserC {
	private static final int trialCount = 100;

	static void runAutomatedTransactions(Client clientC) {
		// this method will do random transactions
		// keep transaction record locally
		// and at the end, check with server if end amount is correct
		Random rand = new Random(System.currentTimeMillis()); // seeded random

		// run random trials
		for (int i = 0; i < trialCount; i++) {
			switch (rand.nextInt() % 3) {
			case 0:
				// add money:
				clientC.Add_money(rand.nextInt() % 10 + 10);
				break;
			case 1:
				// withdraw money:
				clientC.Subtract_money(rand.nextInt() % 10 + 10);
				break;
			case 2:
				// transfer money:
				clientC.Transfer_money(rand.nextInt() % 10 + 10, rand.nextBoolean() ? "A" : "B");
				break;
			}

		}
	}

	static void Testing(Client clientC) {
		// this method will test the three operations of adding, subtracting and
		// transferring money
		// checks the account balance
		// keep a log in a file locally
		clientC.Balance();
	}

	public static void main(String[] args) {
		String serverAddr = "localhost";
		int serverPort = 4545;

		Client clientC = new Client(serverAddr, serverPort, "C");
		try {
			clientC.initialize();
		} catch (UnknownHostException e) {
			System.err.println("\"" + serverAddr + "\" not available");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for port " + serverPort);
			System.exit(1);
		}

		// Run transactions:
		runAutomatedTransactions(clientC);
		// Run Testing:
		Testing(clientC);

		// Tidy up - not really needed due to true condition in while loop
		try {
			clientC.closeUp();
		} catch (IOException e) {
			System.err.println("Couldn't close socket");
			e.printStackTrace();
		}
	}

}
