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

![p1](http://latex.codecogs.com/gif.latex?%5C%5C%5B1mm%5D%20T_%7Bi%7D%20%3D%20TASKS%20%5C%5C%5B1mm%5D%20A_%7Bj%7D%20%3D%20AGENTS%20%5C%5C%5B1mm%5D%20S_%7Bk%7D%20%3D%20SKILLS%20%5C%5C%5B1mm%5D "p1")

We define task as a collection of a three-element set of skill, number of work units, and work done. A skill is a feature characterized by name. Skills ought to reflect characteristics and features existing in real data within GitHub portal due to WikiTeams research project. 

![p2](http://latex.codecogs.com/gif.latex?T_%7Bi%7D%20%3D%20%5C%7B%20s_%7Be%7D%5E%7BT_%7Bi%7D%7D%2C%20G_%7Be%7D%5E%7BS%5E%7BT_%7Bi%7D%7D%7D%2C%20W%5E%7BS%5E%7BT_%7Bi%7D%7D%7D%5C%7D "p2")

![p2a](http://latex.codecogs.com/gif.latex?S_%7Bk%7D%20%3D%20%3Cname%3E "p2a")

Agent is characterized by: a collection of a two-element set which consists of skills and experience (number of work units) in this particular skill, and a strategy for choosing tasks. 

![p3](http://latex.codecogs.com/gif.latex?A_%7Bj%7D%20%3D%20%5C%7B%3CS_%7Be%7D%5E%7BA%5E%7Bj%7D%7D%2C%20E_%7Be%7D%5E%7BS%5E%7BA%5E%7Bj%7D%7D%7D%3E%5C%7D%2C%20%22STRATEGY%20%5C%20FOR%20%5C%20CHOOSING%20%5C%20TASKS%22 "p3")

To remark “number of work units”, notice a G constant in Ti, while a work unit in Aj named E is a variable.

![p4](http://liveexpert.pl/p4.png "p4")

Picture 1: Sigmoid learning curve
Delta δ function is a sigmoid function (S-curve) reflecting human learning progress calculated by an equation δ (Sk) = 1 / (1 + e^-E) . 

![p5](http://liveexpert.pl/p5.png "p5")

Picture 2: Frequency of skills in tasks
Set of skills and it’s characteristics in TASKS is different from a set of skills existing in AGENTS.
Steps of the T-CEM simulation per one step in a time (for every Aj do following):

1.	Agent Aj uses Aj {strategy for choosing tasks} and choses a task to work on
2.	Agent Aj works on Ti

   ![p6](http://latex.codecogs.com/gif.latex?T_%7Bi%7D%20%5Ccap%20A_%7Bj%7D%20%3D%20%5C%7BS_%7Bn%7D%5C%7D_%7Bn%3D1%7D%5E%7BN%7D "p6")

   Work is done be by incrementing the “work done” counter in one the skills of a task.

3.	Chose an algorithm for skill – inside – task choose. Agent have a problem of choosing which skill to work on on current time unit.

   ![p7](http://latex.codecogs.com/gif.latex?%5Calpha%20%3D%20%5Cfrac%7B1%7D%7BN%7D "p7")

   a.	Proportional time division

   For every Sn, do

   ![p8](http://latex.codecogs.com/gif.latex?%5C%5C%5B1mm%5D%20W%5E%7BS_%7Bn%7D%7D%20%3D%20W%5E%7BS_%7Bn%7D%7D%20&plus;%201%20%5Ccdot%20%5Calpha%20%5Ccdot%20%5Cdelta%28E%29%20%5C%5C%5B1mm%5D%20E%5E%7BS_%7Bn%7D%7D%20%3D%20E%5E%7BS_%7Bn%7D%7D%20&plus;%201%20%5Ccdot%20%5Calpha "p8")

   b.	Greedy assignment by task

   For chosen Sn, do

   ![p9](http://latex.codecogs.com/gif.latex?%5C%5C%5B1mm%5D%20W%5E%7BS_%7Bn%7D%7D%20%3D%20W%5E%7BS_%7Bn%7D%7D%20&plus;%201%20%5Ccdot%20%5Cdelta%28E%29%20%5C%5C%5B1mm%5D%20E%5E%7BS_%7Bn%7D%7D%20%3D%20E%5E%7BS_%7Bn%7D%7D%20&plus;%201 "p9")

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
* strategyDistribution
* taskChoiceAlgorithm
* skillChoiceAlgorithm
* agentSkillsPoolRandomize1
* agentSkillsPoolRandomize2
* experienceDecay
* granularity
* granularityObstinacy
* granularityType
* dataSetAll
* experienceCutPoint
