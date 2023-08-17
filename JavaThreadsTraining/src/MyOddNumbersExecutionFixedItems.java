

import grader.basics.junit.NotAutomatableException;
import grader.basics.junit.TestCaseResult;
import grader.basics.project.NotGradableException;
import grader.basics.project.Project;
import util.annotations.MaxValue;
@MaxValue(30)
public class MyOddNumbersExecutionFixedItems extends MyAbstractOddNumbersExecution {
	public static int MAX_ITEMS = 29;
	int numItems;
	public MyOddNumbersExecutionFixedItems() {
	     numItems = (int) Math.ceil(Math.random()*MAX_ITEMS);
	     if (numItems <= 4) {
	    	 numItems = 7;
	     }
	}	
	@Override
	protected int totalIterations() {
		return numItems;
	}
	

	

	
}
