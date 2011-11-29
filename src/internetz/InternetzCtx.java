package internetz;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;


public class InternetzCtx implements ContextBuilder<Object> {
	
	@Override
	public Context<Object> build(Context<Object> context) {
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("artifact network", context, true);
		netBuilder.buildNetwork();
		NetworkBuilder<Object> netBuilderMM = new NetworkBuilder<Object>("artimeme network", context, false);
		netBuilderMM.buildNetwork();
		NetworkBuilder<Object> netBuilderBlf = new NetworkBuilder<Object>("belief network", context, false);
		netBuilderBlf.buildNetwork();
		NetworkBuilder<Object> netBuilderMem = new NetworkBuilder<Object>("memory network", context, false);
		netBuilderMem.buildNetwork();
		NetworkBuilder<Object> netBuilderArtMem = new NetworkBuilder<Object>("bookmark network", context, false);
		netBuilderArtMem.buildNetwork();
		
		// context.setId("internetz");
		
		Parameters param = RunEnvironment.getInstance().getParameters();
		RandomHelper.createPoisson((Integer)param.getValue("avgcap"));
		int agentCount = (int) param.getValue("agent_count");
		int memeCount = (int) param.getValue("meme_count");
		double pctPublishers = (double) param.getValue("pctpubli");
		int readingCapacity;
		boolean ispublisher = false;
		
		
		for (int i=0; i < agentCount; i++) {
			readingCapacity = RandomHelper.getPoisson().nextInt();
			// System.out.println(readingCapacity);
			if (RandomHelper.nextDoubleFromTo(0, 1) <= pctPublishers) {
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
