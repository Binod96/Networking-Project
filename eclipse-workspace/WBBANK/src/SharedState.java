import java.io.BufferedWriter;
import java.util.Arrays;

public class SharedState {

	private static int accounts[];
	private boolean accessing = false; // true a thread has a lock, false otherwise
	private int threadsWaiting = 0; // number of waiting writers
	private BufferedWriter bw;

	// Constructor
	public SharedState(BufferedWriter setbw) {
		// Create three accounts:
		accounts = new int[3];
		Arrays.fill(accounts, 1000);
		bw = setbw;
	}

	// Check if lock is available, acquire lock:
	public synchronized void acquireLock() throws InterruptedException {
		// get a ref to the current thread
		Thread me = Thread.currentThread();
		log("Thread " + me.getName() + " is requiring to acquire lock");
		System.out.println(me.getName() + " is attempting to acquire a lock!");
		++threadsWaiting;
		while (accessing) { // while someone else is accessing or threadsWaiting > 0
			System.out.println(me.getName() + " waiting to get a lock as someone else is accessing...");
			// wait for the lock to be released - see releaseLock() below
			wait();
		}
		// nobody has got a lock so get one
		--threadsWaiting;
		accessing = true;
		System.out.println(me.getName() + " got a lock!");
	}

	// Releases a lock to when a thread is finished
	public synchronized void releaseLock() {
		// release the lock and tell everyone
		log("Thread  is requiring to acquire lock");
		accessing = false;
		notifyAll();
		Thread me = Thread.currentThread(); // get a ref to the current thread
		System.out.println(me.getName() + " a lock!");

	}

	// add amount to account titled myThreadName
	public synchronized boolean deposit(String myThreadName, int amount) {
		System.out.println(myThreadName + " is depositing " + amount);
		log("Deposit by " + myThreadName);
		if (amount <= 0) {
			System.out.println(myThreadName + " is depositing invalid amount");
			return false;
		}
		// Check what the client said
		switch (myThreadName) {
		case "A":
			accounts[0] += amount;
			break;
		case "B":
			accounts[1] += amount;
			break;
		case "C":
			accounts[2] += amount;
			break;
		}
		return true;
	}

	// subtract amount from account titled myThreadName
	public synchronized boolean withdraw(String myThreadName, int amount) {
		System.out.println(myThreadName + " is withdrawing " + amount);
		log("Withdraw by " + myThreadName);
		// Check what the client said
		switch (myThreadName) {
		case "A":
			accounts[0] -= amount;
			break;
		case "B":
			accounts[1] -= amount;
			break;
		case "C":
			accounts[2] -= amount;
			break;
		}
		return true;
	}

	// transfer amount from myThreadName to receiverName
	public synchronized boolean transfer(String myThreadName, int amount, String receiverName) {
		System.out.println(myThreadName + " is transferring " + amount + " to " + receiverName);
		// Check what the client said
		log("Transfer from " + myThreadName + " to " + receiverName);
		int senderIndex, receiverIndex;

		switch (myThreadName) {
		case "A":
			senderIndex = 0;
			break;
		case "B":
			senderIndex = 1;
			break;
		case "C":
			senderIndex = 2;
			break;
		default:
			return false;
		}

		switch (receiverName) {
		case "A":
			receiverIndex = 0;
			break;
		case "B":
			receiverIndex = 1;
			break;
		case "C":
			receiverIndex = 2;
			break;
		default:
			return false;
		}

		if (senderIndex == receiverIndex) {
			System.out.println(myThreadName + " is transferring money to itself");
			return false;
		}

		accounts[senderIndex] -= amount;
		accounts[receiverIndex] += amount;
		return true;
	}

	// get balance
	public synchronized int getBalance(String myThreadName) {
		System.out.println(myThreadName + " is inquiring balance");
		log("Balance inquiry by " + myThreadName);
		// Check what the client said
		switch (myThreadName) {
		case "A":
			return accounts[0];
		case "B":
			return accounts[1];
		case "C":
			return accounts[2];
		}
		return -1;
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