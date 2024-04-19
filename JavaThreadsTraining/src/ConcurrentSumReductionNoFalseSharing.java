import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// mucking around in this line now yet another string

public class ConcurrentSumReductionNoFalseSharing {
	private static int[] numbers = {2, 3, 4, 5, 6, 7};
	public static final int NUM_FORKED_THREADS = 2; 
	public static final int NUM_TOTAL_THREADS = NUM_FORKED_THREADS + 1; 
	private static Thread[] threads = new Thread[NUM_FORKED_THREADS];
	private static IntProducingRunnable[] runnables = new IntProducingRunnable[NUM_TOTAL_THREADS]; 
	private static int[] localResults = new int[NUM_TOTAL_THREADS];
	public static void main(String[] args) {
		System.out.println("Thread " + Thread.currentThread().getId() + ": Processing numbers " + Arrays.toString(numbers));
		createRunnables();
		reducingForkJoin();
	}
	static void reducingForkJoin() {
		for (int aThreadIndex = 0; aThreadIndex < threads.length; aThreadIndex++) {
			forkThread(aThreadIndex);
		}
		runnables[NUM_FORKED_THREADS].run();		 
		for (int aThreadIndex = 0; aThreadIndex < threads.length; aThreadIndex++) {
			joinThread(aThreadIndex);
		}
		for (int aThreadIndex = 0; aThreadIndex < NUM_TOTAL_THREADS; aThreadIndex++) {
			localResults[aThreadIndex] = runnables[aThreadIndex].getResult();
		}
		int aFinalResult = SumUtil.doReduction(localResults, 0, localResults.length);		
		System.out.println("Thread " + Thread.currentThread().getId() + ": Final reduced result " + aFinalResult);		
	}
	private static void forkThread(int aThreadIndex) {
		Thread aNewThread = new Thread(runnables[aThreadIndex]);
		threads[aThreadIndex] = aNewThread;
		aNewThread.start();
	}
	private static void joinThread(int aThreadIndex) {
		try {
			threads[aThreadIndex].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	protected static void createRunnables() {
		int aStartIndex = 0;
		for (int aThreadIndex = 0; aThreadIndex < NUM_TOTAL_THREADS; aThreadIndex++) {
			int aProblemSize = threadProblemSize(aThreadIndex, numbers.length);
			int aStopIndex = aStartIndex + aProblemSize;
			runnables[aThreadIndex] = new SumRunnable(numbers, aStartIndex, aStopIndex);
			aStartIndex = aStopIndex; // next thread's start is this thread's stop
		}
	}

	private static int threadProblemSize(int aThreadIndex, int aProblemSize) {
		int aMinimumProblemSize = aProblemSize / NUM_TOTAL_THREADS;
		// This is the total remaining work to be divided fairly among the worker
		// threads
		int aRemainder = aProblemSize % NUM_TOTAL_THREADS;
		// Calculate out how much of the remaining work is done by this thread
		// This is the part of the total remainder assigned to this thread
		int aThreadRemainder = fairThreadRemainderSize(aThreadIndex, aRemainder);
		return aMinimumProblemSize + aThreadRemainder;
	}

	private static int fairThreadRemainderSize(int aThreadIndex, int aRemainder) {
		if (aThreadIndex < aRemainder) {
			return 1;
		} else {
			return 0;
		}
	}
}

interface IntProducingRunnable extends Runnable {
	int getResult();
}

class SumRunnable implements IntProducingRunnable {
	int[] inputList;
	int startIndex, stopIndex;
	int result;
	public SumRunnable(int[] anInputList, int aStartIndex, int aStopIndex) {
		inputList = anInputList;
		startIndex = aStartIndex;
		stopIndex = aStopIndex;
	}
	@Override
	public void run() {
		result = SumUtil.doReduction(inputList, startIndex, stopIndex);
	}
	public int getResult() {
		return result;
	}
}

class SumUtilNoFalseSharing {
	public static int doReduction(int[] anInputList, int aStartIndex, int aStopIndex) {
		int result = 0;
		for (int anIndex = aStartIndex; anIndex < aStopIndex; anIndex++) {
			result += anInputList[anIndex];
			System.out.println("Thread " + Thread.currentThread().getId()  + ": added item " + anInputList[anIndex] + "(" + anIndex + ") to create local result " + result );

		}
		return result;
	}
}
