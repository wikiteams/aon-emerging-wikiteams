package strategies;

import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;
import strategies.Strategy.TaskMinMaxChoice;
import internetz.Agent;

public class StrategyDistribution {

	public static final int SINGULAR = 0;
	public static final int MULTIPLE = 1;

	private int type;
	
	private String skillChoice;
	private String taskChoice;
	private String taskMinMaxChoice;

	public TaskChoice getTaskStrategy(Agent agent) {
		if (type == 0) {
			if (taskChoice.equals("homophyly")) {
				return Strategy.TaskChoice.HOMOPHYLY;
			} else if (taskChoice.equals("homophyly_classic")) {
				return Strategy.TaskChoice.HOMOPHYLY_CLASSIC;
			} else if (taskChoice.equals("heterophyly_classic")) {
				return Strategy.TaskChoice.HETEROPHYLY_CLASSIC;
			} else if (taskChoice.equals("preferential")) {
				return Strategy.TaskChoice.PREFERENTIAL;
			} else if (taskChoice.equals("heterophyly")) {
				return Strategy.TaskChoice.HETEROPHYLY;
			} else if (taskChoice.equals("random")) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals("social_vector")) {
				return Strategy.TaskChoice.SOCIAL_VECTOR;
			} else if (taskChoice.equals("machine_learned")) {
				return Strategy.TaskChoice.MACHINE_LEARNED;
			} else if (taskChoice.equals("comparision")) {
				return Strategy.TaskChoice.COMPARISION;
			} else if (taskChoice.equals("minmax")) {
				return Strategy.TaskChoice.ARG_MIN_MAX;
			}
		}
		return null;
	}

	public SkillChoice getSkillStrategy(Agent agent) {
		if (type == 0) {
			if (skillChoice.equals("proportional")) {
				return Strategy.SkillChoice.PROPORTIONAL_TIME_DIVISION;
			} else if (skillChoice.equals("greedy")) {
				return Strategy.SkillChoice.GREEDY_ASSIGNMENT_BY_TASK;
			} else if (skillChoice.equals("choice")) {
				return Strategy.SkillChoice.CHOICE_OF_AGENT;
			} else if (skillChoice.equals("random")) {
				return Strategy.SkillChoice.RANDOM;
			}
		}
		return null;
	}

	public String getSkillChoice() {
		return skillChoice;
	}

	public void setSkillChoice(String skillChoice) {
		this.skillChoice = skillChoice;
	}

	public String getTaskChoice() {
		return taskChoice;
	}

	public void setTaskChoice(String taskChoice) {
		this.taskChoice = taskChoice;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public TaskMinMaxChoice getTaskMaxMinStrategy(Agent agent) {
		if (type == 0) {
			if (taskMinMaxChoice.equals("maxmax")) {
				return Strategy.TaskMinMaxChoice.ARGMAX_ARGMAX;
			} else if (taskMinMaxChoice.equals("maxmin")) {
				return Strategy.TaskMinMaxChoice.ARGMAX_ARGMIN;
			} else if (taskMinMaxChoice.equals("minmax")) {
				return Strategy.TaskMinMaxChoice.ARGMIN_ARGMAX;
			} else if (taskMinMaxChoice.equals("minmin")) {
				return Strategy.TaskMinMaxChoice.ARGMIN_ARGMIN;
			}
		}
		return null;
	}

	public String getTaskMinMaxChoice() {
		return taskMinMaxChoice;
	}

	public void setTaskMinMaxChoice(String taskMinMaxChoice) {
		this.taskMinMaxChoice = taskMinMaxChoice;
	}

}
