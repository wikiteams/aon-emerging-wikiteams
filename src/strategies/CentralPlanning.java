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
			if (((Task)object[0]).equals(task) && ((String)object[1]).equals(skill)){
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
		
		mainloop: for (int i = 0 ; i < listAgent.size() ; i++) {
			double found_gMinusW = 0d;
			
			Task chosen = null;
			String skill = null;
			List<Task> lista = new ArrayList<Task>(taskPool.getTasks());
			Collections.shuffle(lista);
			
			that:for(Task task_ : lista){
				List<TaskInternals> lista2 = new ArrayList<TaskInternals>(
						task_.getTaskInternals().values());
				Collections.shuffle(lista2);
				for(TaskInternals ti_ : lista2){
					if (checkIfApplicable(task_, ti_.getSkill().getName())){
						chosen = task_;
						skill = ti_.getSkill().getName();
						break that;
					}
				}
			}
			
			if ( (chosen == null) || (skill == null) )
				break mainloop;
			
			assert chosen != null;
			assert skill != null;
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
				double local_delta = agent.getAgentInternals(skill) != null ?
						agent.getAgentInternals(skill).getExperience().getDelta() : 0;
				if (local_delta > max_delta){
					chosenAgent = agent;
				}
			}
			
			if (chosenAgent == null){
				// nie ma zadnego agenta o takich skillach, wybierz losowo
				chosenAgent = listOfAgents.get(RandomHelper.
						nextIntFromTo(0, listOfAgents.size() - 1));
			}
			
			chosenAgent.setCentralAssignmentOrders(
					new CentralAssignmentOrders(chosen, skill));
			
			bussy.add(chosenAgent);
			taken.add(new Object[] {chosen, skill});
		}
	}

}
