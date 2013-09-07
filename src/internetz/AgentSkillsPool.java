package internetz;

import internetz.TaskSkillsPool.Method;
import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public abstract class AgentSkillsPool {

	private static String filename = "top-users-final.csv";

	private enum Method {
		STATIC_TABLE, LINEAR_KNN, SVM;
	}

	private static Skill[] skillSet = null;

	public static void instantiate() {
		say("initialized AgentSkillsPool");
	}

	public static void instantiate(Method method) {
		say("initialized TaskSkillsPool");

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
	}

	private static void parse_csv() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	        System.out.println(nextLine[0] + nextLine[1] + "etc...");
	    }
	}

	public static Skill chose_random() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.length);
		return skillSet[i];
	}

	public static void fillWithSkills(Agent agent) {
		say("Agent " + agent + " filled with skills");
	}

	public static Skill[] get_skill_set(int count) {
		return skillSet;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
