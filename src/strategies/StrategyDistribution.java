package strategies;

import strategies.Strategy.SkillChoice;
import strategies.Strategy.TaskChoice;
import internetz.Agent;

public class StrategyDistribution {

	public static final int SINGULAR = 0;
	public static final int MULTIPLE = 1;

	private int type;
	private String skillChoice;
	private String taskChoice;

	public TaskChoice getTaskStrategy(Agent agent) {
		if (type == 0) {
			if (taskChoice.equals("preferential")) {
				return Strategy.TaskChoice.HOMOPHYLY;
			} else if (taskChoice.equals("random")) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals("comparision")) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals("social_vector")) {
				return Strategy.TaskChoice.RANDOM;
			} else if (taskChoice.equals("machine_learned")) {
				return Strategy.TaskChoice.RANDOM;
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
			} else if (skillChoice.equals("greedy")) {
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

}
