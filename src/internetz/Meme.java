package internetz;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Meme {
	
	public Meme() {
		
	}
	
	public int IsAlive() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>1) {
			return 1;
		}
		else {
		return 0;
		}
	}
}
