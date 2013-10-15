package strategies;

import internetz.TaskInternals;
import internetz.WorkUnit;

public class ProportionalTimeDivision extends EmergenceStrategy {
	
	public static void increment(
			TaskInternals singleTaskInternal, int n, double alpha, double experience){
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		WorkUnit workRequired = singleTaskInternal.getWorkRequired();
		
		workDone.increment(n * alpha * experience);
	}
	
}
