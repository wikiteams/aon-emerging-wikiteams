package tasks;

import internetz.Agent;
import internetz.Skill;
import internetz.Task;

import java.util.Collection;
import java.util.Map;

import argonauts.PersistAdvancement;

public class Preferential {
	
	//private Map<String, Task> tasks;
	private static final double emptyResultSignal = -0.9;
	
	@Deprecated
	public Preferential(Map<String, Task> tasks){
		//this.tasks = tasks;
	}
	
	public Preferential() {
		// Default constructor, no arguments needed
	}

	public Task concludeMath(Agent agent){
		Collection<Skill> allAgentSkillsPref = agent.getSkills();
		Task chosen = null;
		double adv = -1;
		for (Skill singleSkill : allAgentSkillsPref) {
			Object[] r = PersistAdvancement.getMostAdvanced(singleSkill);
			if (((Double) r[0]) < emptyResultSignal)
				continue;
			if (((Double) r[0]) > adv){
				chosen = (Task) r[1];
			}
		}
		return chosen;
	}
	
	/***
	 * @Deprecated Reason - very slow execution time, moved to newer method
	 * @param agent
	 * @return
	 */
//	public Task concludeMath__(Agent agent){
//		Task mostAdvanced = null;
//		ArrayList<Task> tasksWithMatchingSkillsPref = new ArrayList<Task>();
//		Task chosen = null;
//		
//		Collection<Skill> allAgentSkillsPref = agent.getSkills();
//		for (Task singleTaskFromPool : tasks.values()) {
//			internal:for (Skill singleSkill : allAgentSkillsPref) {
//				if (singleTaskFromPool.getTaskInternals().containsKey(
//						singleSkill.toString())) {
//					tasksWithMatchingSkillsPref.add(singleTaskFromPool);
//					if (mostAdvanced == null) {
//						mostAdvanced = singleTaskFromPool;
//					} else {
//						if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
//								.getGeneralAdvance()) {
//							mostAdvanced = singleTaskFromPool;
//						}
//					}
//					break internal;
//				}
//			}
//		}
//
//		if ( (tasksWithMatchingSkillsPref.size() < 1) || (mostAdvanced == null) ) {
//			for (Task singleTaskFromPool : tasks.values()) {
//				if (mostAdvanced == null) {
//					mostAdvanced = singleTaskFromPool;
//				} else {
//					if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
//							.getGeneralAdvance()) {
//						mostAdvanced = singleTaskFromPool;
//					}
//				}
//			}
//		}
//		
//		if (mostAdvanced != null) {
//			chosen = mostAdvanced;
//		}
//		
//		return chosen;
//	}

}
