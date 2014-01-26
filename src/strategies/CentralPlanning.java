package strategies;

import internetz.Agent;
import internetz.Task;
import internetz.TaskInternals;
import internetz.TaskPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import tasks.CentralAssignment;
import tasks.CentralAssignmentOrders;
import utils.LaunchStatistics;

/**
 * Algorithm of central work planning
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * @version 1.4.1
 */
public class CentralPlanning {

	private List<Agent> bussy;
	private List<Object[]> taken;

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void zeroAgentsOrders(List<Agent> listAgent) {
		say("Zeroing central plan !");

		for (Agent agent : listAgent) {
			agent.setCentralAssignmentOrders(null);
		}

		bussy = bussy == null ? new ArrayList<Agent>() : bussy;
		taken = taken == null ? new ArrayList<Object[]>() : taken;

		bussy.clear();
		taken.clear();
		// TODO: check if this is faster for the GC than creating new object
		// ArrayList
	}

	public void centralPlanningCalc(List<Agent> listAgent, TaskPool taskPool) {
		say("Central planning working !");

		zeroAgentsOrders(listAgent);
		Collections.shuffle(listAgent);

		List<TaskInternals> takenTaskInternals = new ArrayList<TaskInternals>();

		// Powtarzaj M razy, gdzie M to liczba agentow
		// co jezeli taskow jest mniej niz agentow ?
		int mainIterationCount = LaunchStatistics.singleton.taskCount < 
				LaunchStatistics.singleton.agentCount ? taskPool
				.size() : listAgent.size();
		LoopThroughAgentsCount: for (int i = 0; i < mainIterationCount; i++) {
			double found_gMinusW = 0d;

			Task chosen = null;
			TaskInternals skill = null;

			List<Task> shuffledTasksFirstInit = new ArrayList<Task>(
					taskPool.getTasks());
			Collections.shuffle(shuffledTasksFirstInit);

			// zainicjuj chosen oraz skill losowa wartoscia w razie braku matchu
			// potem
			MinusTaskInternalsMarkedAsTaken: for (Task task__ : shuffledTasksFirstInit) {
				if (task__.isClosed())
					continue;
				List<TaskInternals> shuffledTaskInternals = new ArrayList<TaskInternals>(
						task__.getTaskInternals().values());
				Collections.shuffle(shuffledTaskInternals);
				for (TaskInternals ti__ : shuffledTaskInternals) {
					if (checkIfApplicable(task__, ti__) && takenTaskInternals.contains(ti__)) {
						chosen = task__;
						skill = ti__;
						break MinusTaskInternalsMarkedAsTaken;
					}
				}
			}

			if ((chosen == null) || (skill == null)) {
				break LoopThroughAgentsCount; // no more free tasks for planer !
				// possible in situation when there are more agents than tasks!
				// break the loop leaves the method
			} else {
				// shuffledTasksFirstInit.remove(chosen);
			}

			assert chosen != null;
			assert skill != null;

			// znajdz task i i skill j, w ktorym pozostalo najwiêcej pracy
			for (Task singleTaskFromPool : taskPool.getTasks()) {
				for (TaskInternals singleSkill : singleTaskFromPool
						.getTaskInternals().values()) {
					if (checkIfApplicable(singleTaskFromPool, singleSkill)) {
						double gMinusW = singleSkill.getWorkLeft();
						// ile pozostalo pracy
						if ((!singleSkill.isWorkDone())
								&& (gMinusW > found_gMinusW)) {
							chosen = singleTaskFromPool;
							skill = singleSkill;
						}
					}
				}
				// nawet jezeli w ten petli nie wybranego zadnego tasku do
				// chosen,
				// to pamietaj ze i tak w chosen jest jakis task z poczatkowych
				// obliczen
				// - pozostalych dostepnych taskow po operacji shufflue
			}

			double max_delta = 0;
			Agent chosenAgent = null;

			// wybierz agenta m, który ma najwy¿sz¹ wydajnoœæ delta w skillu j
			List<Agent> listOfAgentsNotBussy = CentralAssignment.choseAgents(
					listAgent, bussy);
			assert listOfAgentsNotBussy != null;
			assert listOfAgentsNotBussy.size() > 0;
			// stad te asserty bo w koncu planner iteruje po ilosci agentow,
			// wiec pracujemy nad choc jednym wolnym!

			for (Agent agent : listOfAgentsNotBussy) {
				double local_delta = agent.getAgentInternals(skill
						.getSkillName()) != null ? agent
						.getAgentInternals(skill.getSkillName())
						.getExperience().getDelta() : 0;
				// zero w przypadku gdy agent nie ma w ogole doswiadczenia w tym
				// tasku!
				if (local_delta > max_delta) {
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
			taken.add(new Object[] { chosen, skill });

			takenTaskInternals.add(skill);
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

	private Boolean checkIfApplicable(Task task, TaskInternals skill) {
		boolean applicable = true;
		for (Object[] object : taken) {
			if (((Task) object[0]).equals(task)
					&& ((TaskInternals) object[1]).equals(skill)) {
				applicable = false;
			}
		}
		return applicable;
	}

}
