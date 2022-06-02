import java.lang.*;
import java.util.concurrent.CyclicBarrier;


public class HelloBarrier {
    public static void main(String[] args) {
	int numThreads = 0;

	// Number of threads to be executed shoul be passed on the command line

	//  If nothing was passed on the command line, then print error and exit
	if (args.length < 1) {
	    System.err.println("usage: Hello <number_of_threads>");
	    System.exit(0);
	}
	// Convert the command line string to an integer, exit if error
	try {
	    numThreads = Integer.parseInt(args[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}

	CyclicBarrier b = new CyclicBarrier(numThreads+1);
	// Spawn the number of threads passed on the command line
	Thread[] threads = new Thread[numThreads];
	for (int i = 0; i < numThreads; i++) {
	    threads[i] = new Thread(new HelloHelper(i, b));
	    threads[i].start();
	}

	// time start
	try { b.await(); } catch (Exception ex) {} 
	System.out.println("Main waiting...");
	try { b.await(); } catch (Exception ex) {}
	// time end
	System.out.println("Main done");

	// Wait for the threads to finish
	for (int i = 0; i < numThreads; i++) {
	    try {
		threads[i].join();
	    } catch (Exception ex) {
		System.err.println("Error while waiting for thread " + i);
	    }
	}
    }
}
class HelloHelper implements Runnable {
    public int id;
    CyclicBarrier barrier;

    // Constructor to set the id for this thread
    HelloHelper(int iden, CyclicBarrier bar) {
	id = iden;
	barrier = bar;
    }

    // This method is invoked when the thread starts.  It will print a friendly message.
    public void run() {
	try { barrier.await(); } catch (Exception ex) {}
	synchronized(this) {
	    System.out.println("Hello World from thread " + id);
	}
	try { barrier.await(); } catch (Exception ex) {}
    }
}

