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

	public static List<Agent> choseAgents(Collection<Agent> agents, Collection<Agent> bussy) {
		List<Agent> list = new ArrayList<Agent>();
		for (Agent agent : agents) {
			if (agent.getStrategy().taskChoice
					.equals(TaskChoice.CENTRAL_ASSIGNMENT)) {
				if (!bussy.contains(agent)){
					CentralAssignmentOrders cao = agent
							.getCentralAssignmentOrders();
					if (cao == null)
						list.add(agent);
				}
			}
		}
		return list;
	}

	public Task concludeMath(Agent agent) {
		Task chosen = null;
		
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		chosen = cao == null ? null : cao.getChosenTask();

		return chosen;
	}

}
