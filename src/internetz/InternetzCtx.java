package internetz;

import github.TaskSkillsPool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logger.EndRunLogger;
import logger.PjiitLogger;
import logger.PjiitOutputter;
import logger.SanityLogger;
import logger.ValidationLogger;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.scenario.data.AttributeContainer;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;
import strategies.Strategy;
import strategies.StrategyDistribution;
import test.AgentTestUniverse;
import test.TaskTestUniverse;
import utils.NamesGenerator;
import au.com.bytecode.opencsv.CSVWriter;
import constants.Constraints;
import constants.ModelFactory;

/**
 * COIN network emergence simulator, successfully moved to Repast Simphony 2.1
 * for better performance
 * 
 * @version 1.3 "Bobo bear"
 * @since 1.0
 * @author Oskar Jarczyk (since 1.0+)
 * @see 1) github markdown
 * 		2) "On the effectiveness of emergent task allocation"
 */
public class InternetzCtx extends DefaultContext<Object> {

	private StrategyDistribution strategyDistribution;

	private ModelFactory modelFactory;
	private SkillFactory skillFactory;
	private Schedule schedule = new Schedule();

	private TaskPool taskPool = new TaskPool();
	private AgentPool agentPool = new AgentPool();

	private List<Agent> listAgent;

	// private Network<Agent> agents = null;
	// private Network<Task> tasks = null;
	// private Projection<?> agentsProjected = null;

	public InternetzCtx() {
		super("InternetzCtx");

		try {
			initializeLoggers();

			say("Super object InternetzCtx loaded");
			// getting parameters of simulation
			say(Constraints.LOADING_PARAMETERS);

			SimulationParameters.init();
			modelFactory = new ModelFactory(SimulationParameters.model_type);
			say("Starting simulation with model: " + modelFactory.toString());
			
			if (modelFactory.getFunctionality().ordinal() > 0)
				initializeValidationLogger();
			
			// TODO: implement mixed strategy distribution
			strategyDistribution = new StrategyDistribution();

			// initialize skill pools
			skillFactory = new SkillFactory();
			skillFactory.buildSkillsLibrary();

			say("SkillFactory parsed all skills from CSV file");
		} catch (IOException e) {
			e.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER);
		} catch (Exception exc) {
			say(exc.toString());
			exc.printStackTrace();
			say(Constraints.ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS);
		}

		try {
			AgentSkillsPool
					.instantiate(SimulationParameters.agentSkillPoolDataset);
			say("Instatiated AgentSkillsPool");
			TaskSkillsPool
					.instantiate(SimulationParameters.taskSkillPoolDataset);
			say("Instatied TaskSkillsPool");

			strategyDistribution
					.setType(SimulationParameters.strategyDistribution);
			strategyDistribution
					.setSkillChoice(SimulationParameters.skillChoiceAlgorithm);
			strategyDistribution
					.setTaskChoice(SimulationParameters.taskChoiceAlgorithm);
			strategyDistribution
					.setTaskMinMaxChoice(SimulationParameters.taskMinMaxChoiceAlgorithm);
		} catch (Exception exc) {
			exc.printStackTrace();
			say(Constraints.UNKNOWN_EXCEPTION);
		}

		this.addSubContext(agentPool);
		this.addSubContext(taskPool);

		// NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
		// "agents", (Context<Object>) this, false);
		// netBuilder.buildNetwork();

		// agents = (Network<Agent>) this.getProjection("agents");
		// say("Projection agents (" + agents.getName() +
		// ") exists and is size: "
		// + agents.size());
		// agentsProjected = this.getProjection("agents");
		// agentsProjected.

		initializeTasks();
		initializeAgents();

		// Network<Agent> agents = (Network<Agent>)
		// this.getProjection("agents");
		// say("Projection agents (" + agents.getName() +
		// ") exists and is size: "
		// + agents.size());

		say("Task choice algorithm is "
				+ SimulationParameters.taskChoiceAlgorithm);
		System.out.println("Number of teams created "
				+ this.getObjects(Task.class).size());
		System.out.println("Number of agents created "
				+ this.getObjects(Agent.class).size());
		System.out.println("Algorithm tested: "
				+ SimulationParameters.taskChoiceAlgorithm);

		try {
			outputAgentSkillMatrix();
		} catch (IOException e) {
			say(Constraints.IO_EXCEPTION);
			e.printStackTrace();
		}

		if (SimulationParameters.forceStop)
			RunEnvironment.getInstance().endAt(SimulationParameters.numSteps);

