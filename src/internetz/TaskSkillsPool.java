package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;

import logger.PjiitOutputter;

import au.com.bytecode.opencsv.CSVReader;

public abstract class TaskSkillsPool {

	/**
	 * count_language,language
	 * 
	 * 22477796,JavaScript 12214048,Ruby 12015918,Java
	 */
	private static String filenameFrequencySkills = "data\\50-skills.csv";

	private static String filenameGoogleSkills = "data\\task-skills.csv";

	private static String filenameGithubClusters = "data\\github_clusters.csv";

	public enum Method {
		STATIC_FREQUENCY_TABLE, GOOGLE_BIGQUERY_MINED, GITHUB_CLUSTERIZED;
	}

	private static LinkedHashMap<String, Skill> singleSkillSet = new LinkedHashMap<String, Skill>();

	private static LinkedHashMap<String, HashMap<Skill, Double>> skillSetMatrix = new LinkedHashMap<String, HashMap<Skill, Double>>();

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
			String repo = nextLine[11];
			HashMap<Skill, Double> hmp = new HashMap<Skill, Double>();
			for (int i = 0; i < 10; i++) {
				hmp.put(shs.get(i), Double.parseDouble(nextLine[i]));
			}
			skillSetMatrix.put(repo, hmp);
		}
		// for (Skill skill : skillSet.values()) {
		// skill.setProbability(skill.getCardinalProbability() / count);
		// }
	}

	// private static void parse_top_1000_csv() throws IOException,
	// FileNotFoundException {
	// CSVReader reader = new CSVReader(new FileReader(filename));
	// String[] nextLine;
	// while ((nextLine = reader.readNext()) != null) {
	// // nextLine[] is an array of values from the line
	// System.out.println(nextLine[0] + nextLine[1] + nextLine[2]
	// + nextLine[3] + "etc...");
	// }
	// }

	public static Skill choseRandomSkill() {
		Random generator = new Random();
		int i = generator.nextInt(singleSkillSet.size());
		return getByIndex(singleSkillSet, i);
	}

	public static Skill getByIndex(LinkedHashMap<String, Skill> hMap, int index) {
		return (Skill) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Task task) {
		if (SimulationParameters.taskSkillPoolDataset
				.equals("STATIC_FREQUENCY_TABLE")) {
			Skill skill = choseRandomSkill();
			Random generator = new Random();
			WorkUnit w1 = new WorkUnit(
					generator.nextInt(SimulationParameters.maxWorkRequired));
			WorkUnit w2 = new WorkUnit(0);
			TaskInternals taskInternals = new TaskInternals(skill, w1, w2);
			task.addSkill(skill.getName(), taskInternals);
			say("Task " + task + " filled with skills");
		} else if (SimulationParameters.taskSkillPoolDataset
				.equals("GITHUB_CLUSTERIZED")) {
			
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
