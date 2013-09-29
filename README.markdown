# Emerging Virtual Teams

This is a [REPAST Symphony 2.0 model](http://repast.sourceforge.net/repast_simphony.html)

<p align="center"><img src="http://liveexpert.pl/coin.jpg" /></p>

We try to simulate task collaboration on real-life programming tasks. 
In the simulation universe exists set of tasks, agents and skills. 
Tasks are categorized and named objectives under which agents are working on, 
they are independent and easily characterizable, and they consists of one or more skills.

Simulation tests different strategies of working on tasks (ought to be similar to FLOSS-projects objectives)

**0. Model description**

The task collaboration emergence model “T-CEM”

In the simulation universe exists set of tasks, agents and skills. Tasks are categorized and named objectives under which agents are working on, they are independent and easily characterizable, and they consists of one or more skills.

![p1](http://liveexpert.pl/p1.png "p1")

We define task as a collection of a three-element set of skill, number of work units, and work done. A skill is a feature characterized by name. Skills ought to reflect characteristics and features existing in real data within GitHub portal due to WikiTeams research project. 

![p2](http://liveexpert.pl/p2.png "p2")

Agent is characterized by: a collection of a two-element set which consists of skills and experience (number of work units) in this particular skill, and a strategy for choosing tasks. 

![p3](http://liveexpert.pl/p3.png "p3")

To remark “number of work units”, notice a G constant in Ti, while a work unit in Aj named E is a variable.
 
Picture 1: Sigmoid learning curve
Delta δ function is a sigmoid function (S-curve) reflecting human learning progress calculated by an equation δ (Sk) = 1 / (1 + e^-E) . 
 
Picture 2: Frequency of skills in tasks
Set of skills and it’s characteristics in TASKS is different from a set of skills existing in AGENTS.
Steps of the T-CEM simulation per one step in a time (for every Aj do following):
1.	Agent Aj uses Aj {strategy for choosing tasks} and choses a task to work on
2.	Agent Aj works on Ti
 
Work is done be by incrementing the “work done” counter in one the skills of a task.
3.	Chose an algorithm for skill – inside – task choose. Agent have a problem of choosing which skill to work on on current time unit.
 
a.	Proportional time division
For every Sn, do
 
 
b.	Greedy assignment by task
For chosen Sn, do
 
c.	Choice of agent
4.	Tasks done leave the environment
5.	Agents with high learning rate quit the environment



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
