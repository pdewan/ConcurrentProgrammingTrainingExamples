import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trace.grader.basics.GraderBasicsTraceUtility;
import util.misc.ThreadSupport;


/*
 * The functional goal of the program is to take as an argument an integer N, 
 * generate an array of N random numbers, find the odd numbers in this array, 
 * and print a list of the odd numbers as well as the total number of odd numbers. 
 * 
 * The algorithmic goal of the program is to create a multithreaded  
 * concurrent "fork-join" program. In such a program, a "dispatcher" "thread"
 * divides the given functional task "fairly" among child "worker" threads it "forks",
 * wait for these threads to finish, and then print the results stored in data
 * structure shared and modified by the worker threads.
 * 
 * The goal of this exercise is to use a "hands-on" experimental approach
 * to increase your understanding of the quoted terms. The exercise involves reading
 * the commented concurrency-aware code, that is, code has to do with concurrency,
 * finding and fixing its bugs using the supplied tests, submitting the modified code
 * to Gradescope, and taking a Gradescope quiz to indicate how much you learned 
 * from this instructor-less training. The quiz will count as class Q&A - each
 * question you attempt will automatically be considered as a Q&A entry - it does not
 * matter if you get it correct. The gradescope code submission will count as a small
 * 20 point assignment. Thus, this is hopefully a low stake, high learning exercise.
 *
 * 
 * The code is divided into four classes. ConcurrentOddNumbers and Concurrent Util;
 * are concurrency-unaware. OddNumbersWorkerCode and OddNumbersDispatcherCode are
 * concurrency-unaware.
 * 
 * The code has three bugs in the following methods:
 * 1. forkAndJoinThreads in class OddNumbersDispactherCode .
 * 2. fairThreadRemainderSize in class fairThreadRemainderSize.
 * 3. incrementTotalOddNumbers in OddNumbersSharedRepository
 * 4. addOddNumber()
 *
 * 
 * The concurrency-are classes are deliberately buggy in that they partly meet the 
 * algorithmic goal. To remove these bugs, the following process is recommended:
 * 
  
 * 1. Read the commented code in the classes.
 * 2. (Re)Identify code that is inconsistent with the algorithmic goal. The methods to 
 *    scrutinize for bugs are explicitly pointed out.
 * 3. Fix the code if possible.
 * 4. Test the code. 
 * 5. If the tests pass go to 9..
 * 6. View hints given by the output of the program and the tests
 * 7. Go to 2.
 * 8. Submit code to G_Assignment 0_1 by start of next class.
 * 9. Take Gradescope Quiz 1 by start of next class.
 *
 */
/**
 * The ConcurrentOddNumbers class does not have any code that needs to be scrutinized
 * for bugs. But do read the comment above the main method to understand the
 * concept of thread you have been using already, even in serial programs.
 */
