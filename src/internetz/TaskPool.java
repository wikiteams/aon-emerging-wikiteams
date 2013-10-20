package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import argonauts.PersistJobDone;

import repast.simphony.context.DefaultContext;
import strategies.Strategy;

import logger.PjiitOutputter;

public class TaskPool extends DefaultContext<Task> {

	private static Map<String, Task> tasks = new HashMap<String, Task>();
	
	public TaskPool(){
		super("Tasks");
	}

	public void addTask(String key, Task task) {
		tasks.put(key, task);
		say("Task added successfully to pool. Pool size: " + getCount());
	}

	public Task getTask(String key) {
		return tasks.get(key);
	}

	public int getCount() {
		return tasks.size();
	}

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy) {

		Task chosen = null;
		switch (strategy) {
		case HETEROPHYLY:
			if (agent.wasWorkingOnAnything()){
				// describe what he was working on..
				Map<Integer, Task> desc = PersistJobDone.getContributions(agent.getNick());
			} else {
				// he wasn't working on anything, take skill matrix
				;
			}
			break;
		case HOMOPHYLY:
			if (agent.wasWorkingOnAnything()){
				// describe what he was working on..
				Map<Integer, Task> desc = PersistJobDone.getContributions(agent.getNick());
			} else {
				// he wasn't working on anything, take skill matrix
				// and negate..
				;
			}
			break;
		case SOCIAL_VECTOR:
			// TODO: check if you added "category" attribute to Skills
			;
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
				chosen = tasksWithMatchingSkills.get(new Random()
						.nextInt(tasksWithMatchingSkills.size()));
			} else {
				say("Didn't found task with such skills which agent have!");
			}
			break;
		case COMPARISION:
			;
			break;
		case MACHINE_LEARNED:
			;
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
		}
		return chosen;
	}

	private static void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
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

}
