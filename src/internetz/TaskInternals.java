package internetz;

public class TaskInternals {
	
	private Skill skill;
	private WorkUnit workUnits;
	private WorkUnit workDone;
	
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	public WorkUnit getWorkUnits() {
		return workUnits;
	}
	public void setWorkUnits(WorkUnit workUnits) {
		this.workUnits = workUnits;
	}
	public WorkUnit getWorkDone() {
		return workDone;
	}
	public void setWorkDone(WorkUnit workDone) {
		this.workDone = workDone;
	}
}
