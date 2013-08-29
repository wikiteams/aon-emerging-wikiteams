package internetz;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.projection.Projection;

public class InternetzCtx extends DefaultContext {

	private SimulationParameters simulationParameters = new SimulationParameters();
	private ModelFactory modelFactory = new ModelFactory();
	
	private TaskPool taskPool = new TaskPool();

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private boolean moreThanBasic() {
		return modelFactory.getComplexity() > 0;
	}

	public InternetzCtx() {
		super("InternetzCtx");
		try {
			PjiitLogger.init();
			say("PjiitLogger initialized");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			say("Error initializing PjiitLogger !");
		}
		say("Super object InternetzCtx loaded");
		say("Starting simulation with model: " + modelFactory.toString());
		// getting parameters of simulation
		say("Loading parameters");
		simulationParameters.init();
		// initialize skill pools
		AgentSkillsPool agentSkillPool = new AgentSkillsPool();
		say("Created AgentSkillsPool");
		TaskSkillsPool taskSkillPool = new TaskSkillsPool();
		say("Created TaskSkillsPool");
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("agents",(Context<Object>) this, true);
		netBuilder.buildNetwork();
		for (int i = 0; i < simulationParameters.taskCount; i++) {
			Task task = new Task();
			say("Creating task..");
			taskPool.addTask("", task);
			say("Initializing task..");
			task.initialize();
		}

		Network<Agent> agents = (Network<Agent>) this.getProjection("agents");
		say("Projection agents (" + agents.getName() + ") exists and is size: "
				+ agents.size());
		addAgent(simulationParameters.agentCount, true);
		say("Task choice algorithm is " + simulationParameters.taskChoiceAlgorithm);
		System.out.println("Number of teams created "
				+ this.getObjects(Task.class).size());
		System.out.println("Number of agents created "
				+ this.getObjects(Agent.class).size());
		System.out.println("Algorithm tested: " + simulationParameters.taskChoiceAlgorithm);

		/*
		 * ISchedule schedule =
		 * RunEnvironment.getInstance().getCurrentSchedule(); ScheduleParameters
		 * schedParams; schedParams =
		 * ScheduleParameters.createAtEnd(ScheduleParameters
		 * .FIRST_PRIORITY);//.createOneTime(103); // OutputRecorder recorder =
		 * new OutputRecorder(this, simulation_mode);
		 * schedule.schedule(schedParams,this, "outputSNSData", "record");
		 */
	
	}

	public void addAgent(int agentCnt, boolean concentrate) {
		// Parameters param = RunEnvironment.getInstance().getParameters();
		// this this = (this)ContextUtils.getContext(this);
		// Network teams = (Network) this.getProjection("teams");
		// Network skills = (Network) this.getProjection("skills");
		// Network competencies = (Network) this.getProjection("competencies");
		// Network sns = (Network) this.getProjection("linkedin");

		List<Agent> listAgent = NamesGenerator.getnames(agentCnt);
		for (int i = 0; i < agentCnt; i++) {
			Agent agent = listAgent.get(i);
			say(agent.toString());
			say("in add aggent i: " + i);
			this.add(agent);
		}
	}
	
	private void outputAgentNetworkData(){
		Network agents = (Network) this.getProjection("agents");
		Projection agentsProjected = this.getProjection("agents");
		
		Iterator allNodes = agents.getEdges().iterator();
		for (Object obj : agents.getNodes()) {
			say( "Agent network data output --- " + ((Agent) obj).toString() );
		}
	}

	@ScheduledMethod(start = 2000, priority = 0)
	private void outputSNSData() throws IOException {
		outputAgentNetworkData();
	}

}