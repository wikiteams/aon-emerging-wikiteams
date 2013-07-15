package internetz;

public class Skill {

	private String name;
	private short id;
	private Category category;

	public Skill() {

	}

	public Skill(String name, short id) {
		this.name = name;
		this.id = id;
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

	public int getRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getVotes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getViews() {
		// TODO Auto-generated method stub
		return 0;
	}

}
