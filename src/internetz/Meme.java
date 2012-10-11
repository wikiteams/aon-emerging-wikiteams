package internetz;

import java.util.Hashtable;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Meme {
	int id;
	int group;
	
	public Meme() {
		// We set the id in the context, not here.
		this.id = id;
		this.group = group;
	}
	
	public boolean IsAlive() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>2) return true;
		return false;
	}
	
	public boolean isSuitable(Agent agent) {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		Hashtable allMemes = ((InternetzCtx)context).getMemez();
		Boolean suitable = true;
		int opp = 4999 - this.getID();
		Meme oppositememe = (Meme) allMemes.get(opp);
		// Iterator opposite = new PropertyEquals(context, "id", opp).query().iterator();
		if (belief.isAdjacent(agent, oppositememe)&&belief.getEdge(agent, oppositememe).getWeight()>0)
			suitable = false; 
		return suitable;
	}
	
	public int getDegree() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		return belief.getDegree(this);
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
	
	public boolean isGrp0() {
		if (IsAlive()) {
			if (this.getGrp() == 0) return true;
			else return false;
		} else return false;
	 }
	
	public boolean isGrp1() {
		if (IsAlive()) {
			if (this.getGrp() == 1) return true;
			else return false;
		} else return false;
	 }
	
	public boolean isGrp2() {
		if (IsAlive()) {
			if (this.getGrp() == 2) return true;
			else return false;
		} else return false;
	 }
	
	public boolean isGrp3() {
		if (IsAlive()) {
			if (this.getGrp() == 3) return true;
			else return false;
		} else return false;
	 }
	
	
	public int getID() {
		return this.id;
	}
	
	
	public int EightyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		int totalAgents = context.getObjects(Agent.class).size();
		if (belief.getDegree(this)>=(totalAgents*0.8)) return 1;
		return 0;
	}
	
	public int FiftyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		int totalAgents = context.getObjects(Agent.class).size();
		if (belief.getDegree(this)>=(totalAgents*0.5))  return 1;
		return 0;
	}
	
		public int TenPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		int totalAgents = context.getObjects(Agent.class).size();
		if (belief.getDegree(this)>=(totalAgents*0.1)) return 1;
		return 0;
	}
}