package internetz;

import java.util.List;

import org.soqqo.datagen.RandomDataGenerator;
import org.soqqo.datagen.config.DataTypes.Name;
import org.soqqo.datagen.config.GenConfig;

import internetz.Agent;

public class NamesGenerator {
	
	static RandomDataGenerator rdg = new RandomDataGenerator();

	public static List<Agent> getnames(int count) {
		List<Agent> randomPersons = rdg.generateList(
				count,
				new GenConfig().name(Name.Firstname, "firstname").name(
						Name.Lastname, "lastname"), Agent.class);
		return randomPersons;
	}

}
