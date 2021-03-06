package strategies;

import logger.PjiitOutputter;
import internetz.Task;
import internetz.TaskInternals;

public abstract class EmergenceStrategy {
	
	protected abstract void doAftearmath(Task task, TaskInternals singleTaskInternal);
	
	protected void say(String s) {
		PjiitOutputter.say(s);
	}

	protected void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

}
