# Emerging Virtual Teams

This is a [REPAST Symphony 2.0 model](http://repast.sourceforge.net/repast_simphony.html)

We try to simulate task collaboration on real-life programming tasks. 
In the simulation universe exists set of tasks, agents and skills. 
Tasks are categorized and named objectives under which agents are working on, 
they are independent and easily characterizable, and they consists of one or more skills.

Simulation tests different strategies of working on tasks (ought to be similar to FLOSS-projects objectives)

**1. Task choice strategies**

* heterophyly/homophyly
* random
* comparision
* machine-learned strategy (SVM or decision tree)


**2. Skill choise strategy**

* Proportional time division
* Greedy assignment by task
* Choice of agent


**3. How to lunch simulation?**

In Eclipse, (launchers -> internetz Model.launch), then press Start in Repast window

**4. Simulation parameters**

* randomSeed
* numTasks
* agentCount
* percStartMembership
* allowMultiMembership
* numSteps
* taskChoiceAlgorithm
