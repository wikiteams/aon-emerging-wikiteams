package internetz;

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
	//static int totalTenCent = 0;
	static int totalAgents = 500; 
	
	public Meme() {
		this.id = id;
		this.group = group;
	}
	
	public boolean IsAlive() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>2) return true;
		return false;
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
	
	
	// public int IsReallyAlive() {
	//	Context context = (Context)ContextUtils.getContext(this);
	//	Network belief = (Network)context.getProjection("beliefs");
	//	if (belief.getDegree(this)>0) return 1;
	//	else return 0;
	//}
	
	public int EightyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(totalAgents*0.8)) return 1;
		return 0;
	}
	
	public int FiftyPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(totalAgents*0.5))  return 1;
		return 0;
	}
	
		public int TenPct() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>=(totalAgents*0.1)) return 1;
		return 0;
	}
}