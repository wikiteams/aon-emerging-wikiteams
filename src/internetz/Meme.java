package internetz;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Meme {
	int id;
	int group;
	
	public Meme() {
		this.id = id;
		this.group = group;
	}
	
	public void setID(int ID){
		this.id = ID;
	}
	
	public void setGrp(int group) {
		this.group = group;
	}
	
	public int getGrp() {
		return this.group;
	}
	
	
	public int getID() {
		return this.id;
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
		if (belief.getDegree(this)>=(context.getObjects(Agent.class).size()*0.75)) return 1;
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
