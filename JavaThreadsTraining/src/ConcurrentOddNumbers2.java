import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trace.grader.basics.GraderBasicsTraceUtility;
import util.misc.ThreadSupport;

// All calls to printProperty are important for testing
public class ConcurrentOddNumbers2 {
	/**
	 * Just as a script can be executed by multiples actors at the same or different 
	 * times, the main() method can be executed can be executed by multiple threads
	 * at the same or different times.
	 * 
	 * Each time you execute a main class, a different Main thread is created to
	 * execute the main() method.
	 * 
	 * More interestingly, a non Main thread can also call main().	 * 
	 * This is what happens when you run our tests. The testing thread calls your main() method, 
	 * sometimes multiple times, with different arguments.
	 * 
	 * In this example, any thread that executes this main() method will be called
	 * a dispatcher or forker thread as its job is to dispatch work to worker
	 * threads it creates.
	 * 
	 * * The Main thread starts when when main() is called and terminates
	 * when main() ends. In between the start and stop, a stack of calls can be serviced
	 * by the Main thread. This stack grows and shrinks, as different methods are called,
	 * In this example, for instance, some example stack snapshots are: 
	 * ConcurrentOddNumbers.main()->OddNumnbersUtil.fillRandomNumbers
	 * ConcurrentOddNumbers.main()->OddNumnbersUtil.fillRandomNumbers->OddNumbersUtil.generateRandomNumbers 
	 * ConcurrentOddNumbers.main()-->ConcurrentOddNumbers.fillOddNumbers()
	 
	 */
	public static final int MAX_RANDOM_NUMBER = 1000;
	private static int[] randomNumbers; // input list populated based on main argument
	private static List<Integer> oddNumbers = new ArrayList(); // variable length output list
	private static int totalNumberOddNumbers = 0;
	private static void resetGlobals() {
		oddNumbers.clear();
		totalNumberOddNumbers = 0;
	}

	public static void main(String[] args) {
		// The main method may be called multiple times by testing code
		// so main should reset these static variables
		resetGlobals();
		fillRandomNumbers(args);
		fillOddNumbers(randomNumbers);
		printOddNumbers();
	}

	private static void fillOddNumbers(int[] anInputList) {
		// swap the commented call with the uncommented one to turn on parallel processing
//		serialFillOddNumbers(anInputList);
		OddNumbersDispatcherCode.concurrentFillOddNumbers(anInputList);
	}
	private static void serialFillOddNumbers(int[] anInputList) {
		//In serelai version, fill the entire set
		fillOddNumbersSubset(anInputList, 0, randomNumbers.length);
	}
	private static void printOddNumbers() {
		GraderBasicsTraceUtility.printProperty("Total Num Odd Numbers", totalNumberOddNumbers);
		GraderBasicsTraceUtility.printProperty("Odd Numbers", oddNumbers);
	}
	private static void fillRandomNumbers(String[] args) {
		int aNumRandomNumbers = firstArgToInteger(args); // get the number from arguments
		randomNumbers = generateRandomNumbers(aNumRandomNumbers);
		GraderBasicsTraceUtility.printProperty("Random Numbers", Arrays.toString(randomNumbers));
	}
	private static int[] generateRandomNumbers(int aNumRandomNumbers) {
		int[] retVal = new int[aNumRandomNumbers];
		for (int index = 0; index < retVal.length; index++) {
			double aRandomDouble = Math.random(); // number between 0 and 1
			int aRandomInteger = (int) (aRandomDouble * MAX_RANDOM_NUMBER);
			retVal[index] = aRandomInteger;
		}
		return retVal;
	}
	/**
	 * This is a static method that is called by the run methods of all worker threads.
	 * A method that can be executed by multiple threads is called a shared
	 * method. Such a method can be synchronized or not based on whether
	 * it has the synchronized keyword in its header.
	 * 
	 * Only one synchronized static method of a class can be executed at any time
	 * by any thread. When a synchronized static method is being executed by a thread.
	 * other threads that wish to execute it wait in a queue until the method is exited.
	 * 
	 * Such waiting makes the code safe in that global variables (those not delcared
	 * in the method) do not become inconsistent.
	 * 
	 * Such waiting also slows down computation. So we should synchronize as little code 
	 * as possible.
	 * 
	 *
	 * Shared Methods that do not modify global variables should not be 
	 * be synchronized. If they call unsafe methods then those
	 * methods should be synchronized. 
	 * 
	 * We can synchronize the calling method, but that will synchronize not only 
	 * the unsafe called methods but also other safe code executed by the caller method.
	 * 
	 * Should this method be synchronized? 
	 * 
	 * If so, uncomment the first line in its header. 
	 */
	// This is a a typical parallelizable loop for processing some part of the input
	// The nature of the prints are determined by the testing code. The code
	// has been written to allow it to be used in the serial and concurrent version.
	// The serial version processes the entire input.
	// The concurrent version, called by each thread, processes part of the input
	// specified by the two indices.

