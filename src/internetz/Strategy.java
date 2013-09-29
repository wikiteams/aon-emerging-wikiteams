package internetz;

public class Strategy {
	
	public TaskChoice taskChoice;
	public SkillChoice skillChoice;
	
	public enum TaskChoice {
		HETEROPHYLY_HOMOPHYLY,
		RANDOM,
		COMPARISION,
		MACHINE_LEARNED
	}
	
	public enum SkillChoice {
	    PROPORTIONAL_TIME_DIVISION,
	    GREEDY_ASSIGNMENT_BY_TASK,
	    CHOICE_OF_AGENT
	}

}
