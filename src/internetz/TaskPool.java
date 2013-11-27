package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import constants.Constraints;
import logger.PjiitOutputter;
import repast.simphony.context.DefaultContext;
import strategies.Aggregate;
import strategies.Strategy;
import argonauts.PersistJobDone;

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
		assert strategy != null;
		
		switch (strategy) {
		// *******************************************************************************
		case HOMOPHYLY_EXP_BASED:
			assert agent != null;
			Collection<Skill> skillsByExperienceHmphly = null;
			say("Starting chooseTask consideration inside homophyly for "
					+ agent.getNick());
			if (agent.wasWorkingOnAnything()) {
				// describe what he was working on..
				Map<Integer, Task> desc = PersistJobDone.getContributions(agent
						.getNick());
				assert desc.size() > 0;
				say("Agent " + agent.getNick()
						+ " already have experience in count of: "
						+ desc.size());
				int highest = 0;
				Task mostOften = null;

				ArrayList<Task> shuffled = new ArrayList<Task>(desc.values());
				Collections.shuffle(shuffled);
				assert shuffled.size() > 0;

				for (Task oneOfTheShuffled : shuffled) {
					int taskFruequency = Collections.frequency(desc.values(),
							oneOfTheShuffled);
					if (taskFruequency > highest) {
						mostOften = oneOfTheShuffled;
					}
				}

				assert mostOften != null;

				skillsByExperienceHmphly = intersectWithAgentSkills(agent,
						mostOften.getSkills());
			} else {
				// he wasn't working on anything, take skill matrix
				skillsByExperienceHmphly = agent.getSkills();
				// take all the skills
			}

			if (skillsByExperienceHmphly.size() < 1) {
				skillsByExperienceHmphly = agent.getSkills();
			}

			assert skillsByExperienceHmphly.size() > 0;

			// create list of tasks per a skill
			HashMap<Skill, ArrayList<Task>> tasksPerSkillsHmphly = getTasksPerSkills(skillsByExperienceHmphly);
			// there are no tasks left with such experience ?
			// there is nothing to do
			if (tasksPerSkillsHmphly.size() < 1) {
				// tasksPerSkillsHmphly = getTasksPerSkills(agent.getSkills());
				say(Constraints.DIDNT_FOUND_TASK);
				break;
			}

			HashMap<Task, Integer> intersectionHomophyly = null;

			if (tasksPerSkillsHmphly != null)
				intersectionHomophyly = searchForIntersection(tasksPerSkillsHmphly);
			// search for intersections of n-size

			if (intersectionHomophyly == null
					|| intersectionHomophyly.size() == 0) {
				say(Constraints.DIDNT_FOUND_TASK);
				break;
			}

			Collection<Integer> collectionIntersection = intersectionHomophyly
					.values();
			Integer maximumFromIntersect = Collections
					.max(collectionIntersection);

			ArrayList<Task> tasksWithMaxHomophyly = new ArrayList<Task>();
			for (Task commonTask : intersectionHomophyly.keySet()) {
				if (intersectionHomophyly.get(commonTask) == maximumFromIntersect) {
					tasksWithMaxHomophyly.add(commonTask);
				}
			}
			// take biggest intersection set possible
			chosen = tasksWithMaxHomophyly.get((int) ((new Random()
					.nextDouble()) * tasksWithMaxHomophyly.size()));
			// random

			break;
		// *******************************************************************************
		case HETEROPHYLY_EXP_BASED:
			// it will be basically negation of homophyly

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
					int taskFruequency = Collections.frequency(desc.values(),
							oneOfTheShuffled);
					if (taskFruequency > highest) {
						mostOften = oneOfTheShuffled;
					}
				}

				assert mostOften != null;

				c = intersectWithAgentSkills(agent, mostOften.getSkills());
			} else {
				// he wasn't working on anything, take skill matrix
				c = agent.getSkills();
				// take all the skills
			}
			if (c.size() < 1) {
				c = agent.getSkills();
			}

			assert c.size() > 0;

			// create list of tasks per a skill
			HashMap<Skill, ArrayList<Task>> h = getTasksWithoutSkills(c);
			// there are no tasks left with such conditions ?
			// try again but now with agent skills
			if (h.size() < 1) {
				h = getTasksWithoutSkills(agent.getSkills());
			}
			// there are no tasks left with such conditions ?
			// try again but now with homophyly acceptance
			if (h.size() < 1) {
				h = getTasksPerSkills(agent.getSkills());
			}

			HashMap<Task, Integer> inters = null;

			if (h != null)
				inters = searchForIntersection(h);
			// Rewrite the collection to hashed by tasks

			if (inters == null || inters.size() == 0) {
				say("Didn't found task with such skills which agent don't have!");
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
		case HOMOPHYLY_CLASSIC:
			Task taskWithHighestSG = null;
			double found_sigma_delta = 0;
			//ArrayList<Task> tasksWithMatchingSkillsHomCl = new ArrayList<Task>();
			Collection<Skill> allAgentSkillsHomCl = agent.getSkills();
			for (Task singleTaskFromPool : tasks.values()) {
				double sigma_delta = 0;
				boolean consider = false;
				for (Skill singleSkill : allAgentSkillsHomCl) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						consider = true;
						sigma_delta += singleTaskFromPool.
								getTaskInternals(singleSkill.getName()).getProgress();
					}
				}
				if (consider){
					if (taskWithHighestSG == null) {
						taskWithHighestSG = singleTaskFromPool;
						found_sigma_delta = sigma_delta;
					} else {
						if (found_sigma_delta < sigma_delta) {
							taskWithHighestSG = singleTaskFromPool;
						}
					}
				}
			}
			if (taskWithHighestSG != null){
				chosen = taskWithHighestSG;
			} else {
				// intersection is empty, chose random
				if (tasks.size() > 0)
					chosen = tasks.get(new Random().nextInt(tasks.size()));
				else
					chosen = null;
			}
			break;
		case HETEROPHYLY_CLASSIC:
			Task taskWithLowestSG = null;
			double found_sigma_delta_hetero = 0;
			//ArrayList<Task> tasksWithMatchingSkillsHomCl = new ArrayList<Task>();
			Collection<Skill> allAgentSkillsHetCl = agent.getSkills();
			for (Task singleTaskFromPool : tasks.values()) {
				double sigma_delta = 0;
				boolean consider = false;
				for (Skill singleSkill : allAgentSkillsHetCl) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						consider = true;
						sigma_delta += singleTaskFromPool.
								getTaskInternals(singleSkill.getName()).getProgress();
					}
				}
				if (consider){
					if (taskWithLowestSG == null) {
						taskWithLowestSG = singleTaskFromPool;
						found_sigma_delta = sigma_delta;
					} else {
						if (found_sigma_delta_hetero < sigma_delta) {
							taskWithLowestSG = singleTaskFromPool;
						}
					}
				}
			}
			if (taskWithLowestSG != null){
				chosen = taskWithLowestSG;
			} else {
				// intersection is empty, chose random
				if (tasks.size() > 0)
					chosen = tasks.get(new Random().nextInt(tasks.size()));
				else
					chosen = null;
			}
			break;
		case SOCIAL_VECTOR:
			// TODO: check if you added "category" attribute to Skills
			;
			break;
		case PREFERENTIAL:
			Task mostAdvanced = null;
			ArrayList<Task> tasksWithMatchingSkillsPref = new ArrayList<Task>();
			Collection<Skill> allAgentSkillsPref = agent.getSkills();
			for (Task singleTaskFromPool : tasks.values()) {
				internal:for (Skill singleSkill : allAgentSkillsPref) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						tasksWithMatchingSkillsPref.add(singleTaskFromPool);
						if (mostAdvanced == null) {
							mostAdvanced = singleTaskFromPool;
						} else {
							if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
									.getGeneralAdvance()) {
								mostAdvanced = singleTaskFromPool;
							}
						}
						break internal;
					}
				}
			}

			if ( (tasksWithMatchingSkillsPref.size() < 1) || (mostAdvanced == null) ) {
				//mostAdvanced = null;
				for (Task singleTaskFromPool : tasks.values()) {
					if (mostAdvanced == null) {
						mostAdvanced = singleTaskFromPool;
					} else {
						if (mostAdvanced.getGeneralAdvance() < singleTaskFromPool
								.getGeneralAdvance()) {
							mostAdvanced = singleTaskFromPool;
						}
					}
				}
			}
			
			//assert mostAdvanced != null;

			if (mostAdvanced != null) {
				chosen = mostAdvanced;
			} else {
				say(Constraints.DIDNT_FOUND_TASK_TO_WORK_ON);
			}
			break;
		// *******************************************************************************
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
		// *******************************************************************************
		case ARG_MIN_MAX:

			ArrayList<Task> tasksWithMatchingSkillsMinMax = new ArrayList<Task>();
			Collection<Skill> allAgentSkillsPreMinMax = agent.getSkills();
			for (Task singleTaskFromPool : tasks.values()) {
				for (Skill singleSkill : allAgentSkillsPreMinMax) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						tasksWithMatchingSkillsMinMax.add(singleTaskFromPool);
					}
				}
			}

			if (tasksWithMatchingSkillsMinMax.size() < 1) {
				say("Didn't found task with such skills which agent have!");
				break;
			}

			Collection<Skill> allAgentSkillsMinMax = agent.getSkills();

			Random generator = new Random();
			Task randomValue = tasksWithMatchingSkillsMinMax.get(generator
					.nextInt(tasksWithMatchingSkillsMinMax.size()));

			Task argMaxMax = randomValue;
			Task argMaxMin = randomValue;
			Task argMinMax = randomValue;
			Task argMinMin = randomValue;

			for (Task singleTaskFromPool : tasks.values()) {
				boolean consider = false;
				for (Skill singleSkill : allAgentSkillsMinMax) {
					if (singleTaskFromPool.getTaskInternals().containsKey(
							singleSkill.toString())) {
						consider = true;
						break;
					}
				}
				if (consider) {
					Aggregate aggregate = singleTaskFromPool.argmaxmin();
					assert aggregate != null;
					switch (agent.getStrategy().taskMinMaxChoice) {
					case ARGMAX_ARGMAX:
						if (aggregate.argmax > argMaxMax.argmax()) {
							argMaxMax = singleTaskFromPool;

						}
						chosen = argMaxMax;
						break;
					case ARGMAX_ARGMIN:
						if (aggregate.argmin > argMaxMax.argmin()) {
							argMaxMin = singleTaskFromPool;

						}
						chosen = argMaxMin;
						break;
					case ARGMIN_ARGMAX:
						if (aggregate.argmax < argMaxMax.argmax()) {
							argMinMax = singleTaskFromPool;

						}
						chosen = argMinMax;
						break;
					case ARGMIN_ARGMIN:
						if (aggregate.argmin < argMaxMax.argmin()) {
							argMinMin = singleTaskFromPool;

						}
						chosen = argMinMin;
						break;
					default:
						assert false; // should never happen
						break;
					}
				}
			}
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

	private static Collection<Skill> intersectWithAgentSkills(Agent agent,
			Collection<Skill> skills) {
		List<Skill> result = new ArrayList<Skill>();
		for (Skill skill : skills) {
			if (agent.getSkills().contains(skill)) {
				result.add(skill);
			}
		}
		return result;
	}

	/**
	 * This method counts the frequency of a 'Task' in HashMap<Skill,
	 * ArrayList<Task>> and returns collection of tasks and their frequencies
	 * 
	 * @param h
	 *            - HashMap of skills and tasks which require them
	 * @return HashMap<Task, Integer>
	 */
	private static HashMap<Task, Integer> searchForIntersection(
			HashMap<Skill, ArrayList<Task>> h) {

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

	private static HashMap<Skill, ArrayList<Task>> getTasksWithoutSkills(
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
