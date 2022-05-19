import java.lang.*;
import java.lang.Math;
import java.util.Random;

// Note:  This example uses a Counter object so that it is easily converted to
//        a parallel version (Counter should implement Runnable)
public class SerialPrimes {
    public static void main(String[] args) {
	
	int numItems = 0;
	if (args.length < 1) {
	    System.err.println("usage: SerialPrimes <number_of_items>");
	    System.exit(0);
	}
	try {
	    numItems = Integer.parseInt(args[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}	

	SerialCounter counter = new SerialCounter(numItems);
	counter.count();
	System.out.println("The number of primes is: " + counter.getAnswer());
    }
}


class SerialCounter {
    public int[] intArray;
    private int answer = 0;

    SerialCounter(int numItems) {
	intArray = new int[numItems];
        Random rand = new Random();
        for (int i = 0; i < numItems; i++) {
            intArray[i] = rand.nextInt(50);
        }

    }

    public void count() {
	answer = 0;
        for (int i : intArray) {
	    System.out.print(i);
	    if (isPrime(i)) {
		System.out.println(" is Prime");
		answer++;
	    } else {
		System.out.println(" is NOT Prime");
	
	    }
        }
    }

    boolean isPrime(int num) {
	if (num == 2) return true;

	for (int i = 2; i <= Math.sqrt(num); i++) {
	    if ((num % i) == 0) {
		return false;
	    }
	}

	return true;
    }

    int getAnswer() {
	return answer;
    }

}

