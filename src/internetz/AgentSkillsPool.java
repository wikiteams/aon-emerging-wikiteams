package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public abstract class AgentSkillsPool {

	private static String filename = "top-users-final.csv";

	private enum Method {
		STATIC_TABLE, LINEAR_KNN, SVM;
	}

	private static Skill[] allSkills = null;
	private static LinkedHashMap skillSet = new LinkedHashMap<String, ArrayList>();

	private static int COUNTABLE = 0;

	public static void instantiate() {
		// say("initialized AgentSkillsPool");
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
			ArrayList l = new ArrayList();
			l.add(nextLine[1]);
			l.add(nextLine[2]);
			l.add(nextLine[3]);
			say("Parsed fro CSV: " + nextLine[0] + " " + nextLine[1] + " "
					+ nextLine[2] + " " + nextLine[3]);
			skillSet.put(nextLine[0], l);
		}
	}

	public static Skill chose_random() {
		Random generator = new Random();
		int i = generator.nextInt(allSkills.length);
		return allSkills[i];
	}

	public static ArrayList getByIndex(LinkedHashMap<String, ArrayList> hMap,
			int index) {
		return (ArrayList) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Agent agent) {
//		ArrayList skill = getByIndex(skillSet, COUNTABLE);
//		Experience experience = new Experience();
//		AgentInternals agentInternals = new AgentInternals(skill, experience);
//		agent.addSkill(key, agentInternals);
//		say("Agent " + agent + " filled with skills");
	}

	public static Skill[] get_skill_set(int count) {
		return allSkills;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
