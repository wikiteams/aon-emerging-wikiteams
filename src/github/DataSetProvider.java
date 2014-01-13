package github;

import repast.simphony.random.RandomHelper;
import internetz.SimulationParameters;

public class DataSetProvider {
	
	boolean dataSetAll = false;
	
	private String[] agent_rand_allowed = {"TOP_USERS"};
	private String[] task_rand_allowed = {"TOP_USERS"};

	public DataSetProvider(boolean dataSetAll) {
		this.dataSetAll = dataSetAll;
	}
	
	public String getAgentSkillDataset(){
		if (dataSetAll) {
			return agent_rand_allowed[
			                          RandomHelper.
			                          nextIntFromTo(
			                        		  0,agent_rand_allowed.length-1)];
		} else {
			return SimulationParameters.agentSkillPoolDataset;
		}
	}
	
	public String getTaskSkillDataset(){
		if (dataSetAll) {
			return task_rand_allowed[
			                          RandomHelper.
			                          nextIntFromTo(
			                        		  0,task_rand_allowed.length-1)];
		} else {
			return SimulationParameters.taskSkillPoolDataset;
		}
	}

}
