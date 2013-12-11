package tasks;

import internetz.Agent;
import internetz.Task;
import internetz.TaskInternals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import repast.simphony.random.RandomHelper;
import strategies.Strategy.TaskChoice;

public class CentralAssignment {

	private Map<String, Task> tasks;

	public CentralAssignment(Map<String, Task> tasks) {
		this.tasks = tasks;
	}

	public static List<Agent> choseAgents(Collection<Agent> agents) {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents) {
			if (agent.getStrategy().taskChoice
					.equals(TaskChoice.CENTRAL_ASSIGNMENT)) {
				CentralAssignmentOrders cao = agent
						.getCentralAssignmentOrders();
				if (cao == null)
					list.add(agent);
			}
		}
		return list;
	}

	public Task concludeMath(Agent agent) {
		// Task taskWithHighestSG = null;
		// double found_sigma_delta = 0;
		Task chosen = null;
		
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		chosen = cao.getChosenTask();

		// //Collection<Skill> allAgentSkillsHomCl = agent.getSkills();
		// for (Task singleTaskFromPool : tasks.values()) {
		// //double sigma_delta = 0;
		// //boolean consider = false;
		// for (TaskInternals singleSkill : singleTaskFromPool.
		// getTaskInternals().values()) {
		// double gMinusW = singleSkill.getWorkLeft();
		// }
		// if (consider){
		// if (taskWithHighestSG == null) {
		// taskWithHighestSG = singleTaskFromPool;
		// found_sigma_delta = sigma_delta;
		// } else {
		// if (found_sigma_delta < sigma_delta) {
		// taskWithHighestSG = singleTaskFromPool;
		// }
		// }
		// }
		// }
		// if (taskWithHighestSG != null){
		// chosen = taskWithHighestSG;
		// } else {
		// // intersection is empty, chose random
		// if (tasks.size() > 0)
		// chosen = tasks.get(RandomHelper.nextIntFromTo(0,tasks.size()-1));
		// else
		// chosen = null;
		// }
		return chosen;
	}

}