		List<ISchedulableAction> actions = schedule.schedule(this);
		say(actions.toString());
	}

	private void initializeLoggers() throws IOException {
		PjiitLogger.init();
		say(Constraints.LOGGER_INITIALIZED);
		SanityLogger.init();
		sanity(Constraints.LOGGER_INITIALIZED);
		EndRunLogger.init();
	}

	private void initializeAgents() {
		switch(modelFactory.getFunctionality()){
		case NORMAL:
			addAgents(SimulationParameters.agentCount);
			break;
		case VALIDATION:
			listAgent = new ArrayList<Agent>();
			initializeValidationAgents();
			break;
		case NORMAL_AND_VALIDATION:
			//TODO implement it later...
			break;
		}
	}
	
	private void initializeValidationAgents() {
		for (Agent agent : AgentTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(agent),
					strategyDistribution.getTaskMaxMinStrategy(agent),
					strategyDistribution.getSkillStrategy(agent));

			agent.setStrategy(strategy);
			listAgent.add(agent);
			say(agent.toString() + " added to pool.");
			// Required adding agent to context
			// this.add(agent);
			agentPool.add(agent);
		}
	}

	protected void initializeTasks() {
		switch(modelFactory.getFunctionality()){
		case NORMAL:
			initializeTasksNormally();
			break;
		case VALIDATION:
			initalizeValidationTasks();
			break;
		case NORMAL_AND_VALIDATION:
			//TODO implement it later...
			break;
		}
	}
	
	private void initalizeValidationTasks(){
		for (Task task : TaskTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			taskPool.addTask(task.getName(), task);
			taskPool.add(task);
			agentPool.add(task);
		}
	}
	
	private void initializeTasksNormally(){
		for (int i = 0; i < SimulationParameters.taskCount; i++) {
			Task task = new Task();
			say("Creating task..");
			taskPool.addTask(task.getName(), task);
			say("Initializing task..");
			task.initialize();
			taskPool.add(task);
			agentPool.add(task);
		}
	}
	
	private void initializeValidationLogger(){
		ValidationLogger.init();
		say(Constraints.VALIDATION_LOGGER_INITIALIZED);
	}

	private void outputAgentSkillMatrix() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("input_a1.csv"), ',',
				CSVWriter.NO_QUOTE_CHARACTER);
		for (Agent agent : listAgent) {
			for (AgentInternals __agentInternal : agent.getAgentInternals()) {
				ArrayList<String> entries = new ArrayList<String>();
				entries.add(agent.getNick());
				entries.add(__agentInternal.getExperience().value + "");
				entries.add(__agentInternal.getSkill().getName());
				String[] stockArr = new String[entries.size()];
				stockArr = entries.toArray(stockArr);
				writer.writeNext(stockArr);
			}
		}
		writer.close();
	}

	private void addAgents(int agentCnt) {
		listAgent = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgent.get(i);

			Strategy strategy = new Strategy(
					strategyDistribution.getTaskStrategy(agent),
					strategyDistribution.getTaskMaxMinStrategy(agent),
					strategyDistribution.getSkillStrategy(agent));

			agent.setStrategy(strategy);
			say(agent.toString());
			say("in add aggent i: " + i);
			// Required adding agent to context
			// this.add(agent);
			agentPool.add(agent);
		}
	}

	private void outputAgentNetworkData() {
		Network<?> agents = (Network<?>) this.getProjection("agents");
		Projection<?> agentsProjected = this.getProjection("agents");

		Iterator<?> allNodes = agents.getEdges().iterator();
		for (Object obj : agents.getNodes()) {
			say("Agent network data output --- " + ((Agent) obj).toString());
		}
	}

	// private boolean moreThanBasic() {
	// return modelFactory.getComplexity() > 0;
	// }

	@ScheduledMethod(start = 2000, priority = 0)
	public void outputSNSData() throws IOException {
		say("outputSNSData() check launched");
		// outputAgentNetworkData();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void finishSimulation() {
		say("finishSimulation() check launched");
		EnvironmentEquilibrium.setActivity(false);
		if (taskPool.getCount() < 1) {
			finalMessage(RunState.getInstance().getRunInfo().getBatchNumber()
					+ ","
					+ RunState.getInstance().getRunInfo().getRunNumber()
					+ ","
					+ RunEnvironment.getInstance().getCurrentSchedule()
							.getTickCount() + ","
					+ SimulationParameters.taskChoiceAlgorithm + ","
					+ SimulationParameters.fillAgentSkillsMethod + ","
					+ SimulationParameters.agentSkillPoolDataset + ","
					+ SimulationParameters.taskSkillPoolDataset + ","
					+ SimulationParameters.skillChoiceAlgorithm);
			RunEnvironment.getInstance().endRun();
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void checkForActivity() {
		say("checkForActivity() check launched");
		if (EnvironmentEquilibrium.getActivity() == false) {
			finalMessage(RunState.getInstance().getRunInfo().getBatchNumber()
					+ ","
					+ RunState.getInstance().getRunInfo().getRunNumber()
					+ ","
					+ RunEnvironment.getInstance().getCurrentSchedule()
							.getTickCount() + ","
					+ SimulationParameters.taskChoiceAlgorithm + ","
					+ SimulationParameters.fillAgentSkillsMethod + ","
					+ SimulationParameters.agentSkillPoolDataset + ","
					+ SimulationParameters.taskSkillPoolDataset + ","
					+ SimulationParameters.skillChoiceAlgorithm + ","
					+ SimulationParameters.taskMinMaxChoiceAlgorithm);
			RunEnvironment.getInstance().endRun();
		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	private void finalMessage(String s) {
		EndRunLogger.finalMessage(s);
	}

}