package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import logger.PjiitOutputter;

import au.com.bytecode.opencsv.CSVReader;

public abstract class TaskSkillsPool {

	/***
	 * Input format of a .CSV file:
	 * 
	 * username, skill1, skill2, skill3
	 * 
	 * i.e.
	 * 'fabpot', 'PHP', 'Shell', 'JavaScript'
	 */
	private static String filename = "top-users-final.csv";
	
	/**
	 * count_language,language
	 * 
	 * 22477796,JavaScript
	 * 12214048,Ruby
	 * 12015918,Java
	 */
	private static String filename2 = "50-skills.csv";

	public enum Method {
		STATIC_TABLE, UNIMPLEMENTED1, UNIMPLEMENTED2;
	}

	private static LinkedHashMap<String, Skill> skillSet = 
			new LinkedHashMap<String, Skill>();
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

	private static void parse_csv() throws IOException, FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename2), ',',
				CSVReader.DEFAULT_QUOTE_CHARACTER, 1);
		String[] nextLine;
		long count = 0;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = skillFactory.getSkill(nextLine[1]);
			skill.setCardinalProbability(Integer.parseInt(nextLine[0]));
			count += skill.getCardinalProbability();
			skillSet.put(skill.getName(), skill);
		}
		for (Skill skill : skillSet.values()){
			skill.setProbability(skill.getCardinalProbability() / count);
		}
	}

//	private static void parse_top_1000_csv() throws IOException,
//			FileNotFoundException {
//		CSVReader reader = new CSVReader(new FileReader(filename));
//		String[] nextLine;
//		while ((nextLine = reader.readNext()) != null) {
//			// nextLine[] is an array of values from the line
//			System.out.println(nextLine[0] + nextLine[1] + nextLine[2]
//					+ nextLine[3] + "etc...");
//		}
//	}

	public static Skill chose_random() {
		Random generator = new Random();
		int i = generator.nextInt(skillSet.size());
		return getByIndex(skillSet,i);
	}
	
	public static Skill getByIndex(LinkedHashMap<String, Skill> hMap,
			int index) {
		return (Skill) hMap.values().toArray()[index];
	}

	public static void fillWithSkills(Task task) {
		Skill skill = chose_random();
		Random generator = new Random();
		WorkUnit w1 = new WorkUnit(generator.nextInt(8));
		WorkUnit w2 = new WorkUnit(0);
		TaskInternals taskInternals = new TaskInternals(skill, w1, w2);
		task.addSkill(skill.getName(), taskInternals);
		say("Task " + task + " filled with skills");
	}

//	public static Skill[] get_skill_set(int count) {
//		return skillSet;
//	}
//
//	public static Skill[] get_skill_set() {
//		return skillSet;
//	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
