package strategies;

import internetz.Agent;
import internetz.Task;
import internetz.TaskInternals;
import internetz.TaskPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import tasks.CentralAssignment;
import tasks.CentralAssignmentOrders;
import utils.LaunchStatistics;

/**
 * Algorithm of central work planning, heuristic is based on en entity
 * called a Central Planner which sorts tasks descending by those least
 * finished, and finds an agent most experienced in those tasks.
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * @version 1.4.1
 */
public class CentralPlanning {

	private List<Agent> bussy;
	//private List<Object[]> taken;
	private static final double minus_one = -1;

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void zeroAgentsOrders(List<Agent> listAgent) {
		say("Zeroing central plan !");

		for (Agent agent : listAgent) {
			agent.setCentralAssignmentOrders(null);
		}

		bussy = bussy == null ? new ArrayList<Agent>() : bussy;
		//taken = taken == null ? new ArrayList<Object[]>() : taken;

		bussy.clear();
		//taken.clear();
		// TODO: check if this is faster for the GC than creating new object
		// ArrayList
	}

	public void centralPlanningCalc(List<Agent> listAgent, TaskPool taskPool) {
		say("Central planning working !");

		// Zeroing agents orders
		zeroAgentsOrders(listAgent);
		Collections.shuffle(listAgent);

		//List<TaskInternals> takenTaskInternals = new ArrayList<TaskInternals>();

		// Iterate mainIterationCount times
		// if there are less tasks than agent, iterate taskCount times
		int mainIterationCount = LaunchStatistics.singleton.
				taskCount < LaunchStatistics.singleton.agentCount ? taskPool
				.size() : listAgent.size();

		List<Task> shuffledTasksFirstInit = new ArrayList<Task>(
				taskPool.getTasks());
		Collections.shuffle(shuffledTasksFirstInit);

		SortedMap<Double, TaskInternals> sortedMap = new TreeMap<Double, TaskInternals>(
				new Comparator<Double>() {
					public int compare(Double o1, Double o2) {
						// say(o1 + " compared to " + o2 + " returns " +
						// (-o1.compareTo(o2)));
						return -o1.compareTo(o2);
					}
				});

		int iterator___ = 0;
		// Find Task {i} and Skill {j}, with highest work left
		for (Task singleTaskFromPool : shuffledTasksFirstInit) {
			for (TaskInternals singleSkill : singleTaskFromPool
					.getTaskInternals().values()) {
				// if (checkIfApplicable(singleTaskFromPool, singleSkill)) {
				// double gMinusW = singleSkill.getWorkLeft();
				// ile pozostalo pracy
				if (!singleSkill.isWorkDone()) {
					// chosen = singleTaskFromPool;
					// skill = singleSkill;
					sortedMap.put(singleSkill.getWorkLeft()
							- ((++iterator___) / (10 * 6)), singleSkill);
				}
			}
		}

		for (int i = 0; i < mainIterationCount; i++) {
			
			TaskInternals skill = (TaskInternals) sortedMap.values().toArray()[i]; 
			Task chosen = skill.getOwner();
			
			assert chosen != null;
			assert skill != null;

			Agent chosenAgent = null;

			// Choose Agent m, which have highest delta() in Skill j
			List<Agent> listOfAgentsNotBussy = CentralAssignment.choseAgents(
					listAgent, bussy);
			assert listOfAgentsNotBussy != null;
			assert listOfAgentsNotBussy.size() > 0;
			// stad te asserty bo w koncu planner iteruje po ilosci agentow,
			// wiec pracujemy nad choc jednym wolnym!
			double max_delta = minus_one ;
			for (Agent agent : listOfAgentsNotBussy) {
				double local_delta = agent.getAgentInternals(skill
						.getSkillName()) != null ? agent
						.getAgentInternals(skill.getSkillName())
						.getExperience().getDelta() : 0;
				// zero w przypadku gdy agent nie ma w ogole doswiadczenia w tym
				// tasku!
				if (local_delta > max_delta) {
					max_delta = local_delta;
					chosenAgent = agent;
				}
			}

			if (chosenAgent == null) {
				// nie ma zadnego agenta o takich skillach, wybierz losowo !
				Collections.shuffle(listOfAgentsNotBussy);
				chosenAgent = listOfAgentsNotBussy.get(RandomHelper
						.nextIntFromTo(0, listOfAgentsNotBussy.size() - 1));
			}

			assert chosenAgent != null;
			assert chosen != null;
			assert skill != null;

			chosenAgent.setCentralAssignmentOrders(new CentralAssignmentOrders(
					chosen, skill));

			bussy.add(chosenAgent);
			//taken.add(new Object[] { chosen, skill });
			//takenTaskInternals.add(skill);
		}
	}

	// private Collection<Task> getNotTakenTasks(TaskPool taskPool){
	// Collection<Task> t = taskPool.getTasks();
	// List<Task> result = new ArrayList<Task>();
	// for(Task task : t){
	// if (! taken.contains(task))
	// result.add(task);
	// }
	// return result;
	// }

//	private Boolean checkIfApplicable(Task task, TaskInternals skill) {
//		boolean applicable = true;
//		for (Object[] object : taken) {
//			if (((Task) object[0]).equals(task)
//					&& ((TaskInternals) object[1]).equals(skill)) {
//				applicable = false;
//			}
//		}
//		return applicable;
//	}

}
