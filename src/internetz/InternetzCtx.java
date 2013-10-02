package internetz;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;
import au.com.bytecode.opencsv.CSVWriter;

public class InternetzCtx extends DefaultContext<Object> {

	private SimulationParameters simulationParameters;
	private StrategyDistribution strategyDistribution;

	private ModelFactory modelFactory = new ModelFactory();
	private SkillFactory skillFactory = null;

	private TaskPool taskPool = new TaskPool();
	private List<Agent> listAgent = null;

	@SuppressWarnings("unchecked")
	public InternetzCtx() {
		super("InternetzCtx");

		simulationParameters = new SimulationParameters();
		strategyDistribution = new StrategyDistribution();

		try {
			PjiitLogger.init();
			say("PjiitLogger initialized");
			say("Super object InternetzCtx loaded");
			say("Starting simulation with model: " + modelFactory.toString());
			// getting parameters of simulation
			say("Loading parameters");
			simulationParameters.init();
			// initialize skill pools
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			say("Error initializing PjiitLogger!");
		} catch (Exception exc) {
			say("Error initializing PjiitLogger and/or Simulation Parameters!");
		}

		try {
			skillFactory = new SkillFactory();
			skillFactory.parse_csv_all_skills();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			say("SkillFactory parsed all skills from CSV file");
		}

		try {
			AgentSkillsPool.instantiate();
			say("Instatiated AgentSkillsPool");
			TaskSkillsPool.instantiate();
			say("Instatied TaskSkillsPool");

			strategyDistribution.setType(simulationParameters.strategyDistribution);
			strategyDistribution.setSkillChoice(simulationParameters.skillChoiceAlgorithm);
			strategyDistribution.setTaskChoice(simulationParameters.taskChoiceAlgorithm);
		} catch (Exception exc) {
			exc.printStackTrace();
			say("Unknown exception");
		}

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
				"agents", (Context<Object>) this, true);
		netBuilder.buildNetwork();

		for (int i = 0; i < simulationParameters.taskCount; i++) {
			Task task = new Task();
			say("Creating task..");
			taskPool.addTask(task.getName(), task);
			say("Initializing task..");
			task.initialize();
		}

		Network<Agent> agents = (Network<Agent>) this.getProjection("agents");
		say("Projection agents (" + agents.getName() + ") exists and is size: "
				+ agents.size());

		addAgent(simulationParameters.agentCount);

		say("Task choice algorithm is "
				+ simulationParameters.taskChoiceAlgorithm);
		System.out.println("Number of teams created "
				+ this.getObjects(Task.class).size());
		System.out.println("Number of agents created "
				+ this.getObjects(Agent.class).size());
		System.out.println("Algorithm tested: "
				+ simulationParameters.taskChoiceAlgorithm);

		try {
			outputAgentSkillMatrix();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void outputAgentSkillMatrix() throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("input_a1.csv"), ',',
				CSVWriter.NO_QUOTE_CHARACTER);
		for (Agent agent : listAgent) {
			for (AgentInternals __agentInternal : agent.getAgentInternals()) {
				ArrayList<String> entries = new ArrayList<String>();
				entries.add(agent.getNick());
				entries.add(__agentInternal.getExperience().getCardinal() + "");
				entries.add(__agentInternal.getSkill().getName());
				String[] stockArr = new String[entries.size()];
				stockArr = entries.toArray(stockArr);
				writer.writeNext(stockArr);
			}
		}
		writer.close();
	}

	private void addAgent(int agentCnt) {
		listAgent = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgent.get(i);
			Strategy strategy = new Strategy();
			strategy.skillChoice = strategyDistribution.getSkillStrategy(agent);
			strategy.taskChoice = strategyDistribution.getTaskStrategy(agent);
			agent.setStrategy(strategy);
			say(agent.toString());
			say("in add aggent i: " + i);
			this.add(agent);
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

	private boolean moreThanBasic() {
		return modelFactory.getComplexity() > 0;
	}

	@ScheduledMethod(start = 2000, priority = 0)
	private void outputSNSData() throws IOException {
		outputAgentNetworkData();
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}