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
import java.util.concurrent.CopyOnWriteArrayList;

import logger.PjiitOutputter;
import strategies.Aggregate;
import strategies.CentralAssignmentTask;
import strategies.GreedyAssignmentTask;
import strategies.ProportionalTimeDivision;
import strategies.Strategy;
import tasks.CentralAssignmentOrders;
import argonauts.GranulatedChoice;
import argonauts.PersistJobDone;
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

	public static double START_ARG_MIN = 1.002;
	public static double START_ARG_MAX = -0.002;

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

	public void removeSkill(String key) {
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

	public double argmax() {
		double argmax = START_ARG_MAX;
		for (TaskInternals skill : skills.values()) {
			double p = skill.getProgress();
			if (p > argmax)
				argmax = skill.getProgress();
		}
		return argmax;
	}

	/**
	 * Less CPU ticks to get both of them
	 * 
	 * @return Aggregate - argmax and argmin for all taskinternals {argmax,
	 *         argmin}
	 */
	public Aggregate argmaxmin() {
		Aggregate arg = new Aggregate(START_ARG_MAX, START_ARG_MIN);
		for (TaskInternals skill : skills.values()) {
			double p = skill.getProgress();
			if (p < arg.argmin)
				arg.argmin = skill.getProgress();
			if (p > arg.argmax)
				arg.argmax = skill.getProgress();
		}
		return arg;
	}

	public double argmin() {
		double argmin = START_ARG_MIN;
		for (TaskInternals skill : skills.values()) {
			double p = skill.getProgress();
			if (p < argmin)
				argmin = skill.getProgress();
		}
		return argmin;
	}

	public double getGeneralAdvance() {
		double result = 0;
		for (TaskInternals skill : skills.values()) {
			result += skill.getProgress();
		}
		result = result / skills.size();
		return result;
	}

	/**
	 * For an Agent, get skills common with argument Collection<TaskInternals>
	 * skillsValues return intersection of agent skills and argument skillsValue
	 * 
	 * @param agent
	 * @param skillsValues
	 * @return return intersection of agent skills and argument skillsValue
	 */
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

	public void workOnTaskCentrallyControlled(Agent agent) {
		List<Skill> skillsImprovedList = new ArrayList<Skill>();
		CentralAssignmentOrders cao = agent.getCentralAssignmentOrders();
		CentralAssignmentTask centralAssignmentTask = new CentralAssignmentTask();
		TaskInternals taskInternal = this
				.getTaskInternals(cao.getChosenSkill());
		sanity("Choosing Si:{" + taskInternal.getSkill().getName()
				+ "} inside Ti:{" + this.toString() + "}");

		Experience experience = agent.getAgentInternalsOrCreate(
				cao.getChosenSkill()).getExperience();
		double delta = experience.getDelta();
		centralAssignmentTask.increment(this, taskInternal, 1, delta);
		experience.increment(1);

		if (SimulationParameters.deployedTasksLeave)
			TaskPool.considerEnding(this);
		skillsImprovedList.add(taskInternal.getSkill());

		PersistJobDone.addContribution(agent.getNick(), this, skillsImprovedList);
	}

	public Boolean workOnTaskFromContinuum(Agent agent,
			GranulatedChoice granulated, Strategy.SkillChoice strategy) {
		boolean workDone = false;
		
		workOnTask(agent, strategy);
		
		return workDone;
	}

	public void workOnTask(Agent agent, Strategy.SkillChoice strategy) {
		Collection<TaskInternals> intersection;
		List<Skill> skillsImprovedList = new ArrayList<Skill>();
		
		if (agent.getStrategy().taskChoice
				.equals(Strategy.TaskChoice.HETEROPHYLY_EXP_BASED)
				|| agent.getStrategy().taskChoice
						.equals(Strategy.TaskChoice.PREFERENTIAL)) {
			// heterophyly is an experience-genesis strategy
			intersection = skills.values();
		} else {
			// the intersection is always non-empty because we call
			// "workOnTask" after picking a task with witch we have
			// in common at least one skill...
			intersection = computeIntersection(agent, skills.values());
		}

		GreedyAssignmentTask greedyAssignmentTask = new GreedyAssignmentTask();
		TaskInternals singleTaskInternal = null;
		double highest = -1.;

		assert intersection != null;
		assert intersection.size() > 0;

		switch (strategy) {
		case PROPORTIONAL_TIME_DIVISION:
			say(Constraints.INSIDE_PROPORTIONAL_TIME_DIVISION);
			ProportionalTimeDivision proportionalTimeDivision = new ProportionalTimeDivision();
			for (TaskInternals singleTaskInternalFromIntersect : new CopyOnWriteArrayList<TaskInternals>(
					intersection)) {
				sanity("Choosing Si:{"
						+ singleTaskInternalFromIntersect.getSkill().getName()
						+ "} inside Ti:{"
						+ singleTaskInternalFromIntersect.toString() + "}");
				double n = intersection.size();
				double alpha = 1d / n;
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternalFromIntersect.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				proportionalTimeDivision.increment(this,
						singleTaskInternalFromIntersect, 1, alpha, delta);
				experience.increment(alpha);
				skillsImprovedList.add( singleTaskInternalFromIntersect.getSkill() );
			}
			break;
		case GREEDY_ASSIGNMENT_BY_TASK:
			say(Constraints.INSIDE_GREEDY_ASSIGNMENT_BY_TASK);
			CopyOnWriteArrayList<TaskInternals> copyIntersection = new CopyOnWriteArrayList<TaskInternals>(
					intersection);
			/**
			 * Tutaj sprawdzamy nad ktorymi taskami juz pracowano w tym tasku, i
			 * bierzemy wlasnie te najbardziej rozpoczete. Jezeli zaden nie jest
			 * rozpoczety, to bierzemy losowy
			 */
			for (TaskInternals searchTaskInternal : copyIntersection) {
				if (searchTaskInternal.getWorkDone().d > highest) {
					highest = searchTaskInternal.getWorkDone().d;
					singleTaskInternal = searchTaskInternal;
				}
			}
			/**
			 * zmienna highest zawsze jest w przedziale od [0..*]
			 */
			assert highest > -1.;
			/**
			 * musimy miec jakis pojedynczy task internal (skill) nad ktorym
			 * bedziemy pracowac..
			 */
			assert singleTaskInternal != null;

			{
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				// int n = skills.size();
				// double alpha = 1 / n;
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, singleTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add( singleTaskInternal.getSkill() );
			}
			break;
		case CHOICE_OF_AGENT:
			say(Constraints.INSIDE_CHOICE_OF_AGENT);

			/**
			 * Pracuj wylacznie nad tym skillem, w ktorym agent ma najwiecej
			 * doswiadczenia
			 */
			for (TaskInternals searchTaskInternal : new CopyOnWriteArrayList<TaskInternals>(
					intersection)) {
				if (agent.describeExperience(searchTaskInternal.getSkill()) > highest) {
					highest = agent.describeExperience(searchTaskInternal
							.getSkill());
					singleTaskInternal = searchTaskInternal;
				}
			}
			/**
			 * zmienna highest zawsze jest w przedziale od [0..*]
			 */
			assert highest != -1.;
			/**
			 * musimy miec jakis pojedynczy task internal (skill) nad ktorym
			 * bedziemy pracowac..
			 */
			assert singleTaskInternal != null;

			{
				sanity("Choosing Si:{"
						+ singleTaskInternal.getSkill().getName()
						+ "} inside Ti:{" + singleTaskInternal.toString() + "}");
				Experience experience = agent.getAgentInternalsOrCreate(
						singleTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, singleTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add( singleTaskInternal.getSkill() );
			}
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
				Experience experience = agent.getAgentInternalsOrCreate(
						randomTaskInternal.getSkill().getName())
						.getExperience();
				double delta = experience.getDelta();
				greedyAssignmentTask.increment(this, randomTaskInternal, 1,
						delta);
				experience.increment(1);
				skillsImprovedList.add( randomTaskInternal.getSkill() );
			}
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}

		if (SimulationParameters.deployedTasksLeave)
			TaskPool.considerEnding(this);
		assert skillsImprovedList.size() > 0;

		PersistJobDone.addContribution(agent.getNick(), this, skillsImprovedList);
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
	 * Returns a collection of skills inside internals of current task
	 * 
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
