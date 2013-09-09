package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import au.com.bytecode.opencsv.CSVReader;

public abstract class TaskSkillsPool {

	private static String filename = "top-users-final.csv";
	private static String filename2 = "50-skills.csv";

	public enum Method {
		STATIC_TABLE, LINEAR_KNN, SVM;
	}

	private static Skill[] skillSet = null;

	public static void instantiate() {
		//say("initialized TaskSkillsPool");
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
		CSVReader reader = new CSVReader(new FileReader(filename2));
		String[] nextLine;
		skillSet = new Skill[50];
		int i = 0;
		
		while ((nextLine = reader.readNext()) != null) {
			// nextLine[] is an array of values from the line
			/*System.out.println(nextLine[0] + nextLine[1] + nextLine[2]
					+ nextLine[3] + "etc...");*/
			skillSet[i++] = new Skill(nextLine[1], (short) i);
		}
	}
	
	private static void parse_top_1000_csv() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// nextLine[] is an array of values from the line
			System.out.println(nextLine[0] + nextLine[1] + nextLine[2]
					+ nextLine[3] + "etc...");
		}
	}

	public static Skill chose_random() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.length);
		return skillSet[i];
	}

	public static void fillWithSkills(Task task) {
		Skill skill = chose_random();
		WorkUnit w1 = new WorkUnit();
		WorkUnit w2 = new WorkUnit();
		Random generator = new Random();
		w1.d = generator.nextInt(8);
		w2.d = 0;
		TaskInternals taskInternals = new TaskInternals(skill, w1, w2);
		task.addSkill(skill.getName(), taskInternals);
		say("Task " + task + " filled with skills");
	}

	public static Skill[] get_skill_set(int count) {
		return skillSet;
	}
	
	public static Skill[] get_skill_set() {
		return skillSet;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
