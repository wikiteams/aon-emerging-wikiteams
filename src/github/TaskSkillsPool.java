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

import org.apache.commons.lang3.SystemUtils;

import repast.simphony.random.RandomHelper;
import utils.CharacterConstants;
import logger.PjiitOutputter;
import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import cern.jet.random.Poisson;

/**
 * A factory for creating of real skill data for COIN TASKS data taken mostly
 * from GitHub portal, possible randomization for bigger variation of results
 * 
 * @since 1.0
 * @version 1.4
 * @author Oskar Jarczyk
 * 
 */
public abstract class TaskSkillsPool {

	private static String filenameFrequencySkills = SystemUtils.IS_OS_LINUX ? "data/skills-probability.csv"
			: "data\\skills-probability.csv";
	private static String filenameGoogleSkills = SystemUtils.IS_OS_LINUX ? "data/tasks-skills.csv"
			: "data\\tasks-skills.csv";
	private static String filenameGithubClusters = SystemUtils.IS_OS_LINUX ? "data/github_clusters.csv"
			: "data\\github_clusters.csv";

	private enum DataSet {
		AT_LEAST_1_COMMIT, MOST_OFTEN_STARRED, TOPREPOS_AND_THEIRUSERS, TOPUSERS_AND_THEIRREPOS, TOPREPOS_AND_TOPUSERS, PUSHES_BY_LANGUAGES, SEVERANCE_FROM_MIDDLE, MOST_COMMON_TECHNOLOGY, ALL_LANGUAGES, TOP_USERS, _200_LANGUAGES;
	}

	public enum Method {
		STATIC_FREQUENCY_TABLE, GOOGLE_BIGQUERY_MINED, GITHUB_CLUSTERIZED;
	}

	public static void clear() {
		singleSkillSet.clear();
		skillSetMatrix.clear();
	}

