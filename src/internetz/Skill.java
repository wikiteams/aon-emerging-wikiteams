package internetz;

/***
 * Represents a "skill" - a GitHub language
 * 
 * @author Oskar
 * @since 1.0
 */
public class Skill {

	private String name;
	private short id;
	private Category category;
	private int strength;

	public Skill() {
		say("Empty-constructor Skill initialized");
	}

	public Skill(String name, short id, int strength) {
		this.name = name;
		this.id = id;
		this.strength = strength;
		say("Skill created");
	}

	public Skill(String name) {
		this.name = name;
		say("Skill created with minimum data");
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	public Skill(String name, short id) {
		new Skill(name, id, -1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
