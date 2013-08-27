package internetz;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class SimulationParameters {
	
	public static int agentCount = 0;
	public static int taskCount = 0;
	public static int percStartMembership = 0;
	public static boolean allowMultiMembership = false;
	public static int numSteps = 0;
	public static String taskChoiceAlgorithm = "";
	
	public static int randomSeed = 0;
	
	public static void init() {
		Parameters param = RunEnvironment.getInstance().getParameters();
		
		agentCount = (Integer) param.getValue("agentCount");
		taskCount = (Integer) param.getValue("numTasks");
		percStartMembership = (Integer) param.getValue("percStartMembership");
		allowMultiMembership = (Boolean) param.getValue("allowMultiMembership");
		numSteps = (Integer) param.getValue("numSteps");
		taskChoiceAlgorithm = (String) param.getValue("taskChoiceAlgorithm");
		
		randomSeed = (Integer) param.getValue("randomSeed");
	}
}
