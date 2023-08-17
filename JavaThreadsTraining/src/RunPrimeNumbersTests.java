
import java.io.File;
import grader.basics.execution.BasicProjectExecution;
import gradingTools.comp533s20.assignment7.Assignment7Suite;
import gradingTools.comp533s21.assignment7.S21Assignment7Suite;
import gradingTools.javaThreads.PrimeNumbersSuite;
import gradingTools.javaThreads.JavaThreadsSuite;
import gradingTools.javaThreads.hello.ConcurrentHelloSuite;
import gradingTools.javaThreads.luckyNumbers.ConcurrentLuckyNumbersSuite;
import gradingTools.javaThreads.oddNumbers.ConcurrentOddNumbersSuite;
import gradingTools.javaThreads.primes.ConcurrentPrimesSuite;
import trace.grader.basics.GraderBasicsTraceUtility;
import util.trace.Tracer;

public class RunPrimeNumbersTests {
	public static void main(String[] args) {
		
//		// if you want to step through local checks calls to your code
//		// set this to false
//		BasicProjectExecution.setUseMethodAndConstructorTimeOut(true);
		
		// if you set this to false, grader steps will not be traced
		GraderBasicsTraceUtility.setTracerShowInfo(false);
		
//		// if you set this to false, all grader steps will be traced,
//		// not just the ones that failed
//		GraderBasicsTraceUtility.setBufferTracedMessages(true);
//		
//		// Change this number if a test trace gets longer than 600 and is clipped
//		GraderBasicsTraceUtility.setMaxPrintedTraces(600);
//		
//		// Change this number if all traces together are longer than 2000
//		GraderBasicsTraceUtility.setMaxTraces(2000);
		
//		// Change this number if your process times out prematurely
//		BasicProjectExecution.setProcessTimeOut(5);
//		
		// You need to always call such a method
		PrimeNumbersSuite.main(args);
	}
}
