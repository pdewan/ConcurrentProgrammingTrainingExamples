import static trace.grader.basics.GraderBasicsTraceUtility.printProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.misc.ThreadSupport;



/** 
 * If N is the supplied argument, this code uses the fork-join model to generate 
 * N random numbers and identify the odd numbers in this list and their count.
 * 
 * If is not supplied, then a default value of 6 is assumed.
 * 
 * The comments in the class explain the Java thread API features
 * used in this program. which include: 
 * the interface Runnable,  
 * the class Thread, 
 * the Thread constructor,
 * the Thread instance methods start() and join(),
 * the synchronized keyword in a method declaration.
 * 
 * As you will see, this file actually has not only the main class 
 * ConcurrentOddNumbers, but also four other inner classes, OddNumbersWorkerCode, 
 * OddNumbersDispatcherCode, OddNumbersRepository, and OddNumbersUtil.  
 * Hopefully, this separation of concerns make the code more understandable. 

 * The code has three bugs in the following methods, 
 * which you should fix in the order below:
 * 1.	forkAndJoinThreads in class OddNumbersDispatcherCode .
 * 2.	fairThreadRemainderSize in class OddNumbersDispatcherCode .
 * 3.	incrementTotalOddNumbers in OddNumbersSharedRepository. 
 *      The fix for the bug in this is actually identified – 
 *      your task is to understand why it is a fix.
 *  
 */
public class ConcurrentOddNumbers {
	static int[] randomNumbers; // Input list populated based on main argument
	
	/**
	 * 
	 * In general, a thread is an independent unit of steps, coded in its
	 * root method.
	 * 
	 * The steps in its root method can be executed while other threads are 
	 * executing their root methods. 
	 * 
	 * A thread has its own stack to service the calls made by its root method.
	 * 
	 * In the physical world, a thread corresponds to an actor and its 
	 * root method is the script followed by the actor.
	 * 
	 * The main() method  is  executed by a Java created thread, 
	 * called the Main thread. 
	 * 
	 * All methods called directly or indirectly by the main() method are executed
	 * by this thread.
	 * 
	 * The main() method is always at the root of the thread's stack,
	 * and thus, is called the root method of the thread.
	 * 
	 * Other methods such as the dispatcher methods get pushed on top of the
	 * stack when they are called and popped when they return.
	 *  
	 * A thread starts when its root method is called and terminates
	 * when the root method ends. 
	 * 
	 * In between the start and stop, a stack of calls can be serviced
	 * by a thread.
	 *  
	 * This stack grows and shrinks, as different methods are called.
	 * 
	 * In this example, for instance, two example stack snapshots are: 
	 * ConcurrentOddNumbers.main()->OddNumnbersUtil.fillRandomNumbers->OddNumbersUtil.generateRandomNumbers 
	 * ConcurrentOddNumbers.main()->ConcurrentOddNumbers.fillOddNumbers()
	 * 
	 */
	public static void main(String[] args) {
		// Trace the start of this method and associated thread
		printProperty("main() called with arguments", 
				Arrays.toString(args));
		
		// Ask the util class to generate a sequence of random numbers
		randomNumbers = OddNumbersUtil.fillRandomNumbers(args); 
		
		// Ask the dispatcher class to "fork" and "join" child threads to identify 
		// which of the  generated numbers are odd.	
		// This code will create worker threads that can work independently
		// of the thread that called main()
		OddNumbersDispatcherCode.fillOddNumbers(randomNumbers);
		
		// Ask the util class to print the odd numbers
		OddNumbersUtil.printOddNumbers(); 
		
		// Trace the end of this method and associated thread
		printProperty("main() terminates with arguments", Arrays.toString(args));
	}
}

/**
 * The Main thread is created automatically by the JVM.
 * 
 * What if an existing thread wants to create new thread, which is required in the
 * fork-join and other forms of concurrency?
 * 
 * In Java, a non-Main thread must execute a parameterless instance 
 * root method called run() in a class that implements the Java Runnable interface.
 * 
 * The following is such a Runnable class, which represents the code executed by
 * each forked worker thread.
 * 
 * Different worker threads are bound to different Runnable instances of this class. 
 * 
 * This binding is done by the dispatcher class defined later.
 * 
 */
class OddNumbersWorkerCode implements Runnable {
	int[] inputList;
	int startIndex, stopIndex;
	
