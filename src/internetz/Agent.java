package internetz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import logger.PjiitOutputter;
import repast.simphony.annotate.AgentAnnot;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import strategies.Strategy;
import argonauts.PersistJobDone;

@AgentAnnot(displayName="Agent")
public class Agent {

	private Map<String, AgentInternals> skills = new HashMap<String, AgentInternals>();
	
	private Strategy strategy;
	
	private static final SkillFactory skillFactory = new SkillFactory();

	public static int totalAgents = 0;
	static double time = 0;

	private int id;
	private String firstName;
	private String lastName;
	private String nick;

	public Agent() {
		new Agent("Undefined name", "Undefined", (totalAgents + 1) + "");
	}
	
	public Agent(String firstName, String lastName, String nick) {
		say("Agent constructor called");
		this.id = ++totalAgents;
		AgentSkillsPool.fillWithSkills(this);
		this.firstName = firstName;
		this.lastName = lastName;
		this.nick = nick;
	}

	public void addSkill(String key, AgentInternals agentInternals) {
		skills.put(key, agentInternals);
	}

	public Collection<AgentInternals> getAgentInternals() {
		return skills.values();
	}

	public AgentInternals getAgentInternals(String key) {
//		if (this.getStrategy().taskChoice.equals(Strategy.TaskChoice.HETEROPHYLY)){
//			AgentInternals result = skills.get(key) == null ? (
//					new AgentInternals(
//							skillFactory.getSkill(key), 
//							new Experience(true))
//					) : skills.get(key);
//			skills.put(key, result);
//		}
//		return skills.get(key); ---- THIS WAS SAFE, bet rewritten below to skip unnecessary if
		AgentInternals result = null;
		if (skills.get(key) == null){
			result = (
					new AgentInternals(
							skillFactory.getSkill(key), 
							new Experience(true))
					);
			skills.put(key, result);
			result = skills.get(key);
		} else {
			result = skills.get(key);
		}
		return result;
	}

	public Collection<Skill> getSkills() {
		ArrayList<Skill> skillCollection = new ArrayList<Skill>();
		Collection<AgentInternals> internals = this.getAgentInternals();
		for (AgentInternals ai : internals) {
			skillCollection.add(ai.getSkill());
		}
		return skillCollection;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {

		say("Step(" + time + ") of Agent " + this.id
				+ " scheduled method launched.");

		//Context context = (Context) ContextUtils.getContext(this);
		time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		// Agent Aj uses Aj {strategy for choosing tasks} 
		// and chooses a task to work on

		Task taskToWork = TaskPool.chooseTask(this, this.strategy.taskChoice);
		// TO DO: make a good assertion to prevent nulls !!

		// Agent Aj works on Ti
		if (taskToWork != null) {
			assert taskToWork.getTaskInternals().size() > 0;
			say("Agent " + this.id + " will work on task " + taskToWork.getId());
			taskToWork.workOnTask(this, this.strategy.skillChoice);
			EnvironmentEquilibrium.setActivity(true);
		} else {
			say("Agent " + this.id + " didn't work on anything");
			sanity("Agent " + this.id + " don't have a task to work on in step " + time);
		}

		// Chose and algorithm for inside-task skill choose.
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
	
	public String describeExperience(){
//		Collection<AgentInternals> internals = this.getAgentInternals();
//		Map<String, Double> deltaE = new HashMap<String, Double>();
//		for (AgentInternals ai : internals) {
//			deltaE.put(ai.getSkill().getName() , ai.getExperience().getDelta());
//		}
//		return deltaE.entrySet().toString();
		
		Collection<AgentInternals> internals = this.getAgentInternals();
		Map<String, String> deltaE = new HashMap<String, String>();
		for (AgentInternals ai : internals) {
			deltaE.put( ai.getSkill().getName() , 
					(new DecimalFormat("#.######")).format(ai.getExperience().getDelta()) );
		}
		return deltaE.entrySet().toString();
	}
	
	public double describeExperience(Skill skill){
		if (this.getStrategy().taskChoice.equals(Strategy.TaskChoice.HETEROPHYLY_EXP_BASED)){
			AgentInternals result = skills.get(skill.getName()) == null ? (
					new AgentInternals(
							skillFactory.getSkill(skill.getName()), 
							new Experience(true))
					) : skills.get(skill.getName());
			skills.put(skill.getName(), result);
		}
		return skills.get(skill.getName()).getExperience().getDelta();
	}
	
	@Override
	public String toString() {
		return getNick();
	}

	@Override
	public int hashCode() {
		return nick.hashCode() * id;
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.id == ((Agent) obj).id)
				&& (this.nick.toLowerCase().equals((((Agent) obj).nick.toLowerCase()))))
			return true;
		else
			return false;
	}
	
	protected boolean wasWorkingOnAnything(){
		return PersistJobDone.getJobDone().containsKey(this.getNick());
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
	private void sanity(String s){
		PjiitOutputter.sanity(s);
	}
}

class EnvironmentEquilibrium {
	
    private static boolean activity = false;

    public static synchronized boolean getActivity() {
        return activity;
    }

    public static synchronized void setActivity(boolean defineActivity) {
        activity = defineActivity;
    }

}