package internetz;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Meme {
	Context context = (Context)ContextUtils.getContext(this);
	Network memory = (Network)context.getProjection("memory network");
	Network belief = (Network)context.getProjection("belief network");
	Network artimeme = (Network)context.getProjection("artimeme network");
	public Meme() {
		
	}


}
