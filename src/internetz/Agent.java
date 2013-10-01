package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.ContextUtils;

public class Agent {

	SimulationParameters Sims = new SimulationParameters();

	private Map<String, AgentInternals> skills = new HashMap<String, AgentInternals>();
	private Strategy strategy;

	public static int totalAgents = 0;
	static double time = 0;

	private int id;
	private String firstname;
	private String lastname;
	private String nick;

	public Agent() {
		say("Agent constructor called");
		this.id = ++totalAgents;
		AgentSkillsPool.fillWithSkills(this);
	}

	public void addSkill(String key, AgentInternals agentInternals) {
		skills.put(key, agentInternals);
	}

	public Collection<AgentInternals> getAgentInternals() {
		return skills.values();
	}
	
	public AgentInternals getAgentInternals(String key) {
		return skills.get(key);
	}

	public Collection<Skill> getSkills() {
		ArrayList<Skill> __skills = new ArrayList<Skill>();
		Collection<AgentInternals> internals = this.getAgentInternals();
		for (AgentInternals ai : internals) {
			__skills.add(ai.getSkill());
		}
		return __skills;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {

		say("Step(" + time + ") of Agent scheduled method launched..");

		Context context = (Context) ContextUtils.getContext(this);
		time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		// Agent Aj uses Aj {strategy for choosing tasks} and chooses a task to
		// work on

		Task taskToWork = TaskPool.chooseTask(this, this.strategy.taskChoice);
		// TO DO: make a good assertion to prevent nulls !!

		// Agent Aj works on Ti
		taskToWork.workOnTask(this, this.strategy.skillChoice);

		// Chose and algorithm for inside-task skill choose.
	}

	public String toString() {
		return "id: " + id + " name: " + firstname + " " + lastname;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		say("Agent's login set to: " + nick);
		this.nick = nick;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
}