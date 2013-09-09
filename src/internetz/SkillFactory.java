package internetz;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

public class SkillFactory {
	
	public SkillFactory(){
		
	}

	private static String filename = "all-skills.csv";

	public static ArrayList skills = new ArrayList<Skill>();

	public void parse_csv_all_skills() throws IOException,
			FileNotFoundException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Skill skill = new Skill(nextLine[0]);
			skills.add(skill);
		}
	}

}
