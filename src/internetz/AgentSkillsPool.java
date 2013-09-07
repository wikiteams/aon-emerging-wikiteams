package internetz;

import java.util.Random;

public abstract class AgentSkillsPool {
	
	private static Skill[] skillSet = null;
	
	public static void instantiate(){
		say("initialized AgentSkillsPool");
	}
	
	public static Skill chose_random(){
		Random generator = new Random();
		int i = generator.nextInt(skillSet.length);
		return skillSet[i];
	}
	
	public static void fillWithSkills(Agent agent){
		say("Agent " + agent + " filled with skills");
	}
	
	public static Skill[] get_skill_set(int count){
		return skillSet;
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
