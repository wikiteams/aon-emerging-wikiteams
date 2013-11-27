package internetz;

import java.text.DecimalFormat;

public class TaskInternals {
	
	private Skill skill;
	private WorkUnit workRequired;
	private WorkUnit workDone;
	
	public TaskInternals(Skill skill, WorkUnit workRequired, WorkUnit workDone){
		this.skill = skill;
		this.workDone = workDone;
		this.workRequired = workRequired;
	}
	
	public Skill getSkill() {
		return skill;
	}
	
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	public WorkUnit getWorkRequired() {
		return workRequired;
	}
	
	public void setWorkRequired(WorkUnit workRequired) {
		this.workRequired = workRequired;
	}
	
	public WorkUnit getWorkDone() {
		return workDone;
	}
	
	public void setWorkDone(WorkUnit workDone) {
		this.workDone = workDone;
	}
	
	public boolean isWorkDone(){
		return (this.getWorkDone().d >= this.getWorkRequired().d);
	}
	
	public double getProgress(){
		return Math.abs(this.workDone.d / this.workRequired.d);
	}
	
	@Override
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.######");
		return this.skill.getName() + " " + 
				df.format(workDone.d) + "/" + df.format(workRequired.d);
	}
}
