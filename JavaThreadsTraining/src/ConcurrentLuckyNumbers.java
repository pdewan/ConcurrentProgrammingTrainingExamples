import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConcurrentLuckyNumbers {
	// all methods and variables accessed by static main must ne static
	
// START CONCURRENCY INDEPENDENT VARIABLES
	public static final int MAX_RANDOM_NUMBER = 1000;
	private static int[] randomNumbers; // input lust populated based on main aghument
	public static final int LARGE_ARRAY_SIZE = 30; // no prints if randomNumbers > LARGE_ARRAY_SIZE

	public static final int LUCKY_DIGIT = 7; // named constant, so can be public
	private static List<Integer> luckyNumbers = new ArrayList(); // variable length output list
	
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
		int aNumRandomNumbers = toInteger(args);
		randomNumbers = new int[aNumRandomNumbers];
		fillRandomNumbers();
//		fillLuckyNumbers(randomNumbers, 0, aNumRandomNumbers);
		concurrentFillLuckyNumbers();
		printProperty("Lucky Numbers", luckyNumbers);
		

	}

// START PROBLEM_SPECIFIC SEQUENTIAL CODE, WITH ONLY TWO INTERESTING METHODS
	// Such code can be given to the students of they can be first asked to
	// write it before their parallel assignment

	// A method like this occurs in all concurrent problems, defining the
	// input to the problem
	public static void fillRandomNumbers() {
		for (int index = 0; index < randomNumbers.length; index++) {
			double aRandomDouble = Math.random(); // number between 0 and 1
			int aRandomInteger = (int) (aRandomDouble * MAX_RANDOM_NUMBER);
			randomNumbers[index] = aRandomInteger;
		}
		// The prints in this solution help with debugging and they follow a
		// certain format to allow automatic testing.
		// They are essentially printing a par of values, the first one indicating
		// an external name of a -property (external variable) and the second one
		// indicating the value of the property. Thus, "Random Numbers" is the
		// external name of the input array. The names and type of of the problem
		// properties are determined by the problem specification and associated
		// testing code while the internal names and types are determined by the
		// implementation.
		printProperty("Random Numbers", Arrays.toString(randomNumbers));
	}

	// This is a a typical parallelizable loop for processing some part of the input
	// Again the nature of the prints are determined by the testing code

	public static void fillLuckyNumbers(int[] aNumbers, int aStartIndex, int aStopIndex) {
		int aNumberOfLuckyNumbers = 0;
		for (int index = aStartIndex; index < aStopIndex; index++) {
			printProperty("Index", index);
			int aNumber = aNumbers[index];
			printProperty("Number", aNumber);
			int aNumberToDigit = toSingleDigit(aNumber);
			printProperty("Digit", aNumberToDigit);
//			boolean isLucky = isLuckyNumber(aNumberToDigit);
			boolean isLucky = isOddNumber(aNumberToDigit);

			printProperty("Is Lucky", isLucky);
			if (isLucky) {
				addLuckyNumber(aNumber);
//				aNumberOfLuckyNumbers++; // uncomment this when asked
			}
		}

		printProperty("Num Lucky Numbers", aNumberOfLuckyNumbers);
	}

	// ignore the rest of the sequential code
	public static int toSingleDigit(int aNumber) {
		if (aNumber < 10) {
			return aNumber;
		}
		String aDigitSequence = Integer.toString(aNumber);
		return toSingleDigit(addDigits(aDigitSequence));
	}

	public static int addDigits(String aDigits) {
		int result = 0;
		for (int index = 0; index < aDigits.length(); index++) {
			char aCurrentChar = aDigits.charAt(index);
			String aCurrentString = Character.toString(aCurrentChar);
			int aCurrentInt = Integer.parseInt(aCurrentString);
			result += aCurrentInt;
		}
		return result;
	}

	public static boolean isLuckyNumber(int aDigit) {
		return aDigit == LUCKY_DIGIT;
	}
	
	public static boolean isOddNumber(int aNumber) {
		return aNumber % 2 == 1;
	}

//	synchronized // uncomment this line when asked
	public static void addLuckyNumber(int aNumber) {
		luckyNumbers.add(aNumber); // ArrayList is not thread safe

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
	
	private static void concurrentFillLuckyNumbers() {
		createRunnables();
		createAndStartThreads();
		joinThreads();
		
	}
	
	private static void createRunnables () {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, randomNumbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			workers[aThreadIndex] = new LuckyNumbersWorker(randomNumbers, 
					aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}
	}
	
	public static int threadProblemSize (int aThreadIndex, int aProblemSize) {
		// following is the size if the problem can be eveny divided among the threads
		int aMinimumProblemSize = aProblemSize/NUM_THREADS; 
		// some threads must take one of the remaining iterations
		int aRemainder = aProblemSize % NUM_THREADS; 
		// distribute the remainder among the first aRemainder threads
		if (aThreadIndex < aRemainder) {
			return aMinimumProblemSize + 1;
		}
		return aMinimumProblemSize;           
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
// END BOILER-PLATE FORK_JOIN CONCURRENT CODE
	
	
	
}

class LuckyNumbersWorker implements Runnable {
	int[] numbers;
	int startIndex, stopIndex;
	public LuckyNumbersWorker(int[] aNumbers, int aStartIndex, int aStopIndex) {
		numbers = aNumbers;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}
	@Override
	public void run() {
		// like sequential code except we do only our part of the problem
		ConcurrentLuckyNumbers.fillLuckyNumbers(numbers, startIndex, stopIndex);
	}
	
}
