import grader.basics.execution.BasicProjectExecution;
import gradingTools.comp524f23.assignment0_1.F23Assignment0_1Suite;
import gradingTools.javaThreads.JavaThreadsSuite;
import trace.grader.basics.GraderBasicsTraceUtility;

public class RunAssignment0_1_Tests {
	public static void main(String[] args) {
		
//		// if you want to step through local checks calls to your code
//		// set this to false
//		BasicProjectExecution.setUseMethodAndConstructorTimeOut(false);
		
		// if you set this to false, grader steps will not be traced
		GraderBasicsTraceUtility.setTracerShowInfo(false);
		
//		// if you set this to false, all grader steps will be traced,
//		// not just the ones that failed
		GraderBasicsTraceUtility.setBufferTracedMessages(true);
//		
//		// Change this number if a test trace gets longer than 600 and is clipped
//		GraderBasicsTraceUtility.setMaxPrintedTraces(600);
//		
//		// Change this number if all traces together are longer than 2000
//		GraderBasicsTraceUtility.setMaxTraces(2000);
		
		// Change this number if your process times out prematurely
		BasicProjectExecution.setProcessTimeOut(5);
//		
		// You need to always call such a method
		F23Assignment0_1Suite.main(args);
	}
}
