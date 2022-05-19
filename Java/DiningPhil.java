import java.lang.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.Random;
    
class DiningPhil {
    public static void main(String[] args) {

	Runnable phils = new Philosophers();
	Thread[] threads;
	threads = new Thread[5];
	for (int i = 0; i < 5; i++) {
	    threads[i] = new Thread(phils);
	    threads[i].start();
	}
    }
}

class Philosophers implements Runnable {
    private Lock lock;
    private int nextId = 0;
    private boolean[] fork;
    private Condition[] forkReady;

    Philosophers() {
	lock = new ReentrantLock();
	fork = new boolean[5];
	forkReady = new Condition[5];
	for (int i = 0; i < 5; i++) {
	    forkReady[i] = lock.newCondition();
	    fork[i] = true;
	}
    }
    
    private void getForks(int pid) {
	lock.lock();

	try {
	    
	    int left = pid;
	    int right = (pid+1) % 5;
	    
	    System.out.println("Grabbing forks (" + pid + ")...");	    
	    while (!fork[left]) {
		System.out.println("Left not available, waiting (" + pid + ")...");
		try {
		    forkReady[left].await();
		} catch (Exception ex) {
		    System.out.println("Await error");
		    ex.printStackTrace();
		    System.exit(1);
		}
	    }
	    System.out.println("Got left fork (" + pid + ")");
	    fork[left] = false;
	    
	    while (!fork[right]) {
		System.out.println("Right not available, waiting (" + pid + ")...");
		try {
		    forkReady[right].await();
		} catch (Exception ex) {
		    System.out.println("Await error");
		    ex.printStackTrace();
		    System.exit(1);
		}
	    }
	    System.out.println("Got right fork (" + pid + ")");
	    fork[right] = false;
	} finally {
	    lock.unlock();
	}
    }
    
    private void releaseForks(int pid) {

	lock.lock();
	
	try {	    
	    int left = pid;
	    int right = (pid+1) % 5;	    
	    System.out.println("Releasing forks (" + pid + ")...");
	   
	    fork[left] = true;
	    forkReady[left].signal();
	    fork[right] = true;
	    forkReady[right].signal();
	} finally {
	    lock.unlock();
	}
	
    }

    public void run() {
	int myId = 0;

	lock.lock();
	try {
	    myId = nextId;
	    nextId++;
	} finally {
	    lock.unlock();
	}

	Random rand = new Random();
	try {
	    while (true) {
		System.out.println("In deep thought (" + myId + ")...");
		Thread.sleep(rand.nextInt(2000));
		getForks(myId);
		System.out.println("Eating, yum (" + myId + ")...");
		Thread.sleep(rand.nextInt(2000));
		releaseForks(myId);
	    }
	} catch (Exception ex) {
	    System.out.println("Error in run");
	    ex.printStackTrace();
	}
    }
}
