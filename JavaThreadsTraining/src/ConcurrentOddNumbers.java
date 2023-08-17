import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConcurrentOddNumbers {
	// all methods and variables accessed by static main must be static

// START CONCURRENCY INDEPENDENT VARIABLES
	public static final int MAX_RANDOM_NUMBER = 1000;
	private static int[] randomNumbers; // input lust populated based on main aghument
	public static final int LARGE_ARRAY_SIZE = 30; // no prints if randomNumbers > LARGE_ARRAY_SIZE

	private static List<Integer> oddNumbers = new ArrayList(); // variable length output list

	private static int totalNumberOddNumbers = 0;

// END CONCURRENCY INDEPENDENT VARIABLES

// START CONCURRENCY SPECIFIC VARIABLES
	// Number of threads given to us by this problem specification
	// So we can create the two arrays statically.
	// Sometimes the number of threads is given as a main arg, in which
	// case the arrays are created after the main arg is processed
	public static final int NUM_THREADS = 4;
	private static Runnable[] workers = new Runnable[NUM_THREADS];
	private static Thread[] threads = new Thread[NUM_THREADS];
// END CONCURRENCY SPECIFIC VARIABLES

	public static void main(String[] args) {
		// the method may be called multple times by testing code
		// so main should reset these static variables
		oddNumbers.clear();
		totalNumberOddNumbers = 0;
		
		int aNumRandomNumbers = toInteger(args); // get the number from arguments
		randomNumbers = new int[aNumRandomNumbers];
		fillRandomNumbers();

//		serialFillOddNumbers(); // comment this line to turn off serial processing
		concurrentFillOddNumbers(); // uncomment this line to turn on parallel processing

		printProperty("Total Num Odd Numbers", totalNumberOddNumbers);
		printProperty("Odd Numbers", oddNumbers);

	}

// START PROBLEM_SPECIFIC SEQUENTIAL CODE

	// Such code can be given to the students of they can be first asked to
	// write it before their parallel assignment

	private static void serialFillOddNumbers() {
		fillOddNumbers(randomNumbers, 0, randomNumbers.length);
	}

	// A method like this occurs in most concurrent problems, defining the
	// input to the problem
	private static void fillRandomNumbers() {
		for (int index = 0; index < randomNumbers.length; index++) {
			double aRandomDouble = Math.random(); // number between 0 and 1
			int aRandomInteger = (int) (aRandomDouble * MAX_RANDOM_NUMBER);
			randomNumbers[index] = aRandomInteger;
		}
		// The prints in this solution help with debugging and they follow a
		// certain format to allow automatic testing.
		// They are essentially printing a i of values, the first one indicating
		// an external name of a -property (external variable) and the second one
		// indicating the value of the property. Thus, "Random Numbers" is the
		// external name of the input array. The names and type of of the problem
		// properties are determined by the problem specification and associated
		// testing code while the internal names and types are determined by the
		// implementation. Illustrative output implicitly defines their nature
		printProperty("Random Numbers", Arrays.toString(randomNumbers));
	}

	// This is a a typical parallelizable loop for processing some part of the input
	// Again the nature of the prints are determined by the testing code. The code
	// has been written to allow it to be used in the serial and concurrent version

	public static void fillOddNumbers(int[] aNumbers, int aStartIndex, int aStopIndex) {
		int aNumberOfOddNumbers = 0;
		for (int index = aStartIndex; index < aStopIndex; index++) {
			// Start of typical iteration code
			printProperty("Index", index);
			int aNumber = aNumbers[index];
			printProperty("Number", aNumber);
			// End of typical iteration code

			// Start of custom iteration code
			boolean isOdd = isOddNumber(aNumber);
			printProperty("Is Odd", isOdd);
			if (isOdd) {
				addOddNumber(aNumber);
				aNumberOfOddNumbers++; // uncomment this when asked
			}
			// End of custom iteration code
		}
		// Some summary of loop computation is typically output after iterations
		// and before termination
		printProperty("Num Odd Numbers", aNumberOfOddNumbers);
	}
	public static void fillOddNumbersIn(int[] aNumbers, int aStartIndex, int aStopIndex) {
		int aNumberOfOddNumbers = 0;
		for (int index = aStartIndex; index < aStopIndex; index++) {
			// Start of typical iteration code
			printProperty("Index", index);
			int aNumber = aNumbers[index];
			printProperty("Number", aNumber);
			// End of typical iteration code

			// Start of custom iteration code
			printProperty("Is Odd", aNumber % 2 == 2);
			if (aNumber % 2 != 2 ) {
				addOddNumber(aNumber);
				aNumberOfOddNumbers++; // uncomment this when asked
			}
			// End of custom iteration code
		}
		// Some summary of loop computation is typically output after iterations
		// and before termination
		printProperty("Num Odd Numbers", aNumberOfOddNumbers);
	}
	public static boolean isOddNumber(int aNumber) {
		return aNumber % 2 == 1;
	}

	// uncomment the following line to serialize access to the shared variables
//	synchronized 
	public static void addOddNumber(int aNumber) {
		// the first operation is redundant but is performed to increase race
		// condition chances
		totalNumberOddNumbers++; // this action is also not thread safe
		oddNumbers.add(aNumber); // ArrayList is not thread safe

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
			String aComposition = threadPrefix() + aPropertyName + ":" + aPropertyValue;
//			System.out.println(threadPrefix() + aPropertyName + ":" + aPropertyValue);
			System.out.println(aComposition);

		}
	}

	// This method must be adapted for each problem based on the nature of
	// the problem-specific input
	public static boolean isSmallProblem() {
		return randomNumbers.length < LARGE_ARRAY_SIZE;
	}

	// This method must be reused directly in all testable code and should
	// be given to the student
	public static String threadPrefix() {
		return "Thread " + Thread.currentThread().getId() + "->";
	}
