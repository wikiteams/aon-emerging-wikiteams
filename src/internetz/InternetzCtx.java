package internetz;

import github.DataSetProvider;
import github.TaskSkillsPool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import logger.EndRunLogger;
import logger.PjiitLogger;
import logger.PjiitOutputter;
import logger.SanityLogger;
import logger.ValidationLogger;
import logger.ValidationOutputter;

import org.apache.log4j.LogManager;

import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;
import repast.simphony.util.collections.IndexedIterable;
import strategies.CentralPlanning;
import strategies.Strategy;
import strategies.StrategyDistribution;
import test.AgentTestUniverse;
import test.Model;
import test.TaskTestUniverse;
import utils.DescribeUniverseBulkLoad;
import utils.LaunchStatistics;
import utils.NamesGenerator;
import EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArrayList;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;
import au.com.bytecode.opencsv.CSVWriter;
import constants.Constraints;
import constants.ModelFactory;

/**
 * COIN network emergence simulator, successfully moved to Repast Simphony 2.1
 * for better performance
 * 
 * @version 1.3 "Bobo Bear"
 * @since 1.0
 * @author Oskar Jarczyk (since 1.0+)
 * @see 1) github markdown 2) "On the effectiveness of emergent task allocation"
 */
public class InternetzCtx extends DefaultContext<Object> {

	private StrategyDistribution strategyDistribution;
	private ModelFactory modelFactory;
	private SkillFactory skillFactory;
	private LaunchStatistics launchStatistics;
	private Schedule schedule = new Schedule();
	private String[] universe = null;

	private volatile TaskPool taskPool = new TaskPool();
	private volatile AgentPool agentPool = new AgentPool();

	private List<Agent> listAgent;
	private CentralPlanning centralPlanningHq;

	@SuppressWarnings("unused")
	private boolean shutdownInitiated = false;
	private boolean alreadyFlushed = false;

	public InternetzCtx() {
		super("InternetzCtx");

		try {
			initializeLoggers();
			RandomHelper.init();
			clearStaticHeap();

			say("Super object InternetzCtx loaded");
			// getting parameters of simulation
			say(Constraints.LOADING_PARAMETERS);

			SimulationParameters.init();
			if (SimulationParameters.multipleAgentSets) {
				universe = DescribeUniverseBulkLoad.init();
			}

			launchStatistics = new LaunchStatistics();
			modelFactory = new ModelFactory(SimulationParameters.model_type);
			say("Starting simulation with model: " + modelFactory.toString());

			if (modelFactory.getFunctionality().isValidation())
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
			DataSetProvider dsp = new DataSetProvider(
					SimulationParameters.dataSetAll);

			AgentSkillsPool.instantiate(dsp.getAgentSkillDataset());
			say("Instatiated AgentSkillsPool");
			TaskSkillsPool.instantiate(dsp.getTaskSkillDataset());
			say("Instatied TaskSkillsPool");

			strategyDistribution
					.setType(SimulationParameters.strategyDistribution);
			strategyDistribution.setSkillChoice(modelFactory,
					SimulationParameters.skillChoiceAlgorithm);
			strategyDistribution.setTaskChoice(modelFactory,
					SimulationParameters.taskChoiceAlgorithm);
			strategyDistribution.setTaskMinMaxChoice(modelFactory,
					SimulationParameters.taskMinMaxChoiceAlgorithm);
		} catch (Exception exc) {
			exc.printStackTrace();
			say(Constraints.UNKNOWN_EXCEPTION);
		}

		this.addSubContext(agentPool);
		this.addSubContext(taskPool);

		initializeTasks();
		initializeAgents();

		say("Task choice algorithm is "
				+ SimulationParameters.taskChoiceAlgorithm);
		sanity("Number of teams created " + this.getObjects(Task.class).size());
		sanity("Number of agents created "
				+ this.getObjects(Agent.class).size());
		sanity("Algorithm tested: " + SimulationParameters.taskChoiceAlgorithm);

		try {
			outputAgentSkillMatrix();
		} catch (IOException e) {
			say(Constraints.IO_EXCEPTION);
			e.printStackTrace();
		} catch (NullPointerException nexc) {
			say(Constraints.UNKNOWN_EXCEPTION);
			nexc.printStackTrace();
		}

		if (SimulationParameters.forceStop)
			RunEnvironment.getInstance().endAt(SimulationParameters.numSteps);

		buildCentralPlanner();
		buildExperienceReassessment();
		buildAgentsWithdrawns();

		List<ISchedulableAction> actions = schedule.schedule(this);
		say(actions.toString());
	}