public class ConcurrentOddNumbers {
	static int[] randomNumbers; // input list populated based on main argument
	/**
	 * The main() method of a class is  executed by a Java created "thread", called the
	 * Main thread, when you ask the JVM (Java virtual machine or Java Interpreter)
	 * to run the class. 
	 * 
	 * All methods called directly or indirectly by the main() method are executed
	 * by this thread. 
	 * 
	 * The Main thread starts when when main() is called and terminates
	 * when main() ends. In between the start and stop, a stack of calls can be serviced
	 * by the Main thread. This stack grows and shrinks, as different methods are called,
	 * In this example, for instance, some example stack snapshots are: 
	 * ConcurrentOddNumbers.main()->OddNumnbersUtil.fillRandomNumbers
	 * ConcurrentOddNumbers.main()->OddNumnbersUtil.fillRandomNumbers->OddNumbersUtil.generateRandomNumbers 
	 * ConcurrentOddNumbers.main()-->ConcurrentOddNumbers.fillOddNumbers()
	 
	 * All stack snapshots serviced by a thread have the same method at the root of the
	 * stack, which we will call the root method of the thread. The root method of 
	 * Main thread is the main() method.
	 * 
	 * A thread is an independent unit of steps in a program
	 * that can be executed while other threads are executing. Each thread is
	 * associated with its own stack.
	 * 
	 * In the physical world, a  thread corresponds to an actor and its root method
	 * is the script followed by the actor.
	 * 
	 * The word "main" here is being used both for the active agent, Main thread, 
	 * and the code it executes, main(). It is important to keep this distinction,
	 * as students often think of a method or the class containing it as the thread.
	 * 
	 * Just as a script can be executed by multiples actors at the same or different 
	 * times, the main() method can be executed can be executed by multiple threads
	 * at the same or different times.
	 * 
	 * Each time you execute a main class, a different Main thread is created to
	 * execute the main() method.
	 * 
	 * More interestingly, a non Main thread can also call main(). 
	 * 
	 * This is what happens when you run our tests.	 * 
	 * The testing thread calls your main() method, sometimes multiple times, 
	 * with different arguments.
	 * 
	 * In this example, any thread that executes this main() method will be called
	 * a dispatcher or forker thread as its job is to dispatch work to worker
	 * threads it creates. 
	 * 	
	 *
	 */
	public static void main(String[] args) {
		// Ask the util class to generate a sequence of random numbers
		randomNumbers = OddNumbersUtil.fillRandomNumbers(args); 
		// Ask the dispatcher class to "fork" and "join" child threads to identify 
		// which of the  generated numbers are odd		
		OddNumbersDispatcherCode.fillOddNumbers(randomNumbers);
		// Ask the util class print the odd numbers
		OddNumbersUtil.printOddNumbers(); 
	}
}
/**
 * As we saw above, the Main thread is created automatically by the JVM.
 * 
 * What if an existing thread wants to create new threads, which is required in the
 * fork-join and other forms of concurrency?
 * 
 * In Java, a non Main thread must execute a parameterless instance 
 * method called run() in a  class that implements the Java Runnable interface.
 * 
 * The following is such a Runnable class, which represents the code executed by
 * each worker thread.
 * 
 * Different worker threads are bound to different 
 * Runnable instances of this class. 
 * 
 * This binding is done by the dispatcher class defined later.
 * 
 */
class OddNumbersWorkerCode implements Runnable {
	int[] inputList;
	int startIndex, stopIndex;
	/**
	 * The run() method does not take parameters, so any parameters needed by it are
	 * passed as parameters to the constructor(s) of the enclosing Runnable class.
	 * 
	 * In this example, these indicate the list of generated random numbers,
	 * and the portion of this list that this instance must process.	 *
	 */
	public OddNumbersWorkerCode(int[] anInputList, int aStartIndex, int aStopIndex) {
		inputList = anInputList;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}
	
	/**
	 * This is an instance method that becomes the root method of a new thread.
	 * It is executed when we start an associated thread. How to associate it
	 * with a new thread is shown in the next class.
	 */
	@Override
	public void run() {
		// The run method just delegates work to a method in the Util class.
		fillOddNumbers(inputList, startIndex, stopIndex);
	}	
	/** 
	 * The code executed by this method is a typical iteration loop executed
	 * by worker threads. It finds all odd numbers in the subsequence of the
	 * anInputList between whose indices are >= aStartIndex and < aStopIndex. 
	 * 
	 * It can be called by the Main thread of a serial program or the run
	 * method above. That is why it is static.
	 * 
	 * Of most interest is the call OddNumbersRepository.addOddNumber(aNumber);
	 */	
	public static void fillOddNumbers(int[] anInputList, 
					int aStartIndex, int aStopIndex) {
		int aNumberOfOddNumbers = 0;		
		for (int index = aStartIndex; index < aStopIndex; index++) {
			GraderBasicsTraceUtility.printProperty("Index", index);
			int aNumber = anInputList[index];
			GraderBasicsTraceUtility.printProperty("Number", aNumber);
			boolean isOdd = OddNumbersUtil.isOddNumber(aNumber);
			GraderBasicsTraceUtility.printProperty("Is Odd", isOdd);
			if (isOdd) {
				OddNumbersRepository.addOddNumber(aNumber);
				aNumberOfOddNumbers++; 
			}			
		}
		GraderBasicsTraceUtility.printProperty("Num Odd Numbers", aNumberOfOddNumbers);
	}
}

/**
 * This class contains code contains "dispatching" code executed by main().
 * This code creates the worker threads, associates them
 * with Runnable instances, starts them, waits for them to finish, 
 * and print the results.
 *
 */
class OddNumbersDispatcherCode {	
	
	public static final int NUM_THREADS = 4; // number of forked threads is fixed
	
