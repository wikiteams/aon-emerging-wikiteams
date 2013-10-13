/**
 * 
 */
package internetz;

import java.util.HashMap;
import java.util.Map;

import constants.Constraints;

import strategies.ProportionalTimeDivision;
import strategies.Strategy;

import logger.PjiitOutputter;

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
				double delta = agent.getAgentInternals(
						singleTaskInternal.getSkill().getName()).getExperience().getDelta();
				ProportionalTimeDivision.increment(singleTaskInternal, alpha, delta);
				//agent.
			}
		case GREEDY_ASSIGNMENT_BY_TASK:
			for (TaskInternals __skill : skills.values()) {
				int n = skills.size();
				double delta = agent.getAgentInternals(
						__skill.getSkill().getName()).getExperience().getDelta();
				say("Inside switch - PROPORTIONAL_TIME_DIVISION");
				ProportionalTimeDivision.increment(__skill, n, delta);
			}
		case CHOICE_OF_AGENT:
			;
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
