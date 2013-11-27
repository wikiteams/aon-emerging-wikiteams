package test;

import internetz.Agent;
import internetz.SkillFactory;

import java.util.ArrayList;

import logger.PjiitOutputter;

public class AgentTestUniverse {

	public static ArrayList<Agent> DATASET = new ArrayList<Agent>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void init() {
		Agent agent = new Agent();
		say("Initializing agent..");
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}
	
}
