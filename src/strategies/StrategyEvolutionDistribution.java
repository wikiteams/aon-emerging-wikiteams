package strategies;

import internetz.Agent;
import internetz.SimulationParameters;
import strategies.Strategy.TaskChoice;

public class StrategyEvolutionDistribution extends StrategyDistribution {
	Double[][] evolutionPlans = {
		{0.8, 0.2, 0.0}, 
		{0.8, 0.0, 0.2},
		{0.2, 0.8, 0.0},
		{0.0, 0.8, 0.2},
		{0.2, 0.0, 0.8},
		{0.0, 0.2, 0.8},
		{0.33, 0.33, 0.33}
	};
	
	Integer strategyCounter1 = 0;
	Integer strategyCounter2 = 0;
	Integer strategyCounter3 = 0;
	
	public StrategyEvolutionDistribution() {
	}
	
	public TaskChoice getTaskStrategy(Agent agent) {
		Double proportion1 = (double)this.strategyCounter1 / (double)SimulationParameters.agentCount;
		Double proportion2 = (double)this.strategyCounter2 / (double)SimulationParameters.agentCount;
		Double proportion3 = (double)this.strategyCounter3 / (double)SimulationParameters.agentCount;
		
		Integer selected = 1;
		
		if(proportion1 < this.evolutionPlans[selected][0]) {
			this.strategyCounter1++;
			return Strategy.TaskChoice.HOMOPHYLY_CLASSIC;
		} else if (proportion2 < this.evolutionPlans[selected][1]) {
			this.strategyCounter2++;
			return Strategy.TaskChoice.HETEROPHYLY_CLASSIC;
		} else {
			this.strategyCounter3++;
			return Strategy.TaskChoice.HOMOPHYLY_CLASSIC;
		}
	}
}