	// A thread is represented in Java by an instance of the Thread class.
	// So this array keeps track of the worker threads created.
	private static Thread[] threads = new Thread[NUM_THREADS];
	
	// These are the runnable instances to be bound to the threads above.
	// There is a one to one correspondence between a thread and a runnable
	private static Runnable[] runnables = 
			new Runnable[NUM_THREADS]; // code executed by each thread 
	
	
	static void fillOddNumbers(int[] anInputList) {
		OddNumbersRepository.reset();
		OddNumbersDispatcherCode.concurrentFillOddNumbers(anInputList);
	}
	
	public static void concurrentFillOddNumbers(int[] anInputList) {
		createRunnables(anInputList); // First create the runnables
		forkAndJoinThreads(); // Next create, start, and join threads
	}
	
	/**
	 * This method illustrates how an existing running thread can
	 * create/fork a new "child" thread, start it, and wait for it to finish.
	 * 
	 * The forking dispatcher thread executes this method.
	 *  
	 * The goal of this method to make a set of worker threads execute concurrently,
	 * one for each Runnable instance, and then make the forking dispatcher thread 
	 * wait for all	of them to finish. 
	 * 
	 * The method is buggy in that it does not quite meet this goal. You need to
	 * find and fix the bug(s) in it.	 * 
	 * 
	 */
	private static void forkAndJoinThreads() {
		for (int index = 0; index < runnables.length; index++) {			
			// The call new Thread(aRunnable) creates a new thread object and
			// gives it its work by binding it to aRunnable at the same index.
			threads[index] = new Thread(runnables[index]);
			
			// The call t.start() starts the thread represented by t, that is, 
			// creates a new stack, and executes the root instance method of the 
			// bound Runnable instance bound to t.
			
			// This new thread can executed concurrently with other started threads
			// and the calling thread, that is, the thread that called t.start,
			// which in this case is the dispatcher thread. 
			threads[index].start(); 
			try {
				// t.join blocks the Main thread until t finishes,
				// that is, stops the caller from executing the next instruction
				// until t finishes executing its run method.
				
				// Thus, this call reduces the concurrency in the system, as it
				// makes the joining thread wait until the joined thread t finishes. 
				// In other words, even though the joining thread exists concurrently
				// with the joined thread, it is not allowed to execute concurrently 
				// with it. 
				
				// If t has finished executing when t.join() is called, the call
				// does not block the joining thread.
				
				// A thread can join multiple threads, one at a time, by
				// making a series of such calls on each of these threads.
				threads[index].join(); 
			} catch (InterruptedException e) {
				// The joined thread may be interrupted while the joiner thread
				// is waiting for it to finish. If this happens the joiner
				// unblocks, and services an InterruptedException.
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method decomposes the work of processing the random numbers 
	 * into a bunch of Runnable instances, one for each worker thread
	 * to be started. These runnables are stored in a global variables
	 * accessed by the forkAndJoinThreads method.
	 * 
	 * A decomposed work unit is a subsequence of the input random number sequence. 
	 * It is represented by the start and end indices of this portion.
	 * 
	 * The goal of this method is to create runnables for non-overlapping portions 
	 * of the input sequence while ensuring that (a) the portions completely cover
	 * the input, and (b) the division is fair, that is, the differences in the
	 * sizes of the portions is as small as possible.
	 * 
	 * It uses the method threadProblemSize to meet these constraints.
	 *
	 * 
	 */
	private static void createRunnables(int[] randomNumbers) {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, randomNumbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			runnables[aThreadIndex] = new OddNumbersWorkerCode(randomNumbers, aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}		
	}
		
	/**
	 * This method determines how many elements of the input list, whose size is, 
	 * aProblemSize, will be processed by the thread whose index in the global in the 
	 * thread array is aThreadIndex.
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
			fairThreadRemainderSize(aThreadIndex, aRemainder);
	}		
	
	/**
	 *  The goal of this method, as its name suggests, is to divide aRemainder items
	 *  work fairly among the available threads.
	 *  
	 *  This method is buggy given the definition of fairness given above.	
	 *  
	 *  Your task is to fix it.
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
 * This class represents data shared among the Main (dispatching) and worker threads.
 * It represents the data output by the Main thread that is modified by the worker
 * threads. Thus, the dispatching Main thread is the consumer of this information and 
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
	 * The Main thread calls this method to print the
	 */
	public static List<Integer> getOddNumbers() {
		return oddNumbers;
	}	
	
	public static int getTotalNumberOddNumbers() {
		return totalNumberOddNumbers;
	}
	/**
	 * This is a static method that is called by the run methods of all worker threads.
	 * A method that can be executed by multiple threads is called a shared
	 * method.
	 * 
	 * Such a method can be synchronized or not based on whether
	 * it has the synchronized keyword in its header.
	 * 
	 * Only one synchronized static method of a class can be executed at any time
	 * by any thread. The current synchronized method invocation locks the class.
	 * 
	 * Only one synchronized instance method of an object can be executed at any time
	 * by any thread. The current synchronized method invocation locks the instance. 
	 * The same instance method can be executed on some other unlocked instance.  
	 * 
	 * Threads that try to execute synchronized methods in locked classes/ 
	 * instances wait in queues	until the classes/instances are unlocked.
	 * 
	 * Such waiting makes the code safe in that global variables (those not declared
	 * in the method) do not become inconsistent.
	 * 
	 * Such waiting also slows down computation. So we should synchronize as little code 
	 * as possible.
	 * 
	 *
	 * Shared Methods that do not modify global variables directly should not be 
	 * be synchronized. 
	 * 	 * 
	 * If they call unsafe methods then those methods should be synchronized.	 * 
	 * We can synchronize the calling method, but that will synchronize not only 
	 * the unsafe called methods but also other safe code executed by the caller method.
	 * Sometimes the called methods are provided by a library such as Arraylist
	 * in this example. In that case, we should indeed synchronize the calling method.
	 * 
	 * Should this method be synchronized? The answer is yes.
	 * 
	 * To understand this answer, start by looking at the commented code below. 
	 * If you have not done so already, fix the problem with forkAndJoinThreads.
	 * Look at the output of this method and the final results? Do they make
	 * sense?
	 * 
	 * Next uncomment the first line in the header, and run the program again.
	 * Again, look at the outputs above.
	 */
	// synchronized
	 static void incrementTotalOddNumbers() {
		// Here we are simulating register-based increments, if you do not know
		// how that works, think of aRegister as a temporary variable
		int aRegister = totalNumberOddNumbers; // Simulate load memory to register
		GraderBasicsTraceUtility.printProperty("register", aRegister);
		// Before the register is incremented, another concurrent thread may also load
		// the same value for totalNumberOddNumbers in its local register variable.
		// What problems can this cause if the method is not synchronized?  
		//	
		// Will this problem exist if the current version of forkAndJoinThreads?
		aRegister++; // increment register
		GraderBasicsTraceUtility.printProperty("register", aRegister);
		totalNumberOddNumbers = aRegister; // Simulate save register to memory
		
	}
	
	// The add method in class ArrayList is not synchronized.
	// It is not safe to execute this method if it is unsynchronized even
	// if incrementTotalOddNumbers is synchronized.
	// 
	// Why? 
	// 
	// Hint:  In an ArrayList an array is used to store the list of	elements. 
	// Assuming the array is big enough, add(newElement() assigns newElement to 
	// the first unfilledarray slot, and changes the size of the filled 
	// part of the array. This is shown below:	
	//
	// array[numFilledElements] = newElement;
	// numFilledElements++;
	// 
	// Why is it important to put this code in a synchronized method
	 
	 //synchronized
	 static void addOddNumber(int aNumber) {
		incrementTotalOddNumbers();
		oddNumbers.add(aNumber); // ArrayList is also not thread safe
	}
	
}


class OddNumbersUtil {
	public static final int MAX_RANDOM_NUMBER = 1000;
	static void printOddNumbers() {
		GraderBasicsTraceUtility.printProperty("Total Num Odd Numbers",  
				OddNumbersRepository.getTotalNumberOddNumbers());
		GraderBasicsTraceUtility.printProperty("Odd Numbers",
				OddNumbersRepository.getOddNumbers());
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
	static int[] fillRandomNumbers(String[] args) {
		int aNumRandomNumbers = firstArgToInteger(args); // get the number from arguments
		int[] aRandomNumbers = generateRandomNumbers(aNumRandomNumbers);
		GraderBasicsTraceUtility.printProperty("Random Numbers", 
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


	public static boolean isOddNumber(int aNumber) {
		return aNumber % 2 == 1;
	}
	
	
}



