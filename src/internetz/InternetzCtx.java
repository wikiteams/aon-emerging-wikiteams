package internetz;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;


public class InternetzCtx extends DefaultContext<Object>  implements ContextBuilder<Object> {
	
	@Override
	public Context build(Context<Object> context) {
		
		context.setId("internetz");
		
		Parameters param = RunEnvironment.getInstance().getParameters();
		RandomHelper.createPoisson((Integer)param.getValue("avgcap"));
		int agentCount = (Integer)param.getValue("agent_count");
		int memeCount = (Integer)param.getValue("meme_count");
		double pctPublishers = (Double)param.getValue("pctpubli");
		int readingCapacity;
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("artifacts", context, true);
		netBuilder.buildNetwork();
		NetworkBuilder<Object> netBuilderMM = new NetworkBuilder<Object>("artimemes", context, false);
		netBuilderMM.buildNetwork();
		NetworkBuilder<Object> netBuilderBlf = new NetworkBuilder<Object>("beliefs", context, false);
		netBuilderBlf.buildNetwork();
		NetworkBuilder<Object> netBuilderMem = new NetworkBuilder<Object>("memorys", context, false);
		netBuilderMem.buildNetwork();
		
		for (int i=0; i < memeCount; i++) {
			context.add(new Meme());
		}
		
		int maxbeliefs = (int) param.getValue("maxbelief");
		RandomHelper.createPoisson(maxbeliefs/2);
		int howmany = RandomHelper.getPoisson().nextInt();
		Network belief = (Network)context.getProjection("beliefs");
		Network memory = (Network)context.getProjection("memorys");
		Network artifct = (Network)context.getProjection("artifacts");
		
		for (int i=0; i < agentCount; i++) {
			boolean ispublisher = false;
			readingCapacity = RandomHelper.getPoisson().nextInt();
			if (RandomHelper.nextDoubleFromTo(0, 1) < pctPublishers) ispublisher = true;
			Agent agent = new Agent();
			context.add(agent);
			agent.setReadingCapacity(readingCapacity) ; 
			agent.setPublisher(ispublisher);			
			Iterable mymemes = context.getRandomObjects(Meme.class, howmany);
			while (mymemes.iterator().hasNext()) {
				Meme target = (Meme)mymemes.iterator().next();
				belief.addEdge(agent,target,1);  // 1?? Non dovremmo darli a caso?
			}
		}
		
		System.out.println("belief network size " + belief.getDegree());
		System.out.println("memory network size " + memory.getDegree());
		System.out.println("Artifact network size " + artifct.getDegree());
		

		
		

		System.out.println("Agents are: " + context.getObjects(Agent.class).size());
		System.out.println("Memes are: " + context.getObjects(Meme.class).size());
		return context;
		
	}

}
