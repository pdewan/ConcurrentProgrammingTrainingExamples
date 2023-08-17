package tests;

import java.io.File;

import gradingTools.comp533s20.assignment7.Assignment7Suite;
import gradingTools.comp533s21.assignment7.S21Assignment7Suite;
import gradingTools.javaThreads.hello.ConcurrentHelloSuite;
import gradingTools.javaThreads.primes.ConcurrentPrimesSuite;
import trace.grader.basics.GraderBasicsTraceUtility;
import util.trace.Tracer;

public class ConcurrentPrimesTests {
	public static void main(String[] args) {
//		File aFile = new File(".");
//		if (aFile.exists()) {
//			System.out.println("File:" + aFile.getAbsolutePath());
//		}
		Tracer.showInfo(true);
		
		GraderBasicsTraceUtility.setBufferTracedMessages(false);
//		Tracer.setMaxTraces(8000);
//		Assignment7Suite.main(args);
		String[] mainArgs = {"Primes"};
		ConcurrentPrimesSuite.main(mainArgs);
	}
}
