package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import logger.PjiitOutputter;
import repast.simphony.context.DefaultContext;
import repast.simphony.random.RandomHelper;
import strategies.Strategy;
import tasks.ArgMinMax;
import tasks.CentralAssignment;
import tasks.Heterophyly;
import tasks.HeterophylyExpBased;
import tasks.Homophyly;
import tasks.HomophylyExpBased;
import tasks.Preferential;
import constants.Constraints;

public class TaskPool extends DefaultContext<Task> {

	private static Map<String, Task> tasks = new HashMap<String, Task>();

	public TaskPool() {
		super("Tasks");
	}

	public static void clearTasks() {
		tasks.clear();
	}

	public void addTask(String key, Task task) {
		tasks.put(key, task);
		say("Task added successfully to pool. Pool size: " + getCount());
	}

	public Task getTask(String key) {
		return tasks.get(key);
	}

	public Collection<Task> getTasks() {
		return tasks.values();
	}

	/**
	 * Count tasks in the pool
	 * 
	 * @return Task pool size, in other words, count of the task in the
	 *         simulation universe (positive int)
	 */
	public int getCount() {
		return tasks.size();
	}

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy) {
		Task chosen = null;
		assert strategy != null;

		switch (strategy) {
		case HOMOPHYLY_EXP_BASED:
			HomophylyExpBased homophylyExpBased = new HomophylyExpBased(tasks);
			chosen = homophylyExpBased.concludeMath(agent);
			break;
		case HETEROPHYLY_EXP_BASED:
			// it will be basically negation of homophyly
			HeterophylyExpBased heterophylyExpBased = new HeterophylyExpBased(
					tasks);
			chosen = heterophylyExpBased.concludeMath(agent);
			break;
		case HOMOPHYLY_CLASSIC:
			Homophyly homophyly = new Homophyly(tasks);
			chosen = homophyly.concludeMath(agent);
			break;
		case HETEROPHYLY_CLASSIC:
			Heterophyly heterophyly = new Heterophyly(tasks);
			chosen = heterophyly.concludeMath(agent);
			break;
		case SOCIAL_VECTOR:
			// TODO: check if you added "category" attribute to Skills
			;
			break;
		case PREFERENTIAL:
			Preferential preferential = new Preferential(tasks);
			chosen = preferential.concludeMath(agent);
			if (chosen == null)
				say(Constraints.DIDNT_FOUND_TASK_TO_WORK_ON);
			break;
		case RANDOM:
			ArrayList<Task> tasksWithMatchingSkills = new ArrayList<Task>();
			Collection<Skill> allAgentSkills = agent.getSkills();
			for (Task singleTaskFromPool : tasks.values()) {
				for (Skill singleSkill : allAgentSkills) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						tasksWithMatchingSkills.add(singleTaskFromPool);
					}
				}
			}
			if (tasksWithMatchingSkills.size() > 0) {
				chosen = tasksWithMatchingSkills.get(RandomHelper
						.nextIntFromTo(0, tasksWithMatchingSkills.size() - 1));
			} else {
				say("Didn't found task with such skills which agent have!");
			}
			break;
		case COMPARISION:
			;
			break;
		case CENTRAL_ASSIGNMENT:
			CentralAssignment centralAssignment = new CentralAssignment();
			chosen = centralAssignment.concludeMath(agent);
			break;
		case MACHINE_LEARNED:
			;
			break;
		case ARG_MIN_MAX:
			ArgMinMax argMinMax = new ArgMinMax(tasks);
			chosen = argMinMax.concludeMath(agent);
			break;
		default:
			assert false; // there is no default method, so please never happen
			break;
		}
		if (chosen != null) {
			sanity("Agent " + agent.toString() + " uses strategy "
					+ agent.getStrategy() + " and chooses task "
					+ chosen.getId() + " to work on.");
		} else {
			sanity("Agent " + agent.toString() + " uses strategy "
					+ agent.getStrategy()
					+ " but didnt found any task to work on.");
			if (SimulationParameters.allwaysChooseTask) {
				sanity(Constraints.ROOKIE);
				ArrayList<Task> choseRandomFromThis = new ArrayList<Task>();
				for (Task singleTaskFromPool : tasks.values()) {
					if (singleTaskFromPool.getGeneralAdvance() < 1.) {
						choseRandomFromThis.add(singleTaskFromPool);
					}
				}
				if (choseRandomFromThis.size() > 0)
					chosen = choseRandomFromThis.get(RandomHelper
							.nextIntFromTo(0, choseRandomFromThis.size() - 1));
			}
		}
		return chosen;
	}

	public static HashMap<Skill, ArrayList<Task>> getTasksWithoutSkills(
			Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : tasks.values()) {

				Collection<Skill> ts = task.getSkills();
				if (!ts.contains(skill)) {
					ArrayList<Task> value = result.get(skill);
					if (value == null) {
						result.put(skill, new ArrayList<Task>());
						value = result.get(skill);
					}
					value.add(task);
					result.put(skill, value);
				}
			}
		}
		return result;
	}

	public static HashMap<Skill, ArrayList<Task>> getTasksPerSkills(
			Collection<Skill> c) {
		HashMap<Skill, ArrayList<Task>> result = new HashMap<Skill, ArrayList<Task>>();
		for (Skill skill : c) {
			for (Task task : tasks.values()) {

				Collection<Skill> ts = task.getSkills();
				if (ts.contains(skill)) {
					ArrayList<Task> value = result.get(skill);
					if (value == null) {
						result.put(skill, new ArrayList<Task>());
						value = result.get(skill);
					}
					value.add(task);
					result.put(skill, value);
				}
			}
		}
		return result;
	}

	public static void considerEnding(Task task) {
		boolean notfinished = false;
		for (TaskInternals taskInternal : task.getTaskInternals().values()) {
			if (taskInternal.getWorkDone().d < taskInternal.getWorkRequired().d) {
				notfinished = true;
				break;
			}
		}
		if (!notfinished) {
			tasks.remove(task.getName());
			sanity("Task id:" + task.getId() + " name:" + task.getName()
					+ " is depleted and leaving the environment");
		}
	}

	private static void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
