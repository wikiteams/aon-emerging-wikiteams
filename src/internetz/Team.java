/**
 * 
 */
package internetz;

/**
 * @author Oskar
 * 
 */
public class Team {

	String name;
	int id;
	int group;
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setGroup(int group){
		this.group = group;
	}

	public Team() {

	}

	public Team(short id) {
		this.id = id;
	}

	public Team(String name, short id) {
		this.name = name;
		this.id = id;
	}
	
	@SuppressWarnings("unused")
	private void say(String s) {
		System.out.println(s);
	}

	public String toString() {
		return "Team " + id + " " + name;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

}
