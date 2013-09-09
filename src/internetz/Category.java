package internetz;

public class Category {
	
	private String name;
	
	public Category(){
		say ("Category initialized");
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
