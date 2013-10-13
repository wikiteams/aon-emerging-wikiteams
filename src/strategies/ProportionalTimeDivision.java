package strategies;

import internetz.TaskInternals;
import internetz.WorkUnit;

public class ProportionalTimeDivision extends EmergenceStrategy {
	
	public static void increment(
			TaskInternals singleTaskInternal, double alpha, double experience){
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		WorkUnit workUnit = singleTaskInternal.getWorkUnits();
		
		workDone.increment(alpha * experience);
	}
	
}
