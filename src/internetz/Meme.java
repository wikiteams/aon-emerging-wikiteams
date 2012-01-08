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
		if (belief.getDegree(this)>2) return 1;
		else return 0;
	}
	
	public int EightyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(context.getObjects(Agent.class).size()*0.8)) return 1;
		else return 0;
	}
	
	public int FiftyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(context.getObjects(Agent.class).size()*0.5))  return 1;
		else return 0;
	}
	
	public int TenPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(context.getObjects(Agent.class).size()*0.1)) return 1;
		else return 0;
	}
	
	public int getDegree() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		return belief.getDegree(this);
	}
}