	private static LinkedHashMap<String, Skill> singleSkillSet = new LinkedHashMap<String, Skill>();
	private static LinkedHashMap<Repository, HashMap<Skill, Double>> skillSetMatrix = new LinkedHashMap<Repository, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("MOST_OFTEN_STARRED"))
			instantiate(DataSet.MOST_OFTEN_STARRED);
		else if (method.toUpperCase().equals("AT_LEAST_1_COMMIT"))
			instantiate(DataSet.AT_LEAST_1_COMMIT);
		else if (method.toUpperCase().equals("TOPUSERS_AND_THEIRREPOS"))
			instantiate(DataSet.TOPUSERS_AND_THEIRREPOS);
		else if (method.toUpperCase().equals("TOPREPOS_AND_THEIRUSERS"))
			instantiate(DataSet.TOPREPOS_AND_THEIRUSERS);
		else if (method.toUpperCase().equals("TOPREPOS_AND_TOPUSERS"))
			instantiate(DataSet.TOPREPOS_AND_TOPUSERS);
		else if (method.toUpperCase().equals("PUSHES_BY_LANGUAGES"))
			instantiate(DataSet.PUSHES_BY_LANGUAGES);
		else if (method.toUpperCase().equals("TOP_USERS"))
			instantiate(DataSet.TOP_USERS);
		else if (method.toUpperCase().equals("200_LANGUAGES"))
			instantiate(DataSet._200_LANGUAGES);
	}

	public static void instantiate(DataSet dataset) {
		if (dataset == DataSet._200_LANGUAGES) {
			try {
				parseCsvStatic();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				say("File not found!");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				say("Input / Output Exception!");
				e.printStackTrace();
			}
		} else if (dataset == DataSet.TOP_USERS) {
			try {
				// parseCsvStatic();
				// parseCsvCluster();
				parseCsvGoogle();
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
				CharacterConstants.DEFAULT_EMPTY_CHARACTER, 1);
		String[] nextLine;
		long count = 0;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = skillFactory.getSkill(nextLine[0]);
			say("Processing skill + " + nextLine[0]);
			assert skill != null;
			skill.setCardinalProbability(Integer.parseInt(nextLine[1]));
			count += skill.getCardinalProbability();
			singleSkillSet.put(skill.getName(), skill);
		}
		for (Skill skill : singleSkillSet.values()) {
			skill.setProbability(skill.getCardinalProbability() / count);
		}
		reader.close();
	}

	private static void parseCsvGoogle() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filenameGoogleSkills),
				',', CSVWriter.NO_QUOTE_CHARACTER, 1);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String repo = nextLine[0];
			if (nextLine[2].trim().equals("null"))
				continue;
			Skill s = skillFactory.getSkill(nextLine[2]);
			Double value = Double.parseDouble(nextLine[1]);
			say("repo:" + repo + " skill:" + s + " value:" + value);
			addRepoSkill(repo, s, value);
		}
		reader.close();
	}

	private static void addRepoSkill(String repo, Skill skill, Double value) {
		Repository r = new Repository(repo);
		if (skillSetMatrix.containsKey(r)) {
			HashMap<Skill, Double> hs = skillSetMatrix.get(r);
			if (hs.containsKey(skill)){
				Double v = hs.get(skill);
				v += value;
				hs.put(skill, v);
			} else {
				hs.put(skill, value);
			}
			skillSetMatrix.put(r, hs);
		} else {
			HashMap<Skill, Double> hs = new HashMap<Skill, Double>();
			hs.put(skill, value);
			skillSetMatrix.put(r, hs);
		}
	}

	private static void parseCsvCluster() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(
				new FileReader(filenameGithubClusters), ',',
				CSVParser.DEFAULT_QUOTE_CHARACTER);
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
		reader.close();
	}

	public static Skill choseRandomSkill() {
		// Random generator = new Random();
		int i = RandomHelper.nextIntFromTo(0, singleSkillSet.size() - 1);
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
			int random = RandomHelper.nextIntFromTo(0, skillSetMatrix.size()-1);
			HashMap<Skill, Double> skills = getByIndex(skillSetMatrix, random);
			for(Skill skill : skills.keySet()){
				Double d = skills.get(skill);
				WorkUnit w1 = new WorkUnit(RandomHelper.nextDoubleFromTo(0, d));
				WorkUnit w2 = new WorkUnit(d);
				TaskInternals taskInternals = new TaskInternals(skill, w2, w1);
				task.addSkill(skill.getName(), taskInternals);
				say("Task " + task + " filled with skills from tasks-skills.csv");
			}
		} else if (SimulationParameters.taskSkillPoolDataset.equals("RANDOM")) {
			int x = ((int) (new Random().nextDouble() * SimulationParameters.staticFrequencyTableSc)) + 1;
			for (int i = 0; i < x; i++) {
				Skill skill = choseRandomSkill();
				// Random generator = new Random();
				WorkUnit w1 = new WorkUnit(RandomHelper.nextIntFromTo(1,
						SimulationParameters.maxWorkRequired - 1));
				WorkUnit w2 = new WorkUnit(0);
				TaskInternals taskInternals = new TaskInternals(skill, w1, w2);
				task.addSkill(skill.getName(), taskInternals);
				say("Task " + task + " filled with skills randomly");
			}
		} else if (SimulationParameters.taskSkillPoolDataset
				.equals("GITHUB_CLUSTERIZED")) {
			if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("clusters")) {

			} else if (SimulationParameters.gitHubClusterizedDistribution
					.toLowerCase().equals("distribute")) {
				Poisson poisson = new Poisson(10,
						Poisson.makeDefaultGenerator());
				double d = poisson.nextDouble() / 20;

				HashMap<Skill, Double> skillSetG = getByIndex(skillSetMatrix,
						(int) (skillSetMatrix.size() * d));
				Iterator it = skillSetG.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					Skill key = (Skill) pairs.getKey();
					Double value = (Double) pairs.getValue();
					if (value > 0.0) {
						WorkUnit w1 = new WorkUnit(value);
						WorkUnit w2 = new WorkUnit(0);
						TaskInternals taskInternals = new TaskInternals(key,
								w1, w2);
						task.addSkill(key.getName(), taskInternals);
					}
					// it.remove(); // avoids a ConcurrentModificationException
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
