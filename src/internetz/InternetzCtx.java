package internetz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;


public class InternetzCtx extends DefaultContext<Object> implements ContextBuilder<Object> {
	

	public Context build(Context<Object> context) {
		context.setId("internetz");
		Parameters param = RunEnvironment.getInstance().getParameters();
		int agentCount = (Integer)param.getValue("agent_count");
		int memeCount = (Integer)param.getValue("meme_count");
		double pctPublishers = (Double)param.getValue("pctpubli");
		int groups = (Integer) param.getValue("cultGroups");

		int readingCapacity;
		int memeBrk = 0;
		int agntBrk = 0;
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("artifacts", context, true);
		netBuilder.buildNetwork();
		NetworkBuilder<Object> netBuilderMM = new NetworkBuilder<Object>("artimemes", context, false);
		netBuilderMM.buildNetwork();
		NetworkBuilder<Object> netBuilderBlf = new NetworkBuilder<Object>("beliefs", context, false);
		netBuilderBlf.buildNetwork();
		NetworkBuilder<Object> netBuilderMem = new NetworkBuilder<Object>("memorys", context, false);
		netBuilderMem.buildNetwork();
		
		if (groups > 1) {
			memeBrk = memeCount/groups;
			agntBrk = agentCount/groups;
		}
		

		Vector totCommunities = new Vector();
		for (int group=0; group<groups; group++) { 
			ArrayList community = new ArrayList();
			totCommunities.add(community);
		}
		// totGroups.get(group)

		for (int i=0; i<memeCount; i++) {
			Meme meme = new Meme();
			context.add(meme);
			meme.setID(i);
			if (groups>1) {
				int whichgrp = i/memeBrk;
				ArrayList community = (ArrayList) totCommunities.get(whichgrp);
				community.add(meme);
			}
		}

		int maxbeliefs = (Integer) param.getValue("maxbelief");
		
		Network belief = (Network) context.getProjection("beliefs");
		Network memory = (Network) context.getProjection("memorys");
		Network artifct = (Network) context.getProjection("artifacts");
		
		
			// agentCount/=groups;
			for (int i=0; i<agentCount; i++) {
				boolean ispublisher = false;
				RandomHelper.createPoisson((Integer)param.getValue("avgcap"));
				readingCapacity = RandomHelper.getPoisson().nextInt();
				if (RandomHelper.nextDoubleFromTo(0, 1) < pctPublishers) ispublisher = true;
				Agent agent = new Agent();
				context.add(agent);
				agent.setReadingCapacity(readingCapacity) ; 
				// System.out.println("I can read " + readingCapacity + " artifacts per turn");
				agent.setPublisher(ispublisher);
				RandomHelper.createPoisson(maxbeliefs/2);
				int howmany = RandomHelper.getPoisson().nextInt();
				ArrayList mymemes = new ArrayList();
				if (groups>1) {
					int whichgrp = i/agntBrk;
					
					ArrayList mycommunity = (ArrayList) totCommunities.get(whichgrp); 
					
					Iterator allmemes = context.getObjects(Meme.class).iterator();
					int j=0;
					while (j<howmany) {
						Meme thismeme = (Meme) allmemes.next();
						if (mycommunity.contains(thismeme)) {
							mymemes.add(thismeme);
							j++;
						}
					}
					
				} else {
					Iterator allmemes = context.getRandomObjects(Meme.class, howmany).iterator(); 					
					while (allmemes.hasNext()) {
						mymemes.add(allmemes.next());
					}
				
				}
				int allmms = mymemes.size()-1;
				for (int k=0; k<allmms;k++ ) {
					Meme target = (Meme)mymemes.get(k);
					belief.addEdge(agent,target,1);  // 1?? Non dovremmo darli a caso?
					// System.out.println("i am now adding a meme");
				}
			}
		return context;
	}
}
