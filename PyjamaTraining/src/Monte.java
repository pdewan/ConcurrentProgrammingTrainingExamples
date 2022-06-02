import java.lang.*;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Monte {
	public static void main(String[] args) {
		int numIters = 0;
		try {
			numIters = Integer.parseInt(args[0]);
		} catch (Exception ex) {
			System.err.println("Exception when parsing argument");
			System.exit(1);
		}

		int numIn = 0;
		int numOut = 0;
		//#omp parallel num_threads(4) shared(numIters) reduction(+:numIn) reduction(+:numOut)
		{
			int myIters = numIters;
			ThreadLocalRandom rand = ThreadLocalRandom.current();
			numIn = 0;
			numOut = 0;
			//#omp for
			for (int i = 0; i < myIters; i++) {
				// get random number from 0 to 1
				double x = rand.nextDouble();
				double y = rand.nextDouble();
				double hyp = Math.sqrt(x*x + y*y);
				if (hyp < 1.0) {
					numIn++;
				} else {
					numOut++;
				}
			}
		}
		float p = (((float)numIn)/(numIn+numOut));
		float fourp = 4*p;
		System.out.println("Pi is " + String.valueOf(fourp));
	}
}
