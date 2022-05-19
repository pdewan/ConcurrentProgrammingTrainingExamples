import java.lang.*;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SerialPi {
    public static void main(String[] iters) {
	int numIter = 0;
	if (iters.length < 1) {
	    System.err.println("usage: Pi <iterations>");
	    System.exit(0);
	}
	try {
	    numIter = Integer.parseInt(iters[0]);
	} catch (Exception ex) {
	    System.err.println("Cannot convert argument on command line to integer");
	    System.exit(1);
	}

	SerialMonte monte = new SerialMonte(numIter);
	
	long s1 = System.nanoTime();

	monte.go();

	long s2 = System.nanoTime();
	
	double answer = 0;
	
	answer += monte.getInCircle();

	
	System.out.println("Serial time: " + (s2 - s1)/1000000000.0);
	System.out.println("Ratio is: " + 4*(answer/numIter));
	
    }
}

class SerialMonte {

    private double inCircle;
    private int iters;
    
    public void go() {   // will be run method for parallel version
	inCircle = findInside(iters);
    }

    public SerialMonte(int iterations) {
	iters = iterations;
    }

    public double getInCircle() {
	return inCircle;
    }
    
    private double findInside(int iterations) {
	ThreadLocalRandom rand = ThreadLocalRandom.current();
	int numIn = 0;
	int numOut = 0;
	for (int i = 0; i < iterations; i++) {
	    // get random number from 0 to 1
	    double x = rand.nextDouble();
	    double y = rand.nextDouble();
	    double hyp = Math.sqrt(x*x + y*y);
	    if (hyp < 1.0) {
		numIn++;
	    }
	}
	return numIn;
     }
    
}

