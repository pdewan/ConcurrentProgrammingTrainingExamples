import java.util.Arrays;

import trace.grader.basics.GraderBasicsTraceUtility;


public class SingleHelloForkJoin {
	public static void main (String[] args) {
		System.out.println("Hello Concurrent World");

//		Thread aWorker = new Thread(new SingleHelloWorker());
//		aWorker.start();
//		try {
//			aWorker.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
	}
}

class SingleHelloWorker implements Runnable {
	
	@Override
	public void run() {
		System.out.println("Hello Concurrent World");
	}
	
}

