package strategies;

import internetz.Agent;
import internetz.Task;
import internetz.TaskInternals;
import internetz.TaskPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import logger.PjiitOutputter;
import tasks.CentralAssignment;
import tasks.CentralAssignmentOrders;

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
		
		bussy.clear();
		taken.clear();
	}
	
	private Boolean checkIfApplicable(Task task, String skill){
		boolean applicable = true;
		for (Object[] object : taken){
			if (((Task)object[0] ).equals(task) && ((String)object[1]).equals(skill)){
				applicable = false;
			}
		}
		return applicable;
	}
	
	public void centralPlanningCalc(List<Agent> listAgent, TaskPool taskPool) {
		say("Central planning working !");
		
		bussy = bussy == null ? new ArrayList<Agent>() : bussy;
		taken = taken == null ? new ArrayList<Object[]>() : taken;
		
		zeroAgentsOrders(listAgent);
		
		for (int i = 0 ; i < listAgent.size() ; i++) {
			double found_gMinusW = 0d;
			Task chosen = null;
			String skill = null;
			
			//ArrayList<Task> intersection = makeIntersection(taskPool.getTasks());
			
			for (Task singleTaskFromPool : taskPool.getTasks()) {
				//boolean consider = false;
				
				for (TaskInternals singleSkill : singleTaskFromPool.
						getTaskInternals().values()) {
					if (checkIfApplicable(singleTaskFromPool, 
							singleSkill.getSkill().getName())){
						double gMinusW = singleSkill.getWorkLeft();
						// ile pozostalo pracy
						if (gMinusW > found_gMinusW){
							//consider = true;
							chosen = singleTaskFromPool;
							skill = singleSkill.getSkill().getName();
						}
					}
				}
			}
			
			double max_delta = 0d;
			Agent chosenAgent = null;
			
			//wybierz agenta m, który ma najwy¿sz¹ wydajnoœæ delta w skillu j
			List<Agent> listOfAgents = CentralAssignment.choseAgents(listAgent, bussy);
			
			for (Agent agent : listOfAgents){
				double local_delta = 
						agent.getAgentInternals(skill).getExperience().getDelta();
				if (local_delta > max_delta){
					chosenAgent = agent;
				}
			}
			chosenAgent.setCentralAssignmentOrders(
					new CentralAssignmentOrders(chosen, skill));
			
			bussy.add(chosenAgent);
			taken.add(new Object[] {chosen, skill});
		}
	}

}
