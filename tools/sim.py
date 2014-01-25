#!/usr/bin/env python
import csv,sys, numpy

strategies = {}

with open('all_simulations.csv', 'rb') as csv_file:
	reader = csv.reader(csv_file, delimiter=',')
	for row in reader:
		(batchNumber, runNumber, tickCount, agentsCount, tasksCount, tasksLeft, experienceDecay, fullyLearnedAgentsLeave, expCutPoint, granularity, granularityType, granularityObstinancy, taskChoiceStrategy, fillAgentSillsMethod, agentSkillPoolDataset, taskSkillPoolDataset, skillChoiceStrategy, taskMinMaxChoice, taskDataSetChecksum, agentDataSetChecksum) = row
		print tickCount, agentsCount, tasksCount, tasksLeft, experienceDecay, fullyLearnedAgentsLeave, granularity, taskChoiceStrategy, skillChoiceStrategy, taskDataSetChecksum, agentDataSetChecksum

		key = (taskChoiceStrategy, taskMinMaxChoice, skillChoiceStrategy, agentsCount, tasksCount, experienceDecay, fullyLearnedAgentsLeave, granularity)
		if key not in strategies.keys():
			strategies[key] = []

		strategies[key].append(float(tickCount)) 

for key in strategies.keys():
	(taskChoiceStrategy, taskMinMaxChoice, skillChoiceStrategy, agentsCount, tasksCount, experienceDecay, fullyLearnedAgentsLeave, granularity) = key
	stddevp = numpy.std(strategies[key])
	average = numpy.average(strategies[key])
	result = 2 * stddevp / average

	fileName = '%s_%s_%s_agents_%s_tasks_%s_experience_%s_fully_%s_granularity_%s.csv' % key
	f = open(fileName, 'a+')
	f.write("taskChoiceStrategy,skillChoiceStrategy,agentsCount,tasksCount,experienceDecay,fullyLearnedAgentsLeave,granularity,taskMinMaxChoice,tickCount,taskDataSetChecksum,agentDataSetChecksum\n")

	for row in strategies[key]:
		f.write("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n" % (taskChoiceStrategy, skillChoiceStrategy, agentsCount, tasksCount, experienceDecay, fullyLearnedAgentsLeave, granularity, taskMinMaxChoice, row, taskDataSetChecksum, agentDataSetChecksum))
	
	f.write("ufnosc: %s" % result)

	f.close()
	
