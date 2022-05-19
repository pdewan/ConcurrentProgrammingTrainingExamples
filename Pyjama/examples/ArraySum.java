import java.lang.*;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ArraySum {
	public static void main(String[] args) {
		int numThreads = 0;
		int numItems = 0;
		if (args.length < 2) {
			System.err.println("usage: Hello <numThreads> <numItems>");
			System.exit(0);
		}
		try {
			numThreads = Integer.parseInt(args[0]);
			numItems = Integer.parseInt(args[1]);
		} catch (Exception ex) {
			System.err.println("Bad argument");
			System.exit(1);
		}
		double[] a = new double[numItems];

		fillArray(a);

		double sum = 0;
		
		//#omp parallel for shared(a) num_threads(numThreads) reduction(+:sum)
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		System.out.println("Sum is " + String.valueOf(sum));
	
	}
	private static void fillArray(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.random() * 100;
		}
	}
}

