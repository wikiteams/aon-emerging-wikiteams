package internetz;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.Network;


public class InternetzCtx implements ContextBuilder<Object> {
	public Context<Object> build(Context<Object> context) {
		context.setId("internetz");
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object> ("artifact network", context, true);
		// EdgeCreator mmedge = new memEdges(); 
		// netBuilder.setEdgeCreator(mmedge);
		netBuilder.buildNetwork();
		NetworkBuilder<Object> netBuilderMM = new NetworkBuilder<Object> ("artimeme network", context, true);
		netBuilderMM.buildNetwork();
		NetworkBuilder<Object> netBuilderBlf = new NetworkBuilder<Object> ("belief network", context, false);
		netBuilderBlf.buildNetwork();
		NetworkBuilder<Object> netBuilderMem = new NetworkBuilder<Object> ("memory network", context, false);
		netBuilderMem.buildNetwork();
		NetworkBuilder<Object> netBuilderArtMem = new NetworkBuilder<Object> ("bookmark network", context, false);
		netBuilderArtMem.buildNetwork();
		Parameters param = RunEnvironment.getInstance().getParameters();
		RandomHelper.createPoisson((Integer)param.getValue("avgcap"));
		int agentCount = (Integer)param.getValue("agent_count");
		int memeCount = (Integer)param.getValue("meme_count");
		double pctPublishers = (double) param.getValue("pctpubli");
		int readingCapacity;
		boolean ispublisher = false;
		
		
		for (int i=0; i < agentCount; i++) {
			readingCapacity = RandomHelper.getPoisson().nextInt();
			if (RandomHelper.nextDoubleFromTo(0, 1)<= pctPublishers) {
				ispublisher = true;
			}
			context.add(new Agent(readingCapacity, ispublisher));
		}
		
		for (int i=0; i < memeCount; i++) {
			context.add(new Meme());
		}

		
		return context;
		
	}
}
