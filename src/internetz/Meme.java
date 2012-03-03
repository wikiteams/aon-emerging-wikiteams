package internetz;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Meme {
	int id;
	int group;
	
	public Meme() {
		this.id = id;
		this.group = group;
	}
	
	public int IsAlive() {
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network)context.getProjection("beliefs");
		if (belief.getDegree(this)>2) return 1;
		else return 0;
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
	
	public int isGrp0() {
		if (IsAlive()==1) {
			if (this.getGrp() == 0) return 1;
			else return 0;
		} else return 0;
	 }
	
	public int isGrp1() {
		if (IsAlive()==1) {
			if (this.getGrp() == 1) return 1;
			else return 0;
		} else return 0;
	 }
	
	public int isGrp2() {
		if (IsAlive()==1) {
			if (this.getGrp() == 2) return 1;
			else return 0;
		} else return 0;
	 }
	
	public int isGrp3() {
		if (IsAlive()==1) {
			if (this.getGrp() == 3) return 1;
			else return 0;
		} else return 0;
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
}
