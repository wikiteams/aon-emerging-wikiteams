/**
 * 
 */
package internetz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import logger.PjiitOutputter;
import strategies.GreedyAssignmentTask;
import strategies.ProportionalTimeDivision;
import strategies.Strategy;
import constants.Constraints;

/**
 * Task is a collection of a three-element set of skill, number of work units,
 * and work done.
 * 
 * @since 1.0
 * @version 1.0
 * @author Oskar Jarczyk
 */
public class Task {

	private static int COUNT = 0;

	private String name;
	private int id;

	private Map<String, TaskInternals> skills = new HashMap<String, TaskInternals>();

	public Task() {
		this.id = COUNT++;
		this.name = "Task_" + COUNT;
		say("Task object " + this + " created");
	}

	public void addSkill(String key, TaskInternals taskInternals) {
		skills.put(key, taskInternals);
	}

	public TaskInternals getTaskInternals(String key) {
		return skills.get(key);
	}

	public synchronized void initialize() {
		setId(++COUNT);
		TaskSkillsPool.fillWithSkills(this);
		say("Task object initialized with id: " + this.id);
	}

	public Map<String, TaskInternals> getTaskInternals() {
		return skills;
	}

	public void setTaskInternals(Map<String, TaskInternals> skills) {
		this.skills = skills;
	}

	public synchronized void setId(int id) {
		this.id = id;
	}

	public synchronized int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void workOnTask(Agent agent, Strategy.SkillChoice strategy) {
		switch (strategy) {
		case PROPORTIONAL_TIME_DIVISION:
			say(Constraints.INSIDE_PROPORTIONAL_TIME_DIVISION);
			for (TaskInternals singleTaskInternal : skills.values()) {
				sanity("Choosing Si:{" + 
						singleTaskInternal.getSkill().getName() + 
						"} inside Ti:{" + singleTaskInternal.toString() + "}");
				int n = skills.size();
				double alpha = 1 / n;
				Experience experience = agent.getAgentInternals(
						singleTaskInternal.getSkill().getName()).getExperience();
				double delta = experience.getDelta();
				ProportionalTimeDivision.increment(singleTaskInternal, 1, alpha, delta);
				experience.increment(alpha);
			}
			break;
		case GREEDY_ASSIGNMENT_BY_TASK:
			say(Constraints.INSIDE_GREEDY_ASSIGNMENT_BY_TASK);
			TaskInternals singleTaskInternal = null;
			double highest = -1;
			for (TaskInternals searchTaskInternal : skills.values()) {
				if (searchTaskInternal.getWorkDone().d > highest){
					highest = searchTaskInternal.getWorkDone().d;
					singleTaskInternal = searchTaskInternal;
				}
			}
			{
				sanity("Choosing Si:{" + 
						singleTaskInternal.getSkill().getName() + 
						"} inside Ti:{" + singleTaskInternal.toString() + "}");
				int n = skills.size();
				//double alpha = 1 / n;
				Experience experience = agent.getAgentInternals(
						singleTaskInternal.getSkill().getName()).getExperience();
				double delta = experience.getDelta();
				GreedyAssignmentTask.increment(singleTaskInternal, 1, delta);
				experience.increment(1);
			}
			break;
		case CHOICE_OF_AGENT:
			;
			break;
		case RANDOM:
			say(Constraints.INSIDE_RANDOM);
			Random generator = new Random();
			List<String> keys = new ArrayList<String>(skills.keySet());
			String randomKey = keys.get(generator.nextInt(keys.size()));
			TaskInternals randomTaskInternal = skills.get(randomKey);
			{
				sanity("Choosing Si:{" + 
						randomTaskInternal.getSkill().getName() + 
						"} inside Ti:{" + randomTaskInternal.toString() + "}");
				int n = skills.size();
				//double alpha = 1 / n;
				Experience experience = agent.getAgentInternals(
						randomTaskInternal.getSkill().getName()).getExperience();
				double delta = experience.getDelta();
				GreedyAssignmentTask.increment(randomTaskInternal, 1, delta);
				experience.increment(1);
			}
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}
	}
	
	@Override
	public String toString() {
		return "Task " + id + " " + name;
	}
	
	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
	private void sanity(String s){
		PjiitOutputter.sanity(s);
	}

}
