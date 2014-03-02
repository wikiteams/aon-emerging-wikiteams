package tasks;

import internetz.Agent;
import internetz.AgentInternals;
import internetz.Skill;
import internetz.Task;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import repast.simphony.random.RandomHelper;

public class Homophyly {
	
	private Map<String, Task> tasks;
	
	public Homophyly(Map<String, Task> tasks){
		this.tasks = tasks;
	}
	
	public HashMap<Integer, Task> firstStep(Agent agent) {
		HashMap<Double, Task> initial = new HashMap<Double,Task>();
		HashMap<Integer, Task> result = new HashMap<Integer,Task>();
		
		Collection<Skill> allAgentSkillsHetCl = agent.getSkills();
		
		for (Task singleTaskFromPool : tasks.values()) {
			int commonSkills = 0;
			for (Skill singleSkill : allAgentSkillsHetCl) {
				if (singleTaskFromPool.getTaskInternals().containsKey(singleSkill.toString())) {
					commonSkills++;
				}
			}
			
			if(commonSkills > 0) {
				double proportion = (double)allAgentSkillsHetCl.size() / (double)commonSkills;
				initial.put(proportion, singleTaskFromPool);
			}
		}
		
		if(initial.size() == 0) {
			return result;
		}
		
		Double min = Collections.min(initial.keySet());
		
		for (Map.Entry<Double, Task> task : initial.entrySet()) {
			if(min == task.getKey()) {
				result.put(task.getValue().countTaskInternals(), task.getValue());
			}
		}
		
		return result;
	}
	
	public HashMap<Integer, Task> secondStep(HashMap<Integer, Task> initial) {		
		HashMap<Integer, Task> result = new HashMap<Integer,Task>();
		
		Integer min = Collections.min(initial.keySet());
		
		for (Map.Entry<Integer, Task> task : initial.entrySet()) {
			if(min == task.getKey()) {
				result.put(task.getKey(), task.getValue());
			}
		}
		
		return result;
	}
	
	public Task thirdStep(HashMap<Integer, Task> initial, Agent agent) {		
		HashMap<Double, Task> prepared = new HashMap<Double,Task>();
		HashMap<Double, Task> result = new HashMap<Double,Task>();
		
		for (Map.Entry<Integer, Task> task : initial.entrySet()) {
			Double cumulatedExperience = 0.0;
			int commonTasksCount = 0;
			Double average = 0.0;
			
			Collection<AgentInternals> internals = agent.getAgentInternals();
			for (AgentInternals ai : internals) {
				if (task.getValue().getTaskInternals().containsKey(ai.getSkill().toString())) {
					cumulatedExperience += ai.getExperience().getDelta();
					commonTasksCount += 1;
				}
			}
			
			if(commonTasksCount > 0) {
				average = cumulatedExperience / commonTasksCount;
			}
			
			prepared.put(average, task.getValue());
		}
		
		Double max = Collections.max(prepared.keySet());
		
		for (Map.Entry<Double, Task> task : result.entrySet()) {
			if(max == task.getKey()) {
				result.put(task.getKey(), task.getValue());
			}
		}
		
		if(result.size() == 1) {
			return result.get(max);
		}
		
		return result.get(RandomHelper.nextIntFromTo(0,result.size()-1));
	}
	
	public Task concludeMath(Agent agent){
		HashMap<Integer, Task> preselected = this.firstStep(agent);
		
		if(preselected.size() == 0) {
			return preselected.get(RandomHelper.nextIntFromTo(0,preselected.size()-1));
		}
		
		if(preselected.size() == 1) {
			return (Task) preselected.values().toArray()[0];
		}
		
		preselected = this.secondStep(preselected);
		
		if(preselected.size() == 1) {
			return (Task) preselected.values().toArray()[0];
		}

		return this.thirdStep(preselected, agent);
	}
}

