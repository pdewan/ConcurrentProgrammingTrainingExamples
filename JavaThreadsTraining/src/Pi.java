import java.lang.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Pi {
	public static final int NUM_THREADS = 4;
    public static void main(String[] iters) {
	int numIter = 0;
	if (iters.length < 1) {
	    System.err.println("usage: Pi <iterations>");
	    System.exit(0);
	}
	try {
	    numIter = Integer.parseInt(iters[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}

	long s1 = System.nanoTime();
	Runnable[] runnables = new Runnable[4];
	Thread[] threads = new Thread[NUM_THREADS];
	for (int i = 0; i < NUM_THREADS; i++) {
	    runnables[i] = new Monte(numIter/NUM_THREADS);
	    threads[i] = new Thread(runnables[i]);
	    threads[i].start();
	}

	double answer = 0;
	try {
	    for (int i = 0; i < 4; i++) {
		threads[i].join();
		double threadInCircle = ((Monte) runnables[i]).getInCircle();
//		System.out.println("From Thread:" + i + " In Circle:" + threadInCircle);
//		answer += ((Monte) runnables[i]).getInCircle();
		answer += threadInCircle;

	    }
	} catch (Exception ex) {
	    System.err.println("Thread interrupted");
	    System.exit(2);
	}

	long s2 = System.nanoTime();
	System.out.println("Time: " + (s2 - s1)/1000000000.0);
	System.out.println("Root Thread->PI:" + NUM_THREADS*(answer/numIter));
	
    }
}

class Monte implements Runnable {
    static int nextId;
    private double inCircle;
    private int iters;
    int myId;
    public double[] xRandoms;
    public double[] yRandoms;

    
    public void run() {
      xRandoms = new double [iters];
      yRandoms = new double [iters];
	  inCircle = findInside(iters);
	  printProperty("XRandoms:", Arrays.toString(xRandoms));
	  printProperty("YRandoms:", Arrays.toString(yRandoms));
	  printProperty("InCircle:", inCircle);

    }

    public Monte(int iterations) {
	iters = iterations;
	myId = nextId++;

    }

    public double getInCircle() {
	return inCircle;
    }
    
    private double findInside(int iterations) {
	ThreadLocalRandom rand = ThreadLocalRandom.current();
	int numIn = 0;
	int numOut = 0;
	for (int i = 0; i < iterations; i++) {
	    // get random number from 0 to 1
	    double x = rand.nextDouble();
	    double y = rand.nextDouble();
	    printProperty("X", x);
	    printProperty("Y", y);
	    xRandoms[i] = x;
	    yRandoms[i] = y;
	    double hyp = Math.sqrt(x*x + y*y);
	    boolean inside = hyp < 1.0;
	    printProperty("Inside", inside);
	    if (inside) {
		numIn++;
	    }
	}
	return numIn;
     }
    public static final int LARGE_PROBLEM_SIZE = 5; 

	 void printProperty(String aPropertyName, Object aPropertyValue) {
		if (iters < LARGE_PROBLEM_SIZE) {
			System.out.println(threadPrefix() + aPropertyName+ ":" + aPropertyValue);
		}
	}
	public static String threadPrefix() {
		return "Thread " + Thread.currentThread().getId() + "->";
	}
}

