/**
 * 
 */
package internetz;

import github.TaskSkillsPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import argonauts.PersistJobDone;

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
 * @version 1.2
 * @author Oskar Jarczyk
 */
public class Task {

	private static int idIncrementalCounter = 0;

	private String name;
	private int id;

	private Map<String, TaskInternals> skills = new HashMap<String, TaskInternals>();

	public Task() {
		this.id = ++idIncrementalCounter;
		this.name = "Task_" + this.id;
		say("Task object " + this + " created");
	}

	public void addSkill(String key, TaskInternals taskInternals) {
		skills.put(key, taskInternals);
	}
	
	public void removeSkill(String key){
		skills.remove(key);
	}

	public TaskInternals getTaskInternals(String key) {
		return skills.get(key);
	}

	public synchronized void initialize() {
		// setId(++COUNT);
		TaskSkillsPool.fillWithSkills(this);
		say("Task object initialized with id: " + this.id);
	}

	public Map<String, TaskInternals> getTaskInternals() {
		return skills;
	}

	public void setTaskInternals(Map<String, TaskInternals> skills) {
		this.skills = skills;
	}

	public int countTaskInternals() {
		return skills.size();
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

	private Collection<TaskInternals> computeIntersection(Agent agent,
			Collection<TaskInternals> skillsValues) {
		Collection<TaskInternals> returnCollection = new ArrayList<TaskInternals>();
		for (TaskInternals singleTaskInternal : skillsValues) {
			if (agent
					.getAgentInternals(singleTaskInternal.getSkill().getName()) != null) {
				returnCollection.add(singleTaskInternal);
			}
		}
		return returnCollection;
	}

	public void workOnTask(Agent agent, Strategy.SkillChoice strategy) {
		// the intersection is always non-empty because we call
		// "workOnTask" after picking a task with witch we have
		// in common at least one skill...
		Collection<TaskInternals> intersection = computeIntersection(agent,
				skills.values());
		GreedyAssignmentTask greedyAssignmentTask = new GreedyAssignmentTask();
		
		assert intersection != null;
		assert intersection.size() > 0;
		
		switch (strategy) {
		case PROPORTIONAL_TIME_DIVISION:
			say(Constraints.INSIDE_PROPORTIONAL_TIME_DIVISION);
			ProportionalTimeDivision proportionalTimeDivision = new ProportionalTimeDivision();
			for (TaskInternals singleTaskInternal : intersection) {
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				double n = intersection.size();
				double alpha = 1d / n;
				Experience experience = agent.getAgentInternals(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				proportionalTimeDivision.increment(this, singleTaskInternal, 1,
						alpha, delta);
				experience.increment(alpha);
			}
			break;
		case GREEDY_ASSIGNMENT_BY_TASK:
			say(Constraints.INSIDE_GREEDY_ASSIGNMENT_BY_TASK);
			
			TaskInternals singleTaskInternal = null;
			double highest = -1.;
			
			/**
			 * Tutaj sprawdzamy nad ktorymi taskami juz pracowano
			 * w tym tasku, i bierzemy wlasnie te najbardziej rozpoczete.
			 * Jezeli zaden nie jest rozpoczety, to bierzemy ten
			 * w ktorym mamy najwieksze doswiadczenie
			 */
			for (TaskInternals searchTaskInternal : intersection) {
				if (searchTaskInternal.getWorkDone().d > highest) {
					highest = searchTaskInternal.getWorkDone().d;
					singleTaskInternal = searchTaskInternal;
				}
			}
			
			/**
			 * zmienna highest zawsze jest w przedziale od [0..*]
			 */
			assert highest != -1.;
			/**
			 * musimy miec jakis pojedynczy task internal (skill)
			 * nad ktorym bedziemy pracowac..
			 */
			assert singleTaskInternal != null;
			
			{
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				// int n = skills.size();
				// double alpha = 1 / n;
				Experience experience = agent.getAgentInternals(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, singleTaskInternal, 1, delta);
				experience.increment(1);
			}
			break;
		case CHOICE_OF_AGENT:
			say(Constraints.INSIDE_CHOICE_OF_AGENT);
			break;
		case RANDOM:
			say(Constraints.INSIDE_RANDOM);
			Collections.shuffle((ArrayList<TaskInternals>) intersection);
			TaskInternals randomTaskInternal = ((ArrayList<TaskInternals>) intersection)
					.get(0);
			// Random generator = new Random();
			// List<String> keys = new ArrayList<String>(skills.keySet());
			// String randomKey = keys.get(generator.nextInt(keys.size()));
			// TaskInternals randomTaskInternal = skills.get(randomKey);
			{
				sanity("Choosing Si:{"
						+ randomTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + randomTaskInternal.toString() + "}");
				// int n = skills.size();
				// double alpha = 1 / n;
				Experience experience = agent.getAgentInternals(
						randomTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, randomTaskInternal, 1, delta);
				experience.increment(1);
			}
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}
		
		if (SimulationParameters.deployedTasksLeave)
			TaskPool.considerEnding(this);
		
		PersistJobDone.addContribution(agent.getNick(), this);
	}

	public boolean isClosed() {
		boolean result = true;
		for (TaskInternals taskInternals : skills.values()) {
			if (taskInternals.getWorkDone().d < taskInternals.getWorkRequired().d) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Returns a collection of skills inside internals
	 * of current task
	 * @return Collection of skills inside all TaskInternals
	 */
	public Collection<Skill> getSkills() {
		ArrayList<Skill> skillCollection = new ArrayList<Skill>();
		Collection<TaskInternals> internals = this.getTaskInternals().values();
		for (TaskInternals ti : internals) {
			skillCollection.add(ti.getSkill());
		}
		return skillCollection;
	}

	@Override
	public String toString() {
		return "Task " + id + " " + name;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.name.toLowerCase().equals(((Task) obj).name.toLowerCase()))
				&& (this.id == ((Task) obj).id))
			return true;
		else
			return false;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

}
