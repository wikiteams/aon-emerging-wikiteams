package github;

import internetz.SimulationParameters;
import internetz.Skill;
import internetz.SkillFactory;
import internetz.Task;
import internetz.TaskInternals;
import internetz.WorkUnit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import logger.PjiitOutputter;
import au.com.bytecode.opencsv.CSVReader;
import cern.jet.random.Poisson;

/**
 * 
 * A factory for creating of real skill data for COIN TASKS data taken mostly
 * from GitHub portal, possible randomization for bigger variation of results
 * 
 * @since 1.0
 * @version 1.2
 * @author Oskar Jarczyk
 * 
 */
public abstract class TaskSkillsPool {

	private static String filenameFrequencySkills = "data\\50-skills.csv";
	private static String filenameGoogleSkills = "data\\task-skills.csv";
	private static String filenameGithubClusters = "data\\github_clusters.csv";

	public enum Method {
		STATIC_FREQUENCY_TABLE, GOOGLE_BIGQUERY_MINED, GITHUB_CLUSTERIZED;
	}

	private static LinkedHashMap<String, Skill> singleSkillSet = 
			new LinkedHashMap<String, Skill>();
	private static LinkedHashMap<Repository, HashMap<Skill, Double>> skillSetMatrix = 
			new LinkedHashMap<Repository, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("STATIC_FREQUENCY_TABLE"))
			instantiate(Method.STATIC_FREQUENCY_TABLE);
		else if (method.toUpperCase().equals("GOOGLE_BIGQUERY_MINED"))
			instantiate(Method.GOOGLE_BIGQUERY_MINED);
		else if (method.toUpperCase().equals("GITHUB_CLUSTERIZED"))
			instantiate(Method.GITHUB_CLUSTERIZED);
	}

	public static void instantiate(Method method) {
		if (method == Method.STATIC_FREQUENCY_TABLE) {
			try {
				parseCsvStatic();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (method == Method.GOOGLE_BIGQUERY_MINED) {
			try {
				parseCsvGoogle();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (method == Method.GITHUB_CLUSTERIZED) {
			try {
				parseCsvCluster();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		say("initialized TaskSkillsPool");
	}

	private static void parseCsvStatic() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(
				new FileReader(filenameFrequencySkills), ',',
				CSVReader.DEFAULT_QUOTE_CHARACTER, 1);
		String[] nextLine;
		long count = 0;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = skillFactory.getSkill(nextLine[1]);
			skill.setCardinalProbability(Integer.parseInt(nextLine[0]));
			count += skill.getCardinalProbability();
			singleSkillSet.put(skill.getName(), skill);
		}
		for (Skill skill : singleSkillSet.values()) {
			skill.setProbability(skill.getCardinalProbability() / count);
		}
	}

	private static void parseCsvGoogle() throws IOException,
			FileNotFoundException {
		// TODO: uncomment and finish
		// CSVReader reader = new CSVReader(
		// new FileReader(filenameGoogleSkills), ',',
		// CSVReader.DEFAULT_QUOTE_CHARACTER, 1);
		// String[] nextLine;
		// long count = 0;
		// while ((nextLine = reader.readNext()) != null) {
		// Skill skill = skillFactory.getSkill(nextLine[1]);
		// skill.setCardinalProbability(Integer.parseInt(nextLine[0]));
		// count += skill.getCardinalProbability();
		// skillSet.put(skill.getName(), skill);
		// }
		// for (Skill skill : skillSet.values()) {
		// skill.setProbability(skill.getCardinalProbability() / count);
		// }
	}

	private static void parseCsvCluster() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(
				new FileReader(filenameGithubClusters), ',',
				CSVReader.DEFAULT_QUOTE_CHARACTER);
		String[] nextLine;
		nextLine = reader.readNext();

		LinkedList<Skill> shs = new LinkedList<Skill>();

		for (int i = 0; i < 10; i++) {
			shs.add(skillFactory
					.getSkill(nextLine[i].replace("sc_", "").trim()));
		}

		while ((nextLine = reader.readNext()) != null) {
			Repository repo = new Repository(nextLine[11], nextLine[12]);
			HashMap<Skill, Double> hmp = new HashMap<Skill, Double>();
			for (int i = 0; i < 10; i++) {
				hmp.put(shs.get(i), Double.parseDouble(nextLine[i]));
			}
			skillSetMatrix.put(repo, hmp);
		}

	}

	public static Skill choseRandomSkill() {
		Random generator = new Random();
		int i = generator.nextInt(singleSkillSet.size());
		return getByIndex(singleSkillSet, i);
	}

	private static Skill getByIndex(LinkedHashMap<String, Skill> hMap, int index) {
		return (Skill) hMap.values().toArray()[index];
	}

	private static HashMap<Skill, Double> getByIndex(
			LinkedHashMap<Repository, HashMap<Skill, Double>> hMap, int index) {
		return (HashMap<Skill, Double>) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Task task) {
		if (SimulationParameters.taskSkillPoolDataset
				.equals("STATIC_FREQUENCY_TABLE")) {
			int x = ( (int) (new Random().nextDouble() * 
					SimulationParameters.staticFrequencyTableSc) ) + 1;
			for (int i = 0 ; i < x ; i++){
			Skill skill = choseRandomSkill();
			Random generator = new Random();
			WorkUnit w1 = new WorkUnit(
					generator.nextInt(SimulationParameters.maxWorkRequired));
			WorkUnit w2 = new WorkUnit(0);
			TaskInternals taskInternals = new TaskInternals(skill, w1, w2);
			task.addSkill(skill.getName(), taskInternals);
			say("Task " + task + " filled with skills");
			}
		} else if (SimulationParameters.taskSkillPoolDataset
				.equals("GITHUB_CLUSTERIZED")) {
			if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("clusters")) {

			} else if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("distribute")) {
				Poisson poisson = new Poisson(10, Poisson.makeDefaultGenerator());
				double d = poisson.nextDouble() / 20;
				
				HashMap<Skill, Double> skillSetG = 
						getByIndex(skillSetMatrix, (int)(skillSetMatrix.size() * d) );
				Iterator it = skillSetG.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        Skill key = (Skill) pairs.getKey();
			    	Double value = (Double) pairs.getValue();
			    	if (value > 0.0){
			    		WorkUnit w1 = new WorkUnit(value);
						WorkUnit w2 = new WorkUnit(0);
						TaskInternals taskInternals = new TaskInternals(key, w1, w2);
						task.addSkill(key.getName(), taskInternals);
			    	}
			        //it.remove(); // avoids a ConcurrentModificationException
			    }
			}
		}
	}

	public int getSkillSetMatrixCount() {
		return skillSetMatrix.size();
	}

	public int getSingleSkillSet() {
		return singleSkillSet.size();
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
