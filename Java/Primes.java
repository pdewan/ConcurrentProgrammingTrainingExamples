import java.lang.*;
import java.lang.Math;
import java.util.Random;

public class Primes {
    public static void main(String[] args) {
	
	int numItems = 0;
	if (args.length < 1) {
	    System.err.println("usage: Hello <number_of_items>");
	    System.exit(0);
	}
	try {
	    numItems = Integer.parseInt(args[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}
	
	Runnable runnable = new Counter(numItems);
	Thread[] threads = new Thread[4];
	for (int i = 0; i < 4; i++) {
	    threads[i] = new Thread(runnable);
	    threads[i].start();
	}
	int answer = 0;
	for (int i = 0; i < 4; i++) {
	    try {
		threads[i].join();
		answer += ((Counter) runnable).getAnswer(i);
	    } catch (Exception ex) {
		System.err.println("Error while waiting for thread " + i);
	    }
	}
	System.out.println("The number of primes is: " + answer);
    }
}


class Counter implements Runnable {
    public int[] intArray;
    private int nextId = 0;
    private int[] answers;

    Counter(int numItems) {
	intArray = new int[numItems];
        Random rand = new Random();
        for (int i = 0; i < numItems; i++) {
            intArray[i] = rand.nextInt(50);
        }
	answers = new int[] {0,0,0,0}; 
    }

    public void run() {
        int myId = 0;
        synchronized(this) {
            myId = nextId;
            nextId++;
        }
        int howBig = intArray.length / 4;
        int myStart = myId * howBig;
        int myEnd = myStart + howBig-1;
        if (myId == 3) {
            myEnd = intArray.length-1;
        }
	
        for (int i = myStart; i <= myEnd; i++) {
	    if (isPrime(intArray[i])) {
		answers[myId]++;
	    }
        }
    }

    boolean isPrime(int num) {
	if (num == 2) return true;

	for (int i = 2; i <= Math.sqrt(num); i++) {
	    if ((num % i) == 0) {
		return false;
	    }
	}

	return true;
    }

    int getAnswer(int idx) {
	return answers[idx];
    }

}

