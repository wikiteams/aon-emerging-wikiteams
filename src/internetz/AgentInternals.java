package internetz;

public class AgentInternals {
	
	private Skill skill;
	private Experience experience;
	
	public AgentInternals(Skill skill, Experience experience){
		this.skill = skill;
		this.experience = experience;
	}
	
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	public Experience getExperience(){
		return experience;
	}

}
