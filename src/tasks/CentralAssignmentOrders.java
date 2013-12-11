package tasks;

import internetz.Task;

public class CentralAssignmentOrders {
	
	private Task chosenTask;
	private String chosenSkill;
	
	public CentralAssignmentOrders(Task chosenTask, String chosenSkill){
		this.chosenTask = chosenTask;
		this.chosenSkill = chosenSkill;
	}

	public String getChosenSkill() {
		return this.chosenSkill;
	}

	public void setChosenSkill(String chosenSkill) {
		this.chosenSkill = chosenSkill;
	}
	
	public void cancelOrders(){
		this.chosenSkill = null;
		this.chosenTask = null;
	}

	public Task getChosenTask() {
		return this.chosenTask;
	}

	public void setChosenTask(Task chosenTask) {
		this.chosenTask = chosenTask;
	}

}
