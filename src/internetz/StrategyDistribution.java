package internetz;

import internetz.Strategy.SkillChoice;
import internetz.Strategy.TaskChoice;

public class StrategyDistribution {
	
	public static final int SINGULAR = 0;
	public static final int MULTIPLE = 1;
	
	private int type;
	private String skillChoice;
	private String taskChoice;
	
	public TaskChoice getTaskStrategy(Agent agent){
		if (type == 0){
			if (taskChoice.equals("preferential")){
				return Strategy.TaskChoice.RANDOM;
			}
			if (taskChoice.equals("random")){
				return Strategy.TaskChoice.RANDOM;
			}
			if (taskChoice.equals("comparision")){
				return Strategy.TaskChoice.RANDOM;
			}
			if (taskChoice.equals("machine_learned")){
				return Strategy.TaskChoice.RANDOM;
			}
		}
		return null;
	}
	
	public SkillChoice getSkillStrategy(Agent agent){
		if (type == 0){
			if (skillChoice.equals("proportional")){
				return Strategy.SkillChoice.PROPORTIONAL_TIME_DIVISION;
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
