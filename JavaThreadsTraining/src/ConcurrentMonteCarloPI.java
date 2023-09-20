import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentMonteCarloPI {
	// all methods and variables accessed by static main must ne static

// START CONCURRENCY INDEPENDENT VARIABLES
	public static final int LARGE_PROBLEM_SIZE = 10; // no prints if randomNumbers > LARGE_ARRAY_SIZE

	private static int totalNumberInCircle = 0;
	private static int minIterationsPerThread;
	

// END CONCURRENCY INDEPENDENT VARIABLES

// START CONCURRENCY SPECIFIC VARIABLES
	// Number of threads given to us by this problem specification
	// So we can create the two arrays statically.
	// Sometimes the number of threads is given as a main arg, in which
	// case the arrays are created after the main arg is processed
	public static final int NUM_THREADS = 4;
	private static Runnable[] workers = new Runnable[NUM_THREADS];
	private static Thread[] threads = new Thread[NUM_THREADS];
	private static int totalNumberOfIterations;
// END CONCURRENCY SPECIFIC VARIABLES

	public static void main(String[] args) {
		minIterationsPerThread = toInteger(args);

		totalNumberOfIterations = minIterationsPerThread * NUM_THREADS;
		printProperty("Total Iterations", totalNumberOfIterations);
		// sequential code below computes the same number of iterations as concurrent
		// code should
//		totalNumberInCircle = inCircle(aTotalNumIterations);	
		serialPI();
		concurrentPI();
		printProperty("Total In Circle", totalNumberInCircle);
		double result = (((double) totalNumberInCircle) / totalNumberOfIterations) * 4;

		printProperty("PI", result);

	}

// START PROBLEM_SPECIFIC SEQUENTIAL CODE
	private static void serialPI() {
		totalNumberInCircle = inCircle(totalNumberOfIterations);
	}

	public static int inCircle(int aNumberOfIterations) {
		int numIn = 0;
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		for (int iterationNum = 0; iterationNum < aNumberOfIterations; iterationNum++) {
			// get random number from 0 to 1
			printProperty("Iteration Num", iterationNum);
			double x = rand.nextDouble();
			double y = rand.nextDouble();
			printProperty("X", x);
			printProperty("Y", y);
			double hypotenuse = Math.sqrt(x * x + y * y);
			boolean inside = hypotenuse < 1.0;
			printProperty("In Circle", inside);
			if (inside) {
				numIn++;
			}
		}
		return numIn;

	}

	synchronized // uncomment this line when asked
	public static void addNumberInCircle(int aThreadTotal) {
		totalNumberInCircle += aThreadTotal;

	}
// END PROBLEM_SPECIFIC SEQUENTIAL CODE

// START BOILER PLATE TESTABLE SEQUENTIAL CODE
	// This code should be given to the student.

	// Minor variations of boiler plate methods code can be reused in all problems

	// Many problems require converting a single string argument to an integer
	// This code can be used directly in such problems
	public static int toInteger(String[] args) {
		// Number of threads to be executed shoul be passed on the command line

		// If nothing was passed on the command line, then print error and exit
		if (args.length < 1) {
			System.err.println("Please supply a single argument to the main class");
			System.exit(0);
		}
		// Convert the command line string to an integer, exit if error
		try {
			return Integer.parseInt(args[0]);
		} catch (Exception ex) {
			System.err.println("Cannot convert argument on command line to integer");
			System.exit(1);
		}
		return -1;
	}

	// This method should be used directly in all testable programs
	// it does the print only if the problem size is small - implying testing or
	// debugging. The performance benefits of concurrency are demonstrated only
	// when problems are large, when we do not want prints to clutter the display
	// and slow computation
	public static void printProperty(String aPropertyName, Object aPropertyValue) {
		if (isSmallProblem()) {
			System.out.println(threadPrefix() + aPropertyName + ":" + aPropertyValue);
		}
	}

	// This method must be adapted for each problem based on the nature of
	// the problem-specific input
	public static boolean isSmallProblem() {
		return minIterationsPerThread < LARGE_PROBLEM_SIZE;
	}

	// This method must be reused directly in all testable code and should
	// be given to the student
	public static String threadPrefix() {
		return "Thread " + Thread.currentThread().getId() + "->";
	}
// END BOILER PLATE TESTABLE SEQUENTIAL CODE

//// START BOILER PLATE FORK_JOIN CONCURRENT CODE
//	// This code can also be given to the student. It is analogous to the
//	// the fork-join directives OpemMP provides for us. In fact, OpenMP
//	// generates code such as this
//	
//	
	private static void concurrentPI() {
		createRunnables();
		createAndStartThreads();
		joinThreads();
		
	}
	private static void createRunnables () {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			workers[aThreadIndex] = new MonteCarloWorker(minIterationsPerThread);
		}
	}
	
	
	
	private static void createAndStartThreads() {
		for (int index = 0; index < workers.length; index++) {
			threads[index] = new Thread(workers[index]);
			threads[index].start();
			// comment out this try catch block when asked
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	private static void joinThreads() {
		for (int index = 0; index < threads.length; index++) {
			try {
				threads[index].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}
//// END BOILER-PLATE FORK_JOIN CONCURRENT CODE	
//	
}

class MonteCarloWorker implements Runnable {
	int numberOfIterations;
	public MonteCarloWorker(int aNumberOfIterations) {
		numberOfIterations = aNumberOfIterations;
	}
	@Override
	public void run() {
//		// like sequential code except we do only our part of the problem
		int myInCircle = ConcurrentMonteCarloPI.inCircle(numberOfIterations);
		ConcurrentMonteCarloPI.printProperty("Num In Circle", myInCircle);
		ConcurrentMonteCarloPI.addNumberInCircle(myInCircle);
	}
}

