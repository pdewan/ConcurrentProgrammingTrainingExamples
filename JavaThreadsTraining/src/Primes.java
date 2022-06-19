import java.lang.*;
import java.lang.Math;
import java.util.Random;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Primes {
	public static final int NUM_THREADS = 4;

	public static void main(String[] args) {

		int numItems = 0;
		if (args.length < 1) {
			System.err.println("usage: Primes <number_of_items>");
			System.exit(0);
		}
		try {
			numItems = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			System.err.println("Cannot convert argument on command line to integer");
			System.exit(1);
		}

		Runnable runnable = new PrimesHelper(numItems);
		Thread[] threads = new Thread[4];
//	Thread[] threads = new Thread[numItems];

		for (int i = 0; i < NUM_THREADS; i++) {

			threads[i] = new Thread(runnable);
			threads[i].start();
		}
		int answer = 0;

		for (int i = 0; i < NUM_THREADS; i++) {
			try {
				threads[i].join();
				int aThreadAnswer = ((PrimesHelper) runnable).getAnswer(i);
				answer += aThreadAnswer;
//				answer += ((Counter) runnable).getAnswer(i);
//				System.out.println("Thread:" + threads[i].getId() + " Num Primes:" + aThreadAnswer);
			} catch (Exception ex) {
				System.err.println("Error while waiting for thread " + i);
			}
		}
//		System.out.println("Root Thread->Num Primes:" + answer);
		PrimesHelper.printProperty("Num Primes", answer);
	}
	
}

class PrimesHelper implements Runnable {

	public static int[] intArray;
	private int nextId = 0;
	private int[] answers;

	PrimesHelper(int numItems) {
		intArray = new int[numItems];
		Random rand = new Random();
		for (int i = 0; i < numItems; i++) {
			intArray[i] = rand.nextInt(50);
		}
		printProperty("Random Numbers", Arrays.toString(intArray));
//		if (isSmallProblem()) {
//			System.out.println("Root Thread->Random Numbers:" + Arrays.toString(intArray));
//		}

		answers = new int[] { 0, 0, 0, 0 };
	}

	public void run() {
		int myId = 0;
		synchronized (this) {
			myId = nextId;
			nextId++;
		}
		int howBig = intArray.length / Primes.NUM_THREADS;
		int myStart = myId * howBig;
		int myEnd = myStart + howBig - 1;

		if (myId == 3) {
			myEnd = intArray.length - 1;
		}
//		System.out.println(threadPrefix() + " Start Index:" + myStart );
//		System.out.println("Thread:" + Thread.currentThread().getId()  +  " End Index:" + myEnd);

		for (int i = myStart; i <= myEnd; i++) {
			printProperty("Index", i);
			printProperty("Number", intArray[i]);
			boolean isPrime = isPrime(intArray[i]);
			printProperty("Is Prime", isPrime);

			if (isPrime) {
				answers[myId]++;
			}
		}
		printProperty("Num Primes", answers[myId]);
	}

	public static boolean isPrime(int num) {
		if (num == 2)
			return true;

		for (int i = 2; i <= Math.sqrt(num); i++) {
			if ((num % i) == 0) {
				return false;
			}
		}

		return true;
	}

	int getAnswer(int idx) {
		return answers[idx];
	}

	public static final int LARGE_PROBLEM_SIZE = 25;

	static boolean isSmallProblem() {
		return intArray.length < LARGE_PROBLEM_SIZE;
	}

	static void printProperty(String aPropertyName, Object aPropertyValue) {
		if (isSmallProblem()) {
			System.out.println(threadPrefix() + aPropertyName + ":" + aPropertyValue);
		}
	}

	public static String threadPrefix() {
		return "Thread " + Thread.currentThread().getId() + "->";
	}

}
