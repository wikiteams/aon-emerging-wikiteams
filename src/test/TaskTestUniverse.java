package test;

import internetz.Skill;
import internetz.SkillFactory;
import internetz.Task;
import internetz.TaskInternals;
import internetz.WorkUnit;

import java.util.ArrayList;
import logger.PjiitOutputter;

public class TaskTestUniverse {
	
	public static ArrayList<Task> DATASET = new ArrayList<Task>();
	private static SkillFactory skillFactory = new SkillFactory();
	
	public static void init(){
		Task task1 = new Task();
		say("Initializing task..");
		Skill skill11 = skillFactory.getSkill("Java");
		WorkUnit workDone11 = new WorkUnit(3);
		WorkUnit workRequired11 = new WorkUnit(18);
		Skill skill12 = skillFactory.getSkill("C");
		WorkUnit workDone12 = new WorkUnit(1);
		WorkUnit workRequired12 = new WorkUnit(10);
		Skill skill13 = skillFactory.getSkill("XML");
		WorkUnit workDone13 = new WorkUnit(0);
		WorkUnit workRequired13 = new WorkUnit(10);
		task1.addSkill("Java", new TaskInternals(skill11, workRequired11, workDone11));
		task1.addSkill("C", new TaskInternals(skill12, workRequired12, workDone12));
		task1.addSkill("XML", new TaskInternals(skill13, workRequired13, workDone13));
		
		Task task2 = new Task();
		say("Initializing task2..");
		Skill skill21 = skillFactory.getSkill("Java");
		WorkUnit workDone21 = new WorkUnit(0);
		WorkUnit workRequired21 = new WorkUnit(10);
		Skill skill22 = skillFactory.getSkill("C");
		WorkUnit workDone22 = new WorkUnit(6);
		WorkUnit workRequired22 = new WorkUnit(10);
		Skill skill23 = skillFactory.getSkill("XML");
		WorkUnit workDone23 = new WorkUnit(2);
		WorkUnit workRequired23 = new WorkUnit(10);
		task2.addSkill("Java", new TaskInternals(skill21, workRequired21, workDone21));
		task2.addSkill("C", new TaskInternals(skill22, workRequired22, workDone22));
		task2.addSkill("XML", new TaskInternals(skill23, workRequired23, workDone23));
		
		Task task3 = new Task();
		say("Initializing task3..");
		Skill skill31 = skillFactory.getSkill("Java");
		WorkUnit workDone31 = new WorkUnit(11);
		WorkUnit workRequired31 = new WorkUnit(18);
		Skill skill32 = skillFactory.getSkill("C");
		WorkUnit workDone32 = new WorkUnit(0);
		WorkUnit workRequired32 = new WorkUnit(10);
		Skill skill33 = skillFactory.getSkill("XML");
		WorkUnit workDone33 = new WorkUnit(6);
		WorkUnit workRequired33 = new WorkUnit(10);
		task3.addSkill("Java", new TaskInternals(skill31, workRequired31, workDone31));
		task3.addSkill("C", new TaskInternals(skill32, workRequired32, workDone32));
		task3.addSkill("XML", new TaskInternals(skill33, workRequired33, workDone33));
		
		Task task4 = new Task();
		say("Initializing task4..");
		Skill skill41 = skillFactory.getSkill("C");
		WorkUnit workDone41 = new WorkUnit(0);
		WorkUnit workRequired41 = new WorkUnit(12);
		Skill skill42 = skillFactory.getSkill("XML");
		WorkUnit workDone42 = new WorkUnit(1);
		WorkUnit workRequired42 = new WorkUnit(10);
		task4.addSkill("C", new TaskInternals(skill41, workRequired41, workDone41));
		task4.addSkill("XML", new TaskInternals(skill42, workRequired42, workDone42));
		
		DATASET.add(task1);
		DATASET.add(task2);
		DATASET.add(task3);
		DATASET.add(task4);
	}
	
	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
