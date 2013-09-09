package internetz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.engine.watcher.Watch;
//import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.PropertyEquals;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Agent {

	SimulationParameters Sims = new SimulationParameters();

	private Map<String, AgentInternals> skills = 
			new HashMap<String, AgentInternals>();
	private Strategy strategy;
	
	public static int totalAgents = 0;
	static double time = 0;

	private int id;
	private String firstname;
	private String lastname;

	public Agent() {
		say("Agent constructor called");
		this.id = ++totalAgents;
		AgentSkillsPool.fillWithSkills(this);
	}
	
	public void addSkill(String key, AgentInternals agentInternals) {
		skills.put(key, agentInternals);
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
 
		// Agent Aj uses Aj {strategy for choosing tasks} and chooses a task to work on
		
		// Agent Aj works on Ti
		
		// Chose and algorithm for inside-task skill choose.
	}

	public String toString() {
		return "id: " + id + " name: " + firstname + " " + lastname;
	}
}