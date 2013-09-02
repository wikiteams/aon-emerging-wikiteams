package internetz;

import java.util.Random;

public class TaskSkillsPool {
	
	static Skill[] skillSet = null;

	public TaskSkillsPool() {
		say("TaskSkill object " + this + " created");
	}
	
	public static Skill chose_random(){
		Random generator = new Random();
		int i = generator.nextInt(skillSet.length);
		return skillSet[i];
	}
	
	public static Skill[] get_skill_set(int count){
		return skillSet;
	}
	
	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
}
