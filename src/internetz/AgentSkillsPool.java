package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import logger.PjiitOutputter;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public abstract class AgentSkillsPool {

	/***
	 * Input format of a .CSV file:
	 * 
	 * username, skill1, skill2, skill3
	 * 
	 * i.e. 'fabpot', 'PHP', 'Shell', 'JavaScript'
	 */
	private static String filename = "data\\top-users-final.csv";

	private static String filename_ext = "data\\users-and-their-pull-requests.csv";

	private enum DataSet {
		STATIC_TOP1000_3SKILLS, STATIC_PULL_REQUESTS;
	}

	private enum Method {
		TOP_ACTIVE, RANDOM_FROM_GENERAL_POOL, RANDOM;
	}

	/***
	 * <String:user, {<skill, intensivity>}>
	 */
	private static LinkedHashMap<String, HashMap<Skill, Double>> skillSet = 
			new LinkedHashMap<String, HashMap<Skill, Double>>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate(String method) {
		if (method.toUpperCase().equals("STATIC_TABLE"))
			instantiate(DataSet.STATIC_TOP1000_3SKILLS);
		else if (method.toUpperCase().equals("STATIC_PULL_REQUESTS"))
			instantiate(DataSet.STATIC_PULL_REQUESTS);
	}

	public static void instantiate(DataSet method) {
		if (method == DataSet.STATIC_TOP1000_3SKILLS) {
			try {
				parse_csv(false);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (method == DataSet.STATIC_PULL_REQUESTS) {
			try {
				parse_csv(true);
				parse_csv_ext();
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

	/*
	 * Here is parsing real data - top active 960 GitHub users and their 3 most
	 * often used skills
	 * 
	 * @since 1.1
	 */
	private static void parse_csv(boolean nickOnly) throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'');
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nickOnly) {
				skillSet.put(nextLine[0], new HashMap<Skill, Double>());
			} else {
				HashMap<Skill, Double> l = new HashMap<Skill, Double>();
				for (int i = 1; i < nextLine.length; i++) {
					l.put(skillFactory.getSkill(nextLine[i]), null);
					say("Parsed from CSV: " + nextLine[i]);
				}
				skillSet.put(nextLine[0], l);
			}
		}
		reader.close();
	}

	/*
	 * Here is parsing real data - pull requests of top active 960 GitHub users
	 * and their used skills [1..n]
	 * 
	 * @since 1.2
	 */
	private static void parse_csv_ext() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename_ext), ',',
				CSVWriter.NO_QUOTE_CHARACTER, 1);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			String user = nextLine[3];
			if (nextLine[2].trim().equals("null"))
				continue;
			Skill s = skillFactory.getSkill(nextLine[2]);
			Double value = Double.parseDouble(nextLine[1]);
			say("user:" + user + " skill:" + s + " value:" + value);
			addExtSkill(user, s, value);
		}
		reader.close();
	}

	private static void addExtSkill(String user, Skill skill, Double value) {
		HashMap<Skill, Double> h = skillSet.get(user);
		if (h == null){
			skillSet.put(user, new HashMap<Skill, Double>());
			h = skillSet.get(user);
		}
		Double x = h.get(skill);
		if (x == null){
			h.put(skill, value);
		} else{
			x += value;
			h.put(skill, x);
		}
		skillSet.put(user, h);
	}

	public static HashMap<Skill, Double> choseRandom() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet, i);
	}

	public static HashMap<Skill, Double> getByIndex(
			LinkedHashMap<String, HashMap<Skill, Double>> hMap, int index) {
		return (HashMap<Skill, Double>) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Agent agent) {
		if (SimulationParameters.fillAgentSkillsMethod.toUpperCase().equals(
				"RANDOM"))
			fillWithSkills(agent, Method.RANDOM);
		else if (SimulationParameters.fillAgentSkillsMethod.toUpperCase()
				.equals("RANDOM_FROM_GENERAL_POOL"))
			fillWithSkills(agent, Method.RANDOM_FROM_GENERAL_POOL);
		else if (SimulationParameters.fillAgentSkillsMethod.toUpperCase()
				.equals("TOP_ACTIVE"))
			fillWithSkills(agent, Method.TOP_ACTIVE);
	}

	public static void fillWithSkills(Agent agent, Method method) {
		if (method == Method.RANDOM) {

		} else if (method == Method.TOP_ACTIVE) {
			HashMap<Skill, Double> iterationSkills = getByIndex(skillSet, agent.getId());
			for(Skill iterationSkill : iterationSkills.keySet()){
				AgentInternals builtAgentInternals = new AgentInternals(iterationSkill,
						new Experience(iterationSkills.get(iterationSkill), 8500));
				agent.addSkill(iterationSkill.getName(), builtAgentInternals);
			}
		} else if (method == Method.RANDOM_FROM_GENERAL_POOL) {
			// randomize HOW MANY SKILLS
			Random generator = new Random();
			int how_many = generator
					.nextInt(SimulationParameters.agentSkillsPoolRandomize1);
			ArrayList<Skill> __skills = new ArrayList<Skill>();
			for (int i = 0; i < how_many; i++) {
				Skill s1 = null;
				while (true) {
					s1 = skillFactory.getRandomSkill();
					if (__skills.contains(s1)) {
						continue;
					} else {
						__skills.add(s1);
						break;
					}
				}
				Random generator_exp = new Random();
				int topExperience = SimulationParameters.agentSkillsMaximumExperience;
				int experienceRandomized = generator_exp.nextInt(topExperience);
				// double exp__d = (double)exp__ / (double)top;
				// dont do that, we want to persist integer experience
				// not result of delta(exp) function !
				say("exp randomized to: " + experienceRandomized);
				AgentInternals builtAgentInternals = new AgentInternals(s1,
						new Experience(experienceRandomized, topExperience));
				agent.addSkill(s1.getName(), builtAgentInternals);
			}

		}
		// ArrayList skill = getByIndex(skillSet, COUNTABLE);
		// Experience experience = new Experience();
		// AgentInternals agentInternals = new AgentInternals(skill,
		// experience);
		// agent.addSkill(key, agentInternals);
		// say("Agent " + agent + " filled with skills");
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
