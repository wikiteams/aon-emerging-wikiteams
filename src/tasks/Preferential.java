package tasks;

import internetz.Agent;
import internetz.Skill;
import internetz.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Preferential {
	
	private Map<String, Task> tasks;
	
	public Preferential(Map<String, Task> tasks){
		this.tasks = tasks;
	}
	
	public Task concludeMath(Agent agent){
		Task mostAdvanced = null;
		ArrayList<Task> tasksWithMatchingSkillsPref = new ArrayList<Task>();
		Task chosen = null;
		
		Collection<Skill> allAgentSkillsPref = agent.getSkills();
		for (Task singleTaskFromPool : tasks.values()) {
			internal:for (Skill singleSkill : allAgentSkillsPref) {
				if (singleTaskFromPool.getTaskInternals().containsKey(
						singleSkill.toString())) {
					tasksWithMatchingSkillsPref.add(singleTaskFromPool);
					if (mostAdvanced == null) {
						mostAdvanced = singleTaskFromPool;
					} else {
						if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
								.getGeneralAdvance()) {
							mostAdvanced = singleTaskFromPool;
						}
					}
					break internal;
				}
			}
		}

		if ( (tasksWithMatchingSkillsPref.size() < 1) || (mostAdvanced == null) ) {
			for (Task singleTaskFromPool : tasks.values()) {
				if (mostAdvanced == null) {
					mostAdvanced = singleTaskFromPool;
				} else {
					if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
							.getGeneralAdvance()) {
						mostAdvanced = singleTaskFromPool;
					}
				}
			}
		}
		
		if (mostAdvanced != null) {
			chosen = mostAdvanced;
		}
		
		return chosen;
	}

}
