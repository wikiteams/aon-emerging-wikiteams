package internetz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;


public class InternetzCtx extends DefaultContext {
	//Parameters param = null;
	int agentCount = 0;
	int memeCount = 0;
	double pctPublishers = 0.0;
	int groups = 0;
	int readingCapacity;
	int memeBrk = 0;
	int agntBrk = 0;
	int maxbeliefs = 0;
	// this this;
	Vector totCommunities = new Vector();
	
	public InternetzCtx (){//this build(this<Object> myContext) {
		super("InternetzCtx");
		Parameters param = RunEnvironment.getInstance().getParameters();
		agentCount = (Integer)param.getValue("agent_count");
		memeCount = (Integer)param.getValue("meme_count");
		pctPublishers = (Double)param.getValue("pctpubli");
		groups = (Integer) param.getValue("cultGroups");
		maxbeliefs = (Integer) param.getValue("maxbelief");
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("artifacts", (Context<Object>) this, true);
		netBuilder.buildNetwork();
		NetworkBuilder<Object> netBuilderMM = new NetworkBuilder<Object>("artimemes", (Context<Object>) this, false);
		netBuilderMM.buildNetwork();
		NetworkBuilder<Object> netBuilderBlf = new NetworkBuilder<Object>("beliefs", (Context<Object>) this, false);
		netBuilderBlf.buildNetwork();
		NetworkBuilder<Object> netBuilderMem = new NetworkBuilder<Object>("memorys", (Context<Object>) this, false);
		netBuilderMem.buildNetwork();
		NetworkBuilder<Object> netBuilderSN = new NetworkBuilder<Object>("twitter", (Context<Object>) this, true);
		netBuilderSN.buildNetwork();

		if (groups > 1) {
			agntBrk = agentCount/groups;
			memeBrk = memeCount/groups;
			// System.out.println(memeBrk+"  "+agntBrk);
		}

		for (int group=0; group<groups; group++) {
			ArrayList community = new ArrayList();
			totCommunities.add(community);
		}

		// totGroups.get(group)
		for (int i=0; i<memeCount; i++) {
			Meme meme = new Meme();
			this.add(meme);
			meme.setID(i);
			if (groups>1) {
				int whichgrp = (int)i/memeBrk;
				ArrayList community = (ArrayList) totCommunities.get(whichgrp);
				community.add(meme);
				meme.setGrp(whichgrp);
				// System.out.println("I am a meme in group "+ whichgrp);
			}
		}
		Network belief = (Network) this.getProjection("beliefs");
		Network memory = (Network) this.getProjection("memorys");
		Network artifct = (Network) this.getProjection("artifacts");

		addAgent(agentCount,false);
	}

	public void addAgent(int agentCount, boolean concentrate) {
		Parameters param = RunEnvironment.getInstance().getParameters();
		//this this = (this)ContextUtils.getContext(this);	
		Network memory = (Network)getProjection("memorys");
		Network belief = (Network)getProjection("beliefs");
		Network artimeme = (Network)getProjection("artimemes");
		Network artifact = (Network<Artifact>)getProjection("artifacts");		
		Network sns = (Network)getProjection("twitter");	
		for (int i=0; i<agentCount; i++) {
			boolean ispublisher = false;
			RandomHelper.createPoisson((Integer)param.getValue("avgcap"));
			readingCapacity = RandomHelper.getPoisson().nextInt();
			if (RandomHelper.nextDoubleFromTo(0, 1) < pctPublishers) ispublisher = true;
			Agent agent = new Agent();
			this.add(agent);
			agent.setReadingCapacity(readingCapacity);
			agent.setPublisher(ispublisher);
			RandomHelper.createPoisson(maxbeliefs);
			int howmany = RandomHelper.getPoisson().nextInt();
			ArrayList mymemes = new ArrayList();
			if (groups>1) {
				int whichgrp=2;
				// if (concentrate == false) whichgrp = (int)i/agntBrk;
				if (concentrate == false) whichgrp = RandomHelper.nextIntFromTo(0, groups-1);
				agent.setGroup(whichgrp);
				ArrayList mycommunity = (ArrayList) totCommunities.get(whichgrp); 
				int j=0;
				while (j<howmany) {
					Meme thismeme = (Meme) mycommunity.get(RandomHelper.nextIntFromTo(0, mycommunity.size()-1));
					if (!belief.isAdjacent(thismeme, agent)) {
						mymemes.add(thismeme);
						j++;
					}
				}
			} else {
				Iterator allmemes = this.getRandomObjects(Meme.class, howmany).iterator();	
				while (allmemes.hasNext()) {
					mymemes.add(allmemes.next());
				}
			}
			int allmms = mymemes.size();
			for (int k=0; k<allmms;k++ ) {
				Meme target = (Meme)mymemes.get(k);
				//System.out.println("I am now adding meme: "+target);
				double wght = RandomHelper.nextDoubleFromTo(0.5, 1);
				belief.addEdge(agent,target,wght);
				// darli a caso. si.
				// System.out.println("i am now adding a meme");
			}
		}
	}
	
	//@ScheduledMethod(start = 550)
	//public void september() {
	//	addAgent(50,true);
	//}
	

	@ScheduledMethod(start=10, interval=2)
	public void inflow() {
		addAgent(4,false);
	}
}