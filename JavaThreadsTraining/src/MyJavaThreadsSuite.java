import java.util.Arrays;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import grader.basics.config.BasicExecutionSpecificationSelector;
import grader.basics.junit.BasicJUnitUtils;
import gradingTools.javaThreads.hello.execution.HelloExecutionChecks;
import gradingTools.javaThreads.hello.source.HelloSourceChecks;
import gradingTools.javaThreads.hello.style.HelloStyleChecks;
import gradingTools.javaThreads.luckyNumbers.execution.LuckyNumbersExecutionChecks;
import gradingTools.javaThreads.luckyNumbers.style.LuckyNumbersStyleChecks;
import gradingTools.javaThreads.oddNumbers.execution.OddNumbersExecutionChecks;
import gradingTools.javaThreads.oddNumbers.source.OddNumbersSourceChecks;
import gradingTools.javaThreads.oddNumbers.style.OddNumbersStyleChecks;
import gradingTools.javaThreads.pi.execution.PIExecutionChecks;
import gradingTools.javaThreads.pi.source.PISourceChecks;
import gradingTools.javaThreads.primes.execution.PrimesExecutionChecks;
import gradingTools.javaThreads.primes.source.PrimesSourceChecks;
import gradingTools.javaThreads.source.JavaThreadsSourceChecks;
import gradingTools.javaThreads.style.JavaThreadsStyleChecks;
import gradingTools.shared.testcases.AssignmentSuiteSkeleton;
import gradingTools.shared.testcases.ConcurrencySuiteSkeleton;
@RunWith(Suite.class)
@Suite.SuiteClasses({
	OddNumbersSourceChecks.class,
	OddNumbersExecutionChecks.class,
	PrimesExecutionChecks.class,
	PrimesSourceChecks.class,
	PIExecutionChecks.class,
	PISourceChecks.class,
	JavaThreadsStyleChecks.class

})
public class MyJavaThreadsSuite extends ConcurrencySuiteSkeleton {
		public static void main (String[] args) {
		try {
			processArgs(args);
			BasicJUnitUtils.interactiveTest(MyJavaThreadsSuite.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static {
		BasicExecutionSpecificationSelector.getBasicExecutionSpecification().
		setCheckStyleConfiguration("unc_checks_concurrency.xml");
	}	
}
