package internetz;

import logger.PjiitOutputter;

public class Category {
	
	private String name;
	
	public Category(){
		say ("Category initialized");
	}
	
	public Category(String name){
		say ("Category initialized");
		this.name = name;
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
