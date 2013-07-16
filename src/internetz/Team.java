/**
 * 
 */
package internetz;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oskar
 * 
 */
public class Team {
	
	protected static int COUNT = 0;

	private String name;
	private int id;
	private int group;
	
	public Team() {
		say ("Team object initialized...");
	}
	
	private Map<String, Skill> skills = new HashMap<String, Skill>();
	
	public void addSkill(String key, Skill skill){
		skills.put(key, skill);
	}
	
	public Skill getSkill(String key){
		return skills.get(key);
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setGroup(int group){
		this.group = group;
	}

	public Team(short id) {
		this.id = id;
	}

	public Team(String name, short id) {
		this.name = name;
		this.id = id;
	}
	
	@SuppressWarnings("unused")
	private void say(String s) {
		System.out.println(s);
	}

	public String toString() {
		return "Team " + id + " " + name;
	}

	public void initialize() {
		setId(++COUNT);
	}

	public Map<String, Skill> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, Skill> skills) {
		this.skills = skills;
	}

}
