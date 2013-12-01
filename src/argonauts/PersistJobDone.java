package argonauts;

import internetz.Task;

import java.util.HashMap;
import java.util.Map;

import repast.simphony.engine.environment.RunEnvironment;

public class PersistJobDone {

	/**
	 * <Ai.name : <iterationNu : Task>>
	 * Agent nick - key
	 * value - iteration number, Task
	 */
	private static Map<String, Map<Integer, Task>> jobDone = 
			new HashMap<String, Map<Integer, Task>>();
	
	public static void clear(){
		jobDone.clear();
	}
	
	/**
	 * Tutaj dodaje fakt, ze agent pracowal na taskiem taki i takim
	 * w danym ticku symulacji
	 * @param agentNick
	 * Nick of agent (result of aget.getNick()) used to literally identity
	 * agent by his nick
	 * @param task
	 * Task object on which agent was working
	 */
	public static void addContribution(String agentNick, Task task){
		int iteration = (int) RunEnvironment.getInstance().
				getCurrentSchedule().getTickCount();
		
		Map<Integer, Task> value = jobDone.get(agentNick);
		if (value == null){
			jobDone.put(agentNick, new HashMap<Integer, Task>());
			value = jobDone.get(agentNick);
		}
		value.put(iteration, task);
		jobDone.put(agentNick, value);
	}
	
	public static Map<Integer, Task> getContributions(String agentNick) {
		return jobDone.get(agentNick);
	}

	public static Map<String, Map<Integer, Task>> getJobDone() {
		return jobDone;
	}

	public static void setJobDone(Map<String, Map<Integer, Task>> contributions) {
		PersistJobDone.jobDone = contributions;
	}

}
