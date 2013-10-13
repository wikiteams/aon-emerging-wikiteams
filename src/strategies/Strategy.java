package strategies;

/***
 * 
 * Strategy for Agent {strategy for choosing tasks}
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 1.1
 *
 */
public class Strategy {
	
	public TaskChoice taskChoice;
	public SkillChoice skillChoice;
	
	public enum TaskChoice {
		HETEROPHYLY,
		HOMOPHYLY,
		SOCIAL_VECTOR,
		RANDOM,
		COMPARISION,
		MACHINE_LEARNED
	}
	
	public enum SkillChoice {
	    PROPORTIONAL_TIME_DIVISION,
	    GREEDY_ASSIGNMENT_BY_TASK,
	    CHOICE_OF_AGENT
	}
	
	@Override
	public String toString(){
		return this.taskChoice.name() + "," + this.skillChoice.name();
	}

}
