package internetz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TaskPool {

	private static Map<String, Task> tasks = new HashMap<String, Task>();

	public void addTask(String key, Task task) {
		tasks.put(key, task);
		say("Task added successfully to pool. Pool size: " + getCount());
	}

	public Task getTask(String key) {
		return tasks.get(key);
	}

	public int getCount() {
		return tasks.size();
	}

	public static synchronized Task chooseTask(Agent agent,
			Strategy.TaskChoice strategy) {
		Task chosen = null;
		switch (strategy) {
		case HETEROPHYLY_HOMOPHYLY:
			;
		case RANDOM:
			ArrayList<Task> __tasks = new ArrayList<Task>();
			Collection<Skill> __skills = agent.getSkills();
			for (Task _task : tasks.values()) {
				for (Skill _skill : __skills) {
					if (_task.getSkills().containsKey(_skill.toString())) {
						__tasks.add(_task);
					}
				}
			}
			chosen = __tasks.get(new Random().nextInt(__tasks.size()));
		case COMPARISION:
			;
		case MACHINE_LEARNED:
			;
		default:
			;
		}
		return chosen;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
