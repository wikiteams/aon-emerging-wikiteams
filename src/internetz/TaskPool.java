package internetz;

import java.util.HashMap;
import java.util.Map;

public class TaskPool {

	private Map<String, Task> tasks = new HashMap<String, Task>();

	public void addTask(String key, Task task) {
		tasks.put(key, task);
	}

	public Task getTask(String key) {
		return tasks.get(key);
	}
	
	public int getCount() {
		return tasks.size();
	}

}
