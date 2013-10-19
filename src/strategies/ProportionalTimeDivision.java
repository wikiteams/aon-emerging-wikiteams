package strategies;

import internetz.Task;
import internetz.TaskInternals;
import internetz.WorkUnit;

public class ProportionalTimeDivision extends EmergenceStrategy {
	
	public void increment(Task task, 
			TaskInternals singleTaskInternal, int n, double alpha, double experience){
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		WorkUnit workRequired = singleTaskInternal.getWorkRequired();
		workDone.increment(n * alpha * experience);
		doAftearmath(task, singleTaskInternal);
	}

	@Override
	protected void doAftearmath(Task task, TaskInternals singleTaskInternal) {
		if (singleTaskInternal.isWorkDone()){
			super.say("Work in taskInternal:" + singleTaskInternal + " is done.");
			task.removeSkill(singleTaskInternal.getSkill().getName());
		}
	}
	
}