// END BOILER PLATE TESTABLE SEQUENTIAL CODE

// START BOILER PLATE FORK_JOIN CONCURRENT CODE
	// This code can also be given to the student. It is analogous to the
	// the fork-join directives OpemMP provides for us. In fact, OpenMP
	// generates code such as this

	private static void concurrentFillOddNumbers() {
		createRunnables();
		createAndStartThreads();
		joinThreads();
	}

	private static void createRunnables() {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, randomNumbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			workers[aThreadIndex] = new OddNumbersWorker(randomNumbers, aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}
	}

	public static int threadProblemSize(int aThreadIndex, int aProblemSize) {
		// following is the size if the problem can be eveny divided among the threads
		int aMinimumProblemSize = aProblemSize / NUM_THREADS;
		// some threads must take one of the remaining iterations
		int aRemainder = aProblemSize % NUM_THREADS;
		// distribute the remainder among the first aRemainder threads
		if (aThreadIndex < aRemainder) {
			return aMinimumProblemSize + 1;
		}
		return aMinimumProblemSize;
	}
//	public static final int foo = 100;
	public static final int MAX_THREAD_EXECUTION_TIME = 100;
	private static void createAndStartThreads() {
		for (int index = 0; index < workers.length; index++) {
			threads[index] = new Thread(workers[index]);
			threads[index].start();
			// comment out this try catch block to ensure concurrent
			// rather than serial execution of the thread run methods
			try {
//				Thread.sleep(100);
//				Thread.sleep(foo);
				Thread.sleep( MAX_THREAD_EXECUTION_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Makes the caller wait for the termination of each joined thread
	 */
	private static void joinThreads() {
		for (int index = 0; index < threads.length; index++) {
			try {
				threads[index].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
// END BOILER-PLATE FORK_JOIN CONCURRENT CODE	

}

/**
 * This class is key to concurrency. Creating a new thread essentially involves
 * calling a method without waiting for it to immediately finish. This method
 * forms the base of a new stack, associated with a new spawned or forked
 * thread. The thread API needs to know the address of this method so that it
 * can start execution of it. In languages with procedure parameters, one can
 * pass to the API the address of the method. In Java we pass instead an object
 * with a well known procedure, run, declared in a predefined interface,
 * Runnable. It is an instance to this class, which implements Runnable, which
 * is passed to the Thread object. When we invoke the start method on a Thread
 * object, a new thread starts executing in a new stack whose base is the Run
 * method of the Runnable instance bound to the thread.
 *
 */
class OddNumbersWorker implements Runnable {
	int[] numbers;
	int startIndex, stopIndex;

	public OddNumbersWorker(int[] aNumbers, int aStartIndex, int aStopIndex) {
		numbers = aNumbers;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}

	@Override
	public void run() { // executed when we start the associated thread.
		// like sequential code except we do only our part of the problem,
		// defined by the two indices
		ConcurrentOddNumbers.fillOddNumbers(numbers, startIndex, stopIndex);
	}

}
