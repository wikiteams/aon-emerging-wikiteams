package internetz;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import test.Model;

/**
 * Basically stores parameters from repast file to a holder
 * Simulation Parameters holds static all execution parameters in memory
 * for more convenient access to them
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 */
public class SimulationParameters {

	public static Model model_type = null;
	public static String location = "";
	
	public static int agentCount = 0;
	public static int taskCount = 0;
	public static int percStartMembership = 0;
	public static boolean allowMultiMembership = false;
	public static int numSteps = 0;
	
	public static String taskChoiceAlgorithm = "";
	public static String skillChoiceAlgorithm = "";
	public static String taskMinMaxChoiceAlgorithm = "";
	
	public static int strategyDistribution = 0;

	public static int randomSeed = 0;
	
	public static String taskSkillPoolDataset = "";
	public static String agentSkillPoolDataset = "";
	public static int staticFrequencyTableSc = 0;
	public static String fillAgentSkillsMethod = "";
	public static String skillFactoryRandomMethod = "";
	
	public static String gitHubClusterizedDistribution = "";

	public static int agentSkillsPoolRandomize1 = 0;
	public static int agentSkillsMaximumExperience = 0;
	
	public static boolean deployedTasksLeave = false;
	public static boolean fullyLearnedAgentsLeave = false;
	public static boolean forceStop = false;
	
	public static int maxWorkRequired = 0;

	public static void init() {
		Parameters param = RunEnvironment.getInstance().getParameters();
		
		model_type = (Model) param.getValue("model_type");
		location = (String) param.getValue("location");

		agentCount = (Integer) param.getValue("agentCount");
		taskCount = (Integer) param.getValue("numTasks");
		percStartMembership = (Integer) param.getValue("percStartMembership");
		allowMultiMembership = (Boolean) param.getValue("allowMultiMembership");
		numSteps = (Integer) param.getValue("numSteps");

		taskChoiceAlgorithm = (String) param.getValue("taskChoiceAlgorithm");
		skillChoiceAlgorithm = (String) param.getValue("skillChoiceAlgorithm");
		taskMinMaxChoiceAlgorithm = (String) param.getValue("taskMinMaxChoiceAlgorithm");
		strategyDistribution = (Integer) param.getValue("strategyDistribution");
		
		taskSkillPoolDataset = (String) param.getValue("taskSkillPoolDataset");
		agentSkillPoolDataset = (String) param.getValue("agentSkillPoolDataset");
		staticFrequencyTableSc = (Integer) param.getValue("staticFrequencyTableSc");
		
		fillAgentSkillsMethod = (String) param.getValue("fillAgentSkillsMethod");
		skillFactoryRandomMethod = (String) param.getValue("skillFactoryRandomMethod");
		
		gitHubClusterizedDistribution = (String) param.getValue("gitHubClusterizedDistribution");

		randomSeed = (Integer) param.getValue("randomSeed");

		agentSkillsPoolRandomize1 = (Integer) param
				.getValue("agentSkillsPoolRandomize1");
		agentSkillsMaximumExperience = (Integer) param
				.getValue("agentSkillsMaximumExperience");
		
		maxWorkRequired = (Integer) param
				.getValue("maxWorkRequired");
		
		deployedTasksLeave = (Boolean) param
				.getValue("deployedTasksLeave");
		fullyLearnedAgentsLeave = (Boolean) param
				.getValue("fullyLearnedAgentsLeave");
		forceStop = (Boolean) param
				.getValue("forceStop");
	}
}