	/**
	 * Unlike the main() method, the run() method does not take parameters.
	 * 
	 * Any parameters needed by it are passed as parameters to the constructor(s) 
	 * of the enclosing Runnable class, which stores them in instance variables 
	 * accessible to the run() method.
	 * 
	 * In this example, these indicate the list of generated random numbers,
	 * and the portion of this list that this instance of Runnable must process.
	 */
	public OddNumbersWorkerCode(int[] anInputList, int aStartIndex, int aStopIndex) {
		inputList = anInputList;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}
	
	/**
	 * This is an instance method that becomes the root method of a new thread.
	 * It is executed when we start the associated thread.
	 * How to associate itwith a new thread is shown in the next class.
	 */
	@Override
	public void run() {
		printProperty("run() called to start processing subsequence",  
				startIndex + "-" + stopIndex );
		
		// The run method just delegates work to another method.
		fillOddNumbers(inputList, startIndex, stopIndex);
		
		printProperty("run() terminates to end processing of subsequence",  
				startIndex + "-" + stopIndex );
	}	
	/** 
	 * The code executed by this method is a typical iteration loop executed
	 * by worker threads. It finds all odd numbers in the subsequence of the
	 * anInputList between whose indices are >= aStartIndex and < aStopIndex. 
	 */	
	public static void fillOddNumbers(int[] anInputList, 
					int aStartIndex, int aStopIndex) {
		int aNumberOfOddNumbers = 0;		
		for (int index = aStartIndex; index < aStopIndex; index++) {
			// trace the input processed by this iteration
			printProperty("Index", index);
			int aNumber = anInputList[index];
			printProperty("Number", aNumber);
			
			// compute and trace the result computed by this iteration
			boolean isOdd = OddNumbersUtil.isOddNumber(aNumber);
			printProperty("Is Odd", isOdd);
			
			// deposit the result in a shared repository
			if (isOdd) {			
				OddNumbersRepository.addOddNumber(aNumber);
				// This repository is shared with other worker threads and the
				// dispatcher (Main) thread.
				
				aNumberOfOddNumbers++; // updating a local variable
			}			
		}
		
		// This is the number of odd numbers found by the thread that
		// executes this loop. The total number computed by all threads
		// is in the shared repository
		printProperty("Num Odd Numbers", aNumberOfOddNumbers);
	}
}

/**
 * This class contains contains "dispatching" code executed by main().
 * 
 * This code creates the worker threads, associates them with Runnable instances, 
 * starts them, waits for them to finish, and prints the results. 
 * 
 * When the Main thread executes this code,
 * it becomes the dispatching thread in the fork-join model,
 * dispatching work to forked child threads.
 *
 */
class OddNumbersDispatcherCode {	
	
	public static final int NUM_THREADS = 4; // number of forked threads is fixed
	
	// A thread is represented in Java by an instance of the Thread class.
	// So this array keeps track of the worker threads created.
	private static Thread[] threads = new Thread[NUM_THREADS];
	
	// These are the Runnable instances to be bound to the threads above.
	// There is a one to one correspondence between a thread and a Runnable
	private static Runnable[] runnables = 
			new Runnable[NUM_THREADS]; // Code executed by each thread 
	
	/**
	 * This is the method called by main(), and it delegates its work to other methods
	 */
	static void fillOddNumbers(int[] anInputList) {
		OddNumbersRepository.reset(); // initialize the shared repository
		OddNumbersDispatcherCode.concurrentFillOddNumbers(anInputList);
	}	
	
	public static void concurrentFillOddNumbers(int[] anInputList) {
		createRunnables(anInputList); // First create the runnables
		forkAndJoinThreads(); // Next create, start, and join threads
	}
	
	/**
	 * 
	 * The goal of this method to make a set of worker threads execute concurrently,
	 * and then make the forking dispatcher thread  wait for all of them to finish. 
	 * 
	 * In this program you know threads are running concurrently if 
	 *   1. multiple threads produce output.
	 * 	 2. thread output is interleaved or mixed, that is, before a thread finishes 
	 *      its output, one or more of other threads produce output. 
	 * 
	 * In sequential execution, either one thread produces all output, or between
	 * the first and last output of a thread, no other thread produces output.
	 * 
	 * The method is buggy in that it does not quite meet this goal. You need to
	 * find and fix the bug(s) in it. 
	 * 
	 */
	private static void forkAndJoinThreads() {
		// runnable index is also a thread index
		for (int aThreadIndex = 0; aThreadIndex < threads.length; aThreadIndex++) {			
			forkThread(aThreadIndex);
			joinThread(aThreadIndex);
		}
	}
	
