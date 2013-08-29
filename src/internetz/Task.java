/**
 * 
 */
package internetz;

import java.util.HashMap;
import java.util.Map;

/**
 * Task is a collection of a three-element set of skill, number of work units,
 * and work done.
 * 
 * @since 1.0
 * 
 * @author Oskar
 */
public class Task {

	protected static int COUNT = 0;

	private String name;
	private int id;

	public Task() {
		say("Task object created...");
	}

	private Map<String, TaskInternals> skills = new HashMap<String, TaskInternals>();

	public void addSkill(String key, TaskInternals taskInternals) {
		skills.put(key, taskInternals);
	}

	public TaskInternals getSkill(String key) {
		return skills.get(key);
	}

	public synchronized void setId(int id) {
		this.id = id;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	public String toString() {
		return "Task " + id + " " + name;
	}

	public synchronized void initialize() {
		setId(++COUNT);
		say("Team object initialized with id: " + this.id);
	}

	public Map<String, TaskInternals> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, TaskInternals> skills) {
		this.skills = skills;
	}

}
