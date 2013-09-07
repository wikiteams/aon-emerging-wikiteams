package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public abstract class AgentSkillsPool {

	private static String filename = "top-users-final.csv";

	private enum Method {
		STATIC_TABLE, LINEAR_KNN, SVM;
	}

	private static Skill[] allSkills = null;
	private static HashMap skillSet = new HashMap<String, ArrayList>();

	public static void instantiate() {
		//say("initialized AgentSkillsPool");
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

	private static void parse_csv() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'', 1);
		String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	    	ArrayList l = new ArrayList();
	    	l.add(nextLine[1]);
	    	l.add(nextLine[2]);
	    	l.add(nextLine[3]);
	        skillSet.put(nextLine[0], l);
	    }
	}
	
	private static void parse_csv_all_skills() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		String [] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	        System.out.println(nextLine[0] + nextLine[1] + "etc...");
	    }
	}

	public static Skill chose_random() {
		Random generator = new Random();
		int i = generator.nextInt(allSkills.length);
		return allSkills[i];
	}

	public static void fillWithSkills(Agent agent) {
		say("Agent " + agent + " filled with skills");
	}

	public static Skill[] get_skill_set(int count) {
		return allSkills;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