	private void initializeLoggers() throws IOException {
		// System.setErr(new PrintStream(new
		// FileOutputStream("error_console.log")));
		// actually this little bastard is not working, find out why ?

		PjiitLogger.init();
		say(Constraints.LOGGER_INITIALIZED);
		SanityLogger.init();
		sanity(Constraints.LOGGER_INITIALIZED);
		EndRunLogger.init();
		EndRunLogger.buildHeaders(buildFinalMessageHeader());
	}

	private void initializeAgents() {
		Model model = modelFactory.getFunctionality();
		if (model.isNormal() && model.isValidation()) {
			throw new UnsupportedOperationException();
		} else if (model.isNormal()) {
			addAgents();
		} else if (model.isSingleValidation()) {
			listAgent = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents();
		} else if (model.isValidation()) {
			listAgent = new ArrayList<Agent>();
			AgentTestUniverse.init();
			initializeValidationAgents();
		}
	}

	private void initializeValidationAgents() {
		for (Agent agent : AgentTestUniverse.DATASET) {
			say("Adding validation agent to pool..");
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
		Model model = modelFactory.getFunctionality();
		if (model.isNormal() && model.isValidation()) {
			throw new UnsupportedOperationException();
		} else if (model.isNormal()) {
			initializeTasksNormally();
		} else if (model.isSingleValidation()) {
			TaskTestUniverse.init();
			initalizeValidationTasks();
		} else if (model.isValidation()) {
			TaskTestUniverse.init();
			initalizeValidationTasks();
		} else {
			assert false; // should never happen
		}
	}

	private void initalizeValidationTasks() {
		for (Task task : TaskTestUniverse.DATASET) {
			say("Adding validation task to pool..");
			taskPool.addTask(task.getName(), task);
			taskPool.add(task);
			agentPool.add(task);
		}
	}

	private void initializeTasksNormally() {
		Integer howMany = SimulationParameters.multipleAgentSets ? Integer
				.parseInt(universe[0]) : SimulationParameters.taskCount;
		for (int i = 0; i < howMany; i++) {
			Task task = new Task();
			say("Creating task..");
			taskPool.addTask(task.getName(), task);
			say("Initializing task..");
			task.initialize();
			taskPool.add(task);
			agentPool.add(task);
		}

		launchStatistics.taskCount = taskPool.getCount();
	}

	private void initializeValidationLogger() {
		ValidationLogger.init();
		say(Constraints.VALIDATION_LOGGER_INITIALIZED);
		validation("---------------------------------------------------------");
	}

	private void outputAgentSkillMatrix() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("input_a1.csv"), ',',
				CSVWriter.NO_QUOTE_CHARACTER);
		for (Agent agent : listAgent) {
			for (AgentInternals __agentInternal : agent.getAgentInternals()) {
				ArrayList<String> entries = new ArrayList<String>();
				entries.add(agent.getNick());
				entries.add(__agentInternal.getExperience().getValue() + "");
				entries.add(__agentInternal.getSkill().getName());
				String[] stockArr = new String[entries.size()];
				stockArr = entries.toArray(stockArr);
				writer.writeNext(stockArr);
			}
		}
		writer.close();
	}

	private void addAgents() {
		Integer agentCnt = SimulationParameters.multipleAgentSets ? Integer
				.parseInt(universe[1]) : SimulationParameters.agentCount;

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

		launchStatistics.agentCount = agentPool.size()
				- launchStatistics.taskCount;
	}

	private void outputAgentNetworkData() {
		Network<?> agents = (Network<?>) this.getProjection("agents");
		Projection<?> agentsProjected = this.getProjection("agents");

		Iterator<?> allNodes = agents.getEdges().iterator();
		for (Object obj : agents.getNodes()) {
			say("Agent network data output --- " + ((Agent) obj).toString());
		}
	}

	@ScheduledMethod(start = 2000, priority = 0)
	public void outputSNSData() throws IOException {
		say("outputSNSData() check launched");
		// outputAgentNetworkData();
	}

	public void clearStaticHeap() {
		say("Clearing static data from previous simulation");
		PersistJobDone.clear();
		PersistRewiring.clear();
		TaskSkillsPool.clear();
		SkillFactory.skills.clear();
		NamesGenerator.clear();
		TaskPool.clearTasks();
		AgentSkillsPool.clear();
		Agent.totalAgents = 0;
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void finishSimulation() {
		say("finishSimulation() check launched");
		EnvironmentEquilibrium.setActivity(false);
		if (taskPool.getCount() < 1) {
			say("count of taskPool is < 1, finishing simulation");
			finalMessage(buildFinalMessage());
			shutdownInitiated = true;
			RunEnvironment.getInstance().endRun();
			cleanAfter();
		}
	}

	private String buildFinalMessage() {
		return RunState.getInstance().getRunInfo().getBatchNumber()
				+ ","
				+ RunState.getInstance().getRunInfo().getRunNumber()
				+ ","
				+ RunEnvironment.getInstance().getCurrentSchedule()
						.getTickCount() + "," + launchStatistics.agentCount
				+ "," + launchStatistics.taskCount + ","
				+ launchStatistics.expDecay + ","
				+ launchStatistics.fullyLearnedAgentsLeave + ","
				+ SimulationParameters.experienceCutPoint + ","
				+ SimulationParameters.granularity + ","
				+ SimulationParameters.granularityType + ","
				+ SimulationParameters.granularityObstinacy + ","
				+ strategyDistribution.getTaskChoice() + ","
				+ SimulationParameters.fillAgentSkillsMethod + ","
				+ SimulationParameters.agentSkillPoolDataset + ","
				+ SimulationParameters.taskSkillPoolDataset + ","
				+ strategyDistribution.getSkillChoice() + ","
				+ strategyDistribution.getTaskMinMaxChoice();
	}

	private String buildFinalMessageHeader() {
		return "Batch Number" + "," + "Run Number" + "," + "Tick Count" + ","
				+ "Agents count" + "," + "Tasks count" + ","
				+ "Experience decay" + "," + "Fully-learned agents leave" + ","
				+ "Exp cut point" + ","
				+ "Granularity" + "," + "Granularity type" + ","
				+ "Granularity obstinancy" + "," 
				+ "Task choice strategy" + "," + "fillAgentSkillsMethod" + ","
				+ "agentSkillPoolDataset" + "," + "taskSkillPoolDataset" + ","
				+ "Skill choice strategy" + "," + "Task MinMax choice";
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void checkForActivity() {
		say("checkForActivity() check launched");
		if (EnvironmentEquilibrium.getActivity() == false) {
			say("EnvironmentEquilibrium.getActivity() returns false!");
			finalMessage(buildFinalMessage());
			shutdownInitiated = true;
			RunEnvironment.getInstance().endRun();
			cleanAfter();
		}
	}

	private void cleanAfter() {
		if (!alreadyFlushed) {
			LogManager.shutdown();
			alreadyFlushed = true;
		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void validation(String s) {
		ValidationOutputter.say(s);
	}

	private void validationError(String s) {
		ValidationOutputter.error(s);
	}

	private void validationFatal(String s) {
		ValidationOutputter.fatal(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	private void finalMessage(String s) {
		if (modelFactory.getFunctionality().isValidation()) {
			validation(s);
		}
		EndRunLogger.finalMessage(s);
	}

	public void centralPlanning() {
		say("CentralPlanning scheduled method launched, listAgent.size(): "
				+ listAgent.size() + " taskPool.size(): " + taskPool.size());
		centralPlanningHq.centralPlanningCalc(listAgent, taskPool);
	}

	/**
	 * Here I need to schedule method manually because i don't know if central
	 * planer is enabled for the simulation whether not.
	 */
	public void buildCentralPlanner() {
		say("buildCentralPlanner lunched !");
		if (strategyDistribution.getTaskChoice().equals("central")) {
			centralPlanningHq = new CentralPlanning();

			say("Central planner initiating.....");
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createRepeating(1,
					1, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "centralPlanning");
			say("Central planner initiated and awaiting for call !");
		}
	}

	public synchronized void experienceReassess() {
		try{
		IndexedIterable<Object> agentObjects = agentPool
				.getObjects(Agent.class);
		for (Object agent : agentObjects) {
			String type = agent.getClass().getName();
			if (type.equals("internetz.Agent")) {
				say("Bingo! It's an agent in pool, I may have to decrease exp of this agent");
				// use persist job done
				Map<Integer, List<Skill>> c = PersistJobDone
						.getSkillsWorkedOn(((Agent) agent).getNick());
				if ((c == null) || (c.size() < 1)){ //agent didn't work on anything yet !
					continue; // move on to next agent in pool
				}
				List<Skill> __s = c.get(Integer.valueOf((int) RunEnvironment
						.getInstance().getCurrentSchedule().getTickCount()));
				List<Skill> s = __s == null ? new ArrayList<Skill>() : __s;

				Collection<AgentInternals> aic = ((Agent) agent)
						.getAgentInternals();
				CopyOnWriteArrayList aicconcurrent = new CopyOnWriteArrayList(
						aic);
				for (Object ai : aicconcurrent) {
					if (s.contains(((AgentInternals) ai).getSkill())) {
						// was working on this, don't decay
					} else {
						// decay this experience by beta < 1
						boolean result = ((AgentInternals) ai)
								.decayExperience();
						if (result) {
							((Agent) agent).removeSkill(
									((AgentInternals) ai).getSkill(), false);
						}
					}
				}
			}
		}
		}catch (Exception exc){
			validationFatal(exc.toString());
			validationError(exc.getLocalizedMessage());
		}finally{
			say("Regular method run for expDecay finished for this step.");
		}
	}

	/**
	 * Here I need to schedule method manually because I don't know if expDecay
	 * is enabled for the simulation whether not.
	 */
	public void buildExperienceReassessment() {
		say("buildExperienceReassessment lunched !");
		if (SimulationParameters.experienceDecay) {
			int reassess = RandomHelper.nextIntFromTo(0, 1);
			// I want in results both expDecay off and on!
			// thats why randomize to use both
			if (reassess == 0) {
				SimulationParameters.experienceDecay = false;
				launchStatistics.expDecay = false;
			} else if (reassess == 1) {
				SimulationParameters.experienceDecay = true;
				launchStatistics.expDecay = true;
				say("Exp decay initiating.....");
				ISchedule schedule = RunEnvironment.getInstance()
						.getCurrentSchedule();
				ScheduleParameters params = ScheduleParameters.createRepeating(
						1, 1, ScheduleParameters.LAST_PRIORITY);
				schedule.schedule(params, this, "experienceReassess");
				say("Experience decay initiated and awaiting for call !");
			} else
				assert false; // reassess is always 0 or 1
		}
	}

	public synchronized void agentsWithdrawns() {
		try{
		IndexedIterable<Object> agentObjects = agentPool
				.getObjects(Agent.class);
		for (Object agent : agentObjects) {
			if (agent.getClass().getName().equals("internetz.Agent")) {
				say("Bingo! It's an agent in pool, I may have to force the agent to leave");
				Collection<AgentInternals> aic = ((Agent) agent)
						.getAgentInternals();

				CopyOnWriteArrayList aicconcurrent = new CopyOnWriteArrayList(
						aic);
				for (Object ai : aicconcurrent) {
					if (((AgentInternals) ai).getExperience().getDelta() == 1.) {
						// say("Agent reached maximum!");
						((Agent) agent).removeSkill(
								((AgentInternals) ai).getSkill(), false);
					}
				}
				if (((Agent) agent).getAgentInternals().size() < 1) {
					agentPool.remove(agent);
				}
			}
		}
		}catch (Exception exc){
			validationFatal(exc.toString());
			validationError(exc.getLocalizedMessage());
		}finally{
			say("Eventual forcing agents to leave check finished!");
		}
	}

	/**
	 * Here I need to schedule method manually because I don't know if
	 * fullyLearnedAgentsLeave is enabled for the simulation whether not.
	 */
	public void buildAgentsWithdrawns() {
		say("buildAgentsWithdrawns lunched !");
		if (SimulationParameters.fullyLearnedAgentsLeave) {
			int reassess = RandomHelper.nextIntFromTo(0, 1);
			// I want in results both expDecay off and on!
			// thats why randomize to use both
			if (reassess == 0) {
				SimulationParameters.fullyLearnedAgentsLeave = false;
				launchStatistics.fullyLearnedAgentsLeave = false;
			} else if (reassess == 1) {
				SimulationParameters.fullyLearnedAgentsLeave = true;
				launchStatistics.fullyLearnedAgentsLeave = true;
				say("Agents withdrawns initiating.....");
				ISchedule schedule = RunEnvironment.getInstance()
						.getCurrentSchedule();
				ScheduleParameters params = ScheduleParameters.createRepeating(
						1, 1, ScheduleParameters.LAST_PRIORITY + 1);
				schedule.schedule(params, this, "agentsWithdrawns");
				say("Agents withdrawns initiated and awaiting for call !");
			} else
				assert false; // reassess is always 0 or 1
		}
	}

}