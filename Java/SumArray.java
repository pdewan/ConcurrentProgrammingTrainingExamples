import java.lang.*;
import java.util.Random;

public class SumArray {
    public static void main(String[] args) {
	int numItems = 0;
	if (args.length < 1) {
	    System.err.println("usage: SumArray <number_of_items>");
	    System.exit(0);
	}
	try {
	    numItems = Integer.parseInt(args[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}

	Runnable runnable = new Printer(numItems);
	Thread[] threads = new Thread[4];
	for (int i = 0; i < 4; i++) {
	    threads[i] = new Thread(runnable);
	    threads[i].start();
	}
	int sum = 0;
	for (int i = 0; i < 4; i++) {
	    try {
		threads[i].join();	
	    } catch (Exception ex) {
		System.err.println("Error while waiting for thread " + i);
	    }
	}
	for (int i : ((Printer) runnable).sums) {
	    sum += i;
	}
	System.out.println("Total: " + sum);
    }
}

class Printer implements Runnable {
    public int[] intArray;
    private int nextId = 0;
    public int[] sums;
    
    Printer(int numItems) {
	intArray = new int[numItems];
	Random rand = new Random();
	for (int i = 0; i < numItems; i++) {
	    intArray[i] = rand.nextInt(50);
	}
	sums = new int[4];
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

	sums[myId] = 0;
	for (int i = myStart; i <= myEnd; i++) {
	    synchronized(this) {
		System.out.println("Thread " + myId + " index " + i + " in (" + myStart + ".." +
				   myEnd + "): " + intArray[i]);
		sums[myId] += intArray[i];
	    }
	}
    }
}
