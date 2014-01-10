package strategies;

import internetz.Agent;
import internetz.Task;
import internetz.TaskInternals;
import internetz.TaskPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logger.PjiitOutputter;
import repast.simphony.random.RandomHelper;
import tasks.CentralAssignment;
import tasks.CentralAssignmentOrders;

/**
 * Algorithm of central work planning
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 */
public class CentralPlanning {
	
	private List<Agent> bussy;
	private List<Object[]> taken;
	
	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
	private void zeroAgentsOrders(List<Agent> listAgent){
		say("Zeroing central plan !");
		
		for (Agent agent : listAgent){
			agent.setCentralAssignmentOrders(null);
		}
		
		bussy = bussy == null ? new ArrayList<Agent>() : bussy;
		taken = taken == null ? new ArrayList<Object[]>() : taken;
		
		bussy.clear();
		taken.clear();
	}
	
	private Boolean checkIfApplicable(Task task, String skill){
		boolean applicable = true;
		for (Object[] object : taken){
			if (((Task)object[0]).equals(task) && ((String)object[1]).equals(skill)){
				applicable = false;
			}
		}
		return applicable;
	}
	
	public void centralPlanningCalc(List<Agent> listAgent, TaskPool taskPool) {
		say("Central planning working !");
		
		zeroAgentsOrders(listAgent);
		
		//Powtarzaj M razy, gdzie M to liczba agentow
		LoopThroughAgentsCount: for (int i = 0 ; i < listAgent.size() ; i++) {
			double found_gMinusW = 0d;
			
			Task chosen = null;
			String skill = null;
			
			List<Task> shuffledTasks = new ArrayList<Task>(taskPool.getTasks());
			Collections.shuffle(shuffledTasks);
			
			//zainicjuj chosen oraz skill losowa wartoscia w razie braku matchu potem
			MinusTaskInternalsMarkedAsTaken:for(Task task__ : shuffledTasks){
				List<TaskInternals> shuffledTaskInternals = new ArrayList<TaskInternals>(
						task__.getTaskInternals().values());
				Collections.shuffle(shuffledTaskInternals);
				for(TaskInternals ti__ : shuffledTaskInternals){
					if (checkIfApplicable(task__, ti__.getSkill().getName())){
						chosen = task__;
						skill = ti__.getSkill().getName();
						break MinusTaskInternalsMarkedAsTaken;
					}
				}
			}
			
			if ( (chosen == null) || (skill == null) )
				break LoopThroughAgentsCount; // no more free tasks for planer !
			// possible in situation when there are more agents than tasks!
			// break the loop leaves the method
			
			assert chosen != null;
			assert skill != null;
			
			//znajdz task i i skill j, w ktorym pozostalo najwiêcej pracy
			FindLeastAdvancedTaskInternal:for (Task singleTaskFromPool : taskPool.getTasks()) {
				for (TaskInternals singleSkill : singleTaskFromPool.
						getTaskInternals().values()) {
					if (checkIfApplicable(singleTaskFromPool, 
							singleSkill.getSkill().getName())){
						double gMinusW = singleSkill.getWorkLeft();
						// ile pozostalo pracy
						if (gMinusW > found_gMinusW){
							chosen = singleTaskFromPool;
							skill = singleSkill.getSkill().getName();
						}
					}
				}
				// nawet jezeli w ten petli nie wybranego zadnego tasku do chosen,
				// to pamietaj ze i tak w chosen jest jakis task z poczatkowych obliczen
				// - pozostalych dostepnych taskow po operacji shufflue
			}
			
			double max_delta = 0d;
			Agent chosenAgent = null;
			
			//wybierz agenta m, który ma najwy¿sz¹ wydajnoœæ delta w skillu j
			List<Agent> listOfAgentsNotBussy = CentralAssignment.choseAgents(listAgent, bussy);
			assert listOfAgentsNotBussy != null;
			assert listOfAgentsNotBussy.size() > 0;
			// stad te asserty bo w koncu planner iteruje po ilosci agentow,
			// wiec pracujemy nad choc jednym wolnym!
			
			for (Agent agent : listOfAgentsNotBussy){
				double local_delta = agent.getAgentInternals(skill) != null ?
						agent.getAgentInternals(skill).getExperience().getDelta() : 0;
						// zero w przypadku gdy agent nie ma w ogole doswiadczenia w tym tasku!
				if (local_delta > max_delta){
					chosenAgent = agent;
				}
			}
			
			if (chosenAgent == null){
				// nie ma zadnego agenta o takich skillach, wybierz losowo !
				Collections.shuffle(listOfAgentsNotBussy);
				chosenAgent = listOfAgentsNotBussy.get(RandomHelper.
						nextIntFromTo(0, listOfAgentsNotBussy.size() - 1));
			}
			
			assert chosenAgent != null;
			assert chosen != null;
			assert skill != null;
			
			chosenAgent.setCentralAssignmentOrders(
					new CentralAssignmentOrders(chosen, skill));
			
			bussy.add(chosenAgent);
			taken.add(new Object[] {chosen, skill});
		}
	}

}