	public static void fillOddNumbersSubset(int[] anInputList, int aStartIndex, int aStopIndex) {
		int aNumberOfOddNumbers = 0;
		for (int index = aStartIndex; index < aStopIndex; index++) {
			GraderBasicsTraceUtility.printProperty("Index", index);
			int aNumber = anInputList[index];
			GraderBasicsTraceUtility.printProperty("Number", aNumber);
			boolean isOdd = isOddNumber(aNumber);
			GraderBasicsTraceUtility.printProperty("Is Odd", isOdd);
			if (isOdd) {
				addOddNumber(aNumber);
				aNumberOfOddNumbers++; // uncomment this when asked
			}

			// Uncomment the following block of code if you get the message
			// no interleaving of threads
//			ThreadSupport.sleep(1);// Force a switch to another ready thread using min sleep time			
		}
		GraderBasicsTraceUtility.printProperty("Num Odd Numbers", aNumberOfOddNumbers);
	}

	public static boolean isOddNumber(int aNumber) {
		return aNumber % 2 == 1;
	}
	/*
	 * The following code will be executed by each of the dispatched threads.
	 * If they execute concurrently, that is, are not serialized, then this
	 * can result in race conditions, if a thread switch occurs between the
	 * two statements. Therefore it is important to synchronize access to
	 * this method
	 */
	// Uncomment the following line to serialize access to the  variables,
	// totalNumberOffNumbers and oddNumbers, shared by all worker threads
//	synchronized 
	public static void addOddNumber(int aNumber) {
		// The first operation is redundant but is performed to increase race
		// condition chances, simulating machine instructions
		int aRegister = totalNumberOddNumbers; // Simulate load memory to register
		GraderBasicsTraceUtility.printProperty("register", aRegister);
//		ThreadSupport.sleep(1);//force a switch to another ready thread
		// Before the register is incremented, another thread may also load
		// the same value for totalNumberOddNumbers in its local register variable,
		// in case the two increments will result in one increment
		aRegister++; // increment register
		GraderBasicsTraceUtility.printProperty("register", aRegister);
		totalNumberOddNumbers = aRegister; // Simulate save register to memory
		oddNumbers.add(aNumber); // ArrayList is also not thread safe
	}

	public static int firstArgToInteger(String[] args) {
		// If nothing was passed on the command line, then print error and exit
		if (args.length < 1) {
			System.err.println("Please supply at least single argument to the main class");
			System.exit(0);
		}
		// Convert the first command line argument to an integer, exit if error
		try {
			return Integer.parseInt(args[0]);
		} catch (Exception ex) {
			System.err.println("Cannot convert argument on command line to integer");
			System.exit(1);
		}
		return -1;
	}
}

/**
 * This class is key to concurrency. It is an Runnable implementation -
 * the code to be executed by a thread. An instance of this class
 * is passed to the java Thread object, which represents a running thread. 
 * When we invoke the start method on a Thread object, a new thread starts 
 * executing in a new stack whose base is the Run method of the Runnable instance 
 * bound to the thread.
 * 
 * In our example, the class represents the work decomposed by the dispatcher.
 * This decomposition simply consists of giving the start and stop index in the 
 * input list. The worker processes the sublist represented by the two indices.
 */
class OddNumbersWorkerCode2 implements Runnable {
	int[] inputList;
	int startIndex, stopIndex;