	private static void forkThread (int aThreadIndex) {
		// The call new Thread(aRunnable) creates a new thread object and
		// binds it to the Runnable constructor argument. 
		threads[aThreadIndex] = new Thread(runnables[aThreadIndex]);	
					
		printProperty("Starting", OddNumbersUtil.threadName(threads[aThreadIndex]));			 
					threads[aThreadIndex].start(); 
		// The call t.start() starts the thread represented by t, that is, 
		// creates a new stack, and executes the run() method of the 
		// Runnable instance bound to t.
	}
	
	private static void joinThread (int aThreadIndex) {
		try {
			printProperty(
					"Stopping execution until the following thread terminates",
					OddNumbersUtil.threadName(threads[aThreadIndex]));
			threads[aThreadIndex].join(); 
			// t.join blocks the calling thread until t finishes,
			// that is, stops the caller from executing the next instruction
			// until t finishes executing its run method. 
			
			// This blocking akin to a readLine input call blocking the caller 
			// until the user types a line. 
			
			// Thus, this call reduces the concurrency in the system, as it
			// makes the joining thread wait until the joined thread t finishes.				
			// In other words, even though the joining thread *exists* concurrently
			// with the joined thread, it is not allowed to execute concurrently 
			// with it. 
			
			// If t has finished executing when t.join() is called, the call
			// does not block the joining thread.
			
			// A thread can join multiple threads, one at a time, by
			// making a series of such calls on each of these threads
			printProperty(
					"Resuming execution as the following thread has terminated", 
					OddNumbersUtil.threadName(threads[aThreadIndex]));
		} catch (InterruptedException e) {				
			e.printStackTrace();
			// The joined thread may be interrupted while the joiner thread
			// is waiting for it to finish. If this happens the joiner
			// unblocks, and services an InterruptedException.
		}		
	}
	
	/**
	 * This method decomposes the work of processing the random numbers 
	 * into a bunch of Runnable instances, one for each worker thread
	 * to be started. 
	 * 
	 * A decomposed work unit is a subsequence of the input random number sequence.
	 *  
	 * It is represented by the start and end indices of this portion.	  
	 * 
	 */
	private static void createRunnables(int[] randomNumbers) {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, randomNumbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			runnables[aThreadIndex] = 
			 new OddNumbersWorkerCode(randomNumbers, aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}		
	}	
	
	/**
	 * This method determines how many elements of the input list, whose size is, 
	 * aProblemSize, will be processed by the thread whose index in the 
	 * thread array is aThreadIndex.
	 *   
	 * The problem may not be evenly divided among the threads. The thread index is
	 * used to determine which threads do extra work.
	 * 
	 */
	private static int threadProblemSize(int aThreadIndex, int aProblemSize) {
		// Following is the size if the problem can be eveny divided among the threads
		int aMinimumProblemSize = aProblemSize / NUM_THREADS;
		
		// This is the remaining work
		int aRemainder = aProblemSize % NUM_THREADS;
		
		return aMinimumProblemSize + 
			// calculate out how much of the remaining work is done by this thread
			fairThreadRemainderSize(aThreadIndex, aRemainder);
	}		
	
	/**
	 * The goal of this method, as its name suggests, is to divide aRemainder 
	 * items fairly among the available threads, that is,
	 * the differences in the sizes of the portions is as small as possible.
	 * 
	 * aRemainder is expected to be between 0 and NUM_THREADS - 1;
	 * 
	 * This method is buggy.
	 *
	 */
	private static int fairThreadRemainderSize(int aThreadIndex, int aRemainder) {
		if (aThreadIndex == 0) {
			return aRemainder;
		} else {
			return 0;
		}
	}
}

/**
 * This class represents the data  modified by
 * the worker threads that is output by the dispatcher Main thread. 
 * 
 * Thus, the dispatcher Main thread is the consumer of this information and 
 * the worker threads are the producers of it.
 */
