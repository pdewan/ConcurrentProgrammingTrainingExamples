package tests;

import gradingTools.javaThreads.luckyNumbers.ConcurrentLuckyNumbersSuite;
import trace.grader.basics.GraderBasicsTraceUtility;
import util.trace.Tracer;

public class ConcurrentLuckyNumbersTests {
	public static void main(String[] args) {
//		File aFile = new File(".");
//		if (aFile.exists()) {
//			System.out.println("File:" + aFile.getAbsolutePath());
//		}
		Tracer.showInfo(true);
		
		GraderBasicsTraceUtility.setBufferTracedMessages(false);
//		Tracer.setMaxTraces(8000);
//		Assignment7Suite.main(args);
		String[] mainArgs = {"ConcurrentLuckyNumbers"};
		ConcurrentLuckyNumbersSuite.main(mainArgs);
	}
}
