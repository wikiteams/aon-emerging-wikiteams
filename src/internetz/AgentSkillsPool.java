package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public abstract class AgentSkillsPool {

	/***
	 * Input format of a .CSV file:
	 * 
	 * username, skill1, skill2, skill3
	 * 
	 * i.e. 'fabpot', 'PHP', 'Shell', 'JavaScript'
	 */
	private static String filename = "top-users-final.csv";

	private enum Method {
		STATIC_TABLE, MUTATE_STATIC_TABLE, RANDOM;
	}

	private static LinkedHashMap<String, ArrayList> skillSet = new LinkedHashMap<String, ArrayList>();
	private static SkillFactory skillFactory = new SkillFactory();

	public static void instantiate() {
		instantiate(Method.STATIC_TABLE);
	}

	public static void instantiate(Method method) {
		if (method == Method.STATIC_TABLE) {
			try {
				parse_csv();
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
	private static void parse_csv() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'', 1);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			ArrayList<Skill> l = new ArrayList<Skill>();
			for (int i = 1; i < nextLine.length; i++) {
				l.add(skillFactory.getSkill(nextLine[i]));
				say("Parsed from CSV: " + nextLine[i]);
			}
			skillSet.put(nextLine[0], l);
		}
	}

	public static ArrayList choseRandom() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet, i);
	}

	public static ArrayList getByIndex(LinkedHashMap<String, ArrayList> hMap,
			int index) {
		return (ArrayList) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Agent agent) {
		fillWithSkills(agent, Method.RANDOM);
	}

	public static void fillWithSkills(Agent agent, Method method) {
		if (method == Method.RANDOM) {
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
				int top = SimulationParameters.agentSkillsPoolRandomize2;
				int exp__ = generator_exp.nextInt(top);
				double exp__d = (double)exp__ / (double)top;
				say ("exp randomized to: " + exp__);
				AgentInternals __agentInternals = new AgentInternals(s1,
						new Experience(exp__d, top));
				agent.addSkill(s1.getName(), __agentInternals);
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
