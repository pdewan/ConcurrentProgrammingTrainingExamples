import java.lang.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.Random;

public class ProdConDriver {
    public static void main(String[] args) {

	ProdConBuffer pcbuf = new ProdConBuffer();
        Runnable producer = new Producer(pcbuf);
	Runnable consumer = new Consumer(pcbuf);
        Thread prodThread = new Thread(producer);
	Thread conThread = new Thread(consumer);
	prodThread.start();
	conThread.start();
    }

}

class Producer implements Runnable {
    private ProdConBuffer pcbuffer;

    Producer(ProdConBuffer pcbuf) {
	pcbuffer = pcbuf;
    }
    
    public void run() {
	Random rand = new Random();
	while (true) {
	    int x = rand.nextInt(1000);
	    try {
		Thread.sleep(x);
	    } catch (Exception ex) {
		System.out.println("Error while sleeping");
		System.exit(1);
	    }
	    System.out.println("+" + x);
	    pcbuffer.append(x);
	}
    }
}

class Consumer implements Runnable {
    private ProdConBuffer pcbuffer;
    
    Consumer(ProdConBuffer pcbuf) {
	pcbuffer = pcbuf;
    }

    public void run() {
	Random rand = new Random();
	while (true) {
	    int x = pcbuffer.take();
	    System.out.flush();
	    int workTime = rand.nextInt(2000);
	    try {
		Thread.sleep(workTime);
	    }
	    catch (Exception ex) {
		System.out.println("Error while sleeping");
		System.exit(1);
	    }
	    System.out.println("-" + x);
	}
    }   
}

class ProdConBuffer {
    private int[] buffer;
    private int nextIn = 0, nextOut = 0, count = 0;
    private Condition notFull, notEmpty;

    private Lock lock;
    
    ProdConBuffer() {
	buffer = new int[10];
	lock = new ReentrantLock();
	notFull = lock.newCondition();
	notEmpty = lock.newCondition();
    }

    void append(int x) {
	lock.lock();
	try {
	    while (count == 10) {
		try {
		    notFull.await();
		} catch (Exception ex) {
		    System.out.println("append: Error waiting");
		    System.exit(2);
		}
	    }
	    buffer[nextIn] = x;
	    nextIn = (nextIn + 1) % 10;
	    count++;
	    notEmpty.signal();
	} finally {
	    lock.unlock();
	}
    }

    int take() {
	lock.lock();
	int x;
	try {
	    while (count == 0) {
		try {
		    notEmpty.await();
		} catch (Exception ex) {
		    System.out.println("take: Error waiting");
		    System.exit(2);
		}
	    }
	    x = buffer[nextOut];
	    nextOut = (nextOut + 1) % 10;
	    count--;
	    notFull.signal();
	} finally {
	    lock.unlock();
	}
	return x;
    }
}
