package strategies;

import internetz.TaskInternals;
import internetz.WorkUnit;

public class ChoiceAgentStrategy extends EmergenceStrategy {
	
	public static void increment(TaskInternals singleTaskInternal, int n, double experience){
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		WorkUnit workRequired = singleTaskInternal.getWorkRequired();
		
		//wd.increment();
	}

}
