package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import argonauts.PersistJobDone;

import repast.simphony.context.DefaultContext;
import strategies.Strategy;

import logger.PjiitOutputter;

public class TaskPool extends DefaultContext<Task> {

	private static Map<String, Task> tasks = new HashMap<String, Task>();

	public TaskPool() {
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
		case HOMOPHYLY:

			Collection<Skill> c = null;

			if (agent.wasWorkingOnAnything()) {
				// describe what he was working on..
				Map<Integer, Task> desc = PersistJobDone.getContributions(agent
						.getNick());
				assert desc.size() > 0;
				int highest = 0;
				Task mostOften = null;

				ArrayList<Task> shuffled = new ArrayList<Task>(desc.values());
				Collections.shuffle(shuffled);
				assert shuffled.size() > 0;

				for (Task oneOfTheShuffled : shuffled) {
					int taskFruequency = Collections.frequency(
							desc.values(), oneOfTheShuffled);
					if (taskFruequency > highest) {
						mostOften = oneOfTheShuffled;
					}
				}
				
				assert mostOften != null;

				c = mostOften.getSkills();
			} else {
				// he wasn't working on anything, take skill matrix
				c = agent.getSkills();
				// take all the skills
			}
			assert c.size() > 0;
			
			HashMap<Skill, ArrayList<Task>> h = getTasksPerSkills(c);
			// create list of tasks per a skill
			if (h.size() < 1){
				h = getTasksPerSkills(agent.getSkills());
			}

			HashMap<Task, Integer> inters = searchForIntersection(h);
			// search for intersections of n-size

			if (inters == null || inters.size() == 0) {
				say("Didn't found task with such skills which agent have!");
				break;
			}

			Collection<Integer> ci = inters.values();
			Integer maximum = Collections.max(ci);

			ArrayList<Task> intersection = new ArrayList<Task>();
			for (Task task__ : inters.keySet()) {
				if (inters.get(task__) == maximum) {
					intersection.add(task__);
				}
			}
			// take biggest intersection set possible

			chosen = intersection
					.get((int) ((new Random().nextDouble()) * intersection
							.size()));
			// random

			break;
		case HETEROPHYLY:
			// it will be basicly negation of homophyly
			if (agent.wasWorkingOnAnything()) {
				// describe what he was working on..
				Map<Integer, Task> desc = PersistJobDone.getContributions(agent
						.getNick());
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

	private static HashMap<Task, Integer> searchForIntersection(
			HashMap<Skill, ArrayList<Task>> h) {
		// TODO Auto-generated method stub
		HashMap<Task, Integer> map = new HashMap<Task, Integer>();
		for (Skill skill : h.keySet()) {
			ArrayList<Task> t = h.get(skill);
			for (Task task : t) {
				if (map.containsKey(task)) {
					map.put(task, map.get(task) + 1);
				} else {
					map.put(task, 0);
				}
			}
		}
		return map;
	}

	private static HashMap<Skill, ArrayList<Task>> getTasksPerSkills(
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
