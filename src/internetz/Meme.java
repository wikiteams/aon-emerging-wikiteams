package internetz;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Meme {
	Context context = (Context)ContextUtils.getContext(this);
	Network memetic = (Network)context.getProjection("memetic network");
	Network artifactic = (Network)context.getProjection("artifact network");
	public Meme() {
		
	}


}