	public OddNumbersWorkerCode2(int[] anInputList, int aStartIndex, int aStopIndex) {
		inputList = anInputList;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}

	@Override
	public void run() { // executed when we start the associated thread.
		// Like sequential code except we do only our subset of the problem,
		// defined by the two indices
		ConcurrentOddNumbers2.fillOddNumbersSubset(inputList, startIndex, stopIndex);
	}
}

/**
 * The code in the following class is executed by the main thread, which behaves 
 * like a dispatching thread implementing the fork-join model.
 *  
 * In this model, a dispatching thread creates or forks a bunch of child threads,
 * determines, the amount of work each thread will do, 
 * tells each thread what their part of the work is, starts the threads, waits for
 * them to terminate, and outputs the results based on variables shared with all
 * forked threads.
 * 
 * Each child or forked thread does its work, and writes results to the shared
 * variables and terminates.
 * 
 * This model is independent of Java, and we see below how it is implemented in Java.
 */
class OddNumbersDispatcher2 {
	private static final int MAX_THREAD_EXECUTION_TIME = 
			100;// we expect each worker thread to take less than 100 ms
	public static final int NUM_THREADS = 4; // number of forked threads is fixed
	private static Thread[] threads = new Thread[NUM_THREADS];
	private static Runnable[] decomposedWork = 
			new Runnable[NUM_THREADS]; // code executed by each thread 
	/**
	 * Determines how many elements of the input list, whose size is, aProblemSize
	 * will be processed by the	thread whose index in the threads global is aThreadIndex.
	 *  
	 * The problem may not be evenly divided among the threads. The thread index is
	 * used to determine which threads do extra work.
	 */
	private static int threadProblemSize(int aThreadIndex, int aProblemSize) {
		// Following is the size if the problem can be eveny divided among the threads
		int aMinimumProblemSize = aProblemSize / NUM_THREADS;
		// Some threads must take one or more of the remaining iterations
		int aRemainder = aProblemSize % NUM_THREADS;
		return aMinimumProblemSize + 
			// swap call to unfairThreadRemainderSize with fairThreadRemainderSize to balance load
			unfairThreadRemainderSize(aThreadIndex, aRemainder);
//			fairThreadRemainderSize(aThreadIndex, aRemainder);
	}	
	
	/**
	 * Distribute one unit of work to first aRemainder threads	 * 	 
	 */
	private static int fairThreadRemainderSize(int aThreadIndex, int aRemainder) {
		if (aThreadIndex < aRemainder) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 	Give each  unit of work to first  thread, aRemainder is < NUM_THREADS
	 */
	private static int unfairThreadRemainderSize(int aThreadIndex, int aRemainder) {
		if (aThreadIndex == 0) {
			return aRemainder;
		} else {
			return 0;
		}
	}
	
	/**
	 * Decompose the work into a bunch of Runnable instances, using the method 
	 * above to	determine how much work each thread does.
	 */
	private static void createRunnables(int[] randomNumbers) {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, randomNumbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			decomposedWork[aThreadIndex] = new OddNumbersWorkerCode(randomNumbers, aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}
	}	
	
	/**
	 * Create threads, associate them with thread indices, and use the method
	 * above to give each thread its Runnable decomposed work.
	 */
	private static void createAndStartThreads() {
		for (int index = 0; index < decomposedWork.length; index++) {
			// create thread and give it its work
			threads[index] = new Thread(decomposedWork[index]);
			threads[index].start(); // start the thread
			// Comment out this call to ensure concurrent
			// rather than serial execution of the thread run methods
			// This code delays creation of the next thread until the previous
			// thread has finished execution, avoiding race conditions in 
			// accessing shared variables
			ThreadSupport.sleep(MAX_THREAD_EXECUTION_TIME);
		}
	}

	/**
	 * Makes the dispatcher thread wait for the termination of each joined thread
	 */
	private static void joinThreads() {
		for (int index = 0; index < threads.length; index++) {
			try {
				threads[index].join();
			} catch (InterruptedException e) {
				e.printStackTrace(); // the waiting thread may be interrupted
			}
		}
	}
	public static void concurrentFillOddNumbers(int[] anInputList) {
		createRunnables(anInputList);
		createAndStartThreads();
		joinThreads();
	}
}