class OddNumbersRepository {
	static List<Integer> oddNumbers;  // variable length output list
	static int totalNumberOddNumbers;
	
	/**
	 * The values must be reset before workers start modifying them. 
	 */
	public static void reset() {
		totalNumberOddNumbers = 0;
		oddNumbers = new ArrayList();
	}
	
	/**
	 * These two static methods are called by the dispatcher thread	
	 */
	public static List<Integer> getOddNumbers() {
		return oddNumbers;
	}	
	public static int getTotalNumberOddNumbers() {
		return totalNumberOddNumbers;
	}
	
	/**
	 * This is a static method that is called by the run methods of 
	 * all worker threads.
	 * 
	 * A method that can be executed by multiple threads is called a shared
	 * method.
	 * 
	 * Such a method can be synchronized or not based on whether
	 * it has the synchronized keyword in its header.
	 * 
	 * Only one synchronized static method of a class can be executed at any time
	 * by any thread.  
	 * The current synchronized method invocation locks the class.
	 * 
	 * Only one synchronized instance method of an object can be executed at any time
	 * by any thread. 
	 * The current synchronized method invocation locks the instance. 
	 * The same instance method can be executed on some other unlocked instance.  
	 * 
	 * Threads that try to execute synchronized methods in locked classes/instances 
	 * wait in queues until the classes/instances are unlocked.
	 * 
	 * Such waiting makes the code safe in that global variables (those not declared
	 * in the method) do not become inconsistent.
	 *
	 * Should this method be synchronized? The answer is yes.
	 * 
	 * To understand this answer, start by looking at the commented code below. 
	 * If you have not done so already, fix the problem with forkAndJoinThreads.
	 * Look at the output of this method and the final results? 
	 * Do they make	sense?
	 * 
	 * Next uncomment the first line in the header to make it synchronized, 
	 * and run the program again.
	 * 
	 * Again, look at the outputs above. Does this now make sense?
	 */
	// synchronized
	 static void incrementTotalOddNumbers() {
		// Here we are simulating register-based increments, if you do not know
		// how that works, think of aRegister as a temporary variable
		 
		int aRegister = totalNumberOddNumbers; // Simulate load memory to register
		printProperty("Loaded total number of odd numbers", totalNumberOddNumbers);
		
	
		// Before the incremented register is saved to memory, 
		// another concurrent thread may also load
		// the same value for totalNumberOddNumbers in its local register variable.
		// What problems can this cause if the method is not synchronized?	
				
		aRegister++; // increment register		
				
		ThreadSupport.sleep(10);
		// The above sleep call will block the current thread for 10 ms and let
		// some other thread execute before the save operation occurs
		// This call simulates a CPU switching execution to another thread.
		
		totalNumberOddNumbers = aRegister; // Simulate save register to memory
		printProperty("Saved total number of odd numbers", totalNumberOddNumbers);
	}	
	
	 static void addOddNumber(int aNumber) {
		incrementTotalOddNumbers();
		oddNumbers.add(aNumber); 
	}	
}

/**
 * Ignore this class, it has no concurrency concept
 */
class OddNumbersUtil {
	public static final int MAX_RANDOM_NUMBER = 1000;
	public static final int DEFAULT_INPUT_LENGTH = 6;
	static void printOddNumbers() {
		printProperty("Total Num Odd Numbers",  
				OddNumbersRepository.getTotalNumberOddNumbers());
		printProperty("Odd Numbers",
				OddNumbersRepository.getOddNumbers());
	}
	public static int firstArgToInteger(String[] args) {
		// If nothing was passed on the command line, then print error and exit
		
		if (args.length < 1) {
			System.err.println("No argument supplied to the main class, assuming default value of " + DEFAULT_INPUT_LENGTH);
			return DEFAULT_INPUT_LENGTH;
			//System.exit(0);;
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
	static int[] fillRandomNumbers(String[] args) {
		int aNumRandomNumbers = firstArgToInteger(args); // get the number from arguments
		int[] aRandomNumbers = generateRandomNumbers(aNumRandomNumbers);
		printProperty("Random Numbers", 
				Arrays.toString(aRandomNumbers));
		return aRandomNumbers;
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

	public static String threadName(Thread aThread) {
		return "Thread " + aThread.getId();
	}

	public static boolean isOddNumber(int aNumber) {
		return aNumber % 2 == 1;
	}	
	
}



