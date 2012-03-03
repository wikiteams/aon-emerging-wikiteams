package internetz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Artifact {
	
	public int views;
	public int votes;
	public int shares;
	public double birthday;
	public Agent author;
	public double newrank;
	private double pagerank;
	public int id;


	public Artifact(Agent author, double pagerank) {
		this.views = views;
		this.votes = votes;
		this.shares = shares;
		this.pagerank = pagerank;
		this.newrank = 0;
		this.birthday = 0;
		this.author = author;
		this.id = id;
	}
	
	public Agent getAuthor() {
		return this.author;
	}
	
	
	public Iterator getMemes() {
		Context context = (Context)ContextUtils.getContext(this);
		Network artimeme = (Network)context.getProjection("artimemes");
		return artimeme.getAdjacent(this).iterator();
	}
	
	public int totalMemesInvested() {
		Context context = (Context)ContextUtils.getContext(this);
		Network artimeme = (Network)context.getProjection("artimemes");
		return artimeme.getDegree(this);
	}
	
	public Iterator getOutLinks() {
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		return artifact.getSuccessors(this).iterator();
	}
	
	public Iterator getInLinks() {
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		return artifact.getPredecessors(this).iterator();
	}
	
	
	
	public void addView() {
		views++;
	}
	
	public void addVote() {
		votes++;
	}
	
	public void addShare() {
		shares++;
	}
	
	public double getRank() {
		return pagerank;
	}
	
	public void setRank(double PageRank) {
		pagerank = PageRank;
	}
	
	public double getNewRank() {
		return newrank;
	}
	
	public void setNewRank(double NewRank) {
		newrank = NewRank;
	}
	
	
	public int getViews() {
		return views;
	}
	
	public int getVotes() {
		return votes;
	}
	
	@ScheduledMethod(start = 100, interval = 1)
	public void killOldLinks() {
		// ArrayList allinks = getTransformedIteratorToArrayList(artifact.getEdges().iterator());
		// Collections.sort(allinks, new InverseWeightComparator());
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		Iterator myedges = artifact.getEdges(this).iterator();
		while (myedges.hasNext()) {
			RepastEdge myedge = (RepastEdge) myedges.next();
			if (myedge.getWeight()>100){
				if (RandomHelper.nextDoubleFromTo(0, 1)<=0.50) artifact.removeEdge(myedge);
			}
		}
	}
		
	//	double maxweight = ((RepastEdge) allinks.get(0)).getWeight();
	//	for (int i=0; i<allinks.size(); i++) {
	//		RepastEdge link = (RepastEdge) allinks.get(i);
	//		double wght = link.getWeight(); 
	//		if (wght <= maxweight) {
	//			if (RandomHelper.nextDoubleFromTo(0, 1)<=0.65) allinks.remove(i);
	//		} 
	//		else break;
	//	}
	//}
	
	public void buildLink(Artifact arti) {
		Parameters param = RunEnvironment.getInstance().getParameters();
		double recipro = (Double) param.getValue("avgReciprocating");
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		int birthday = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		artifact.addEdge(this, arti, birthday);
		if (RandomHelper.nextDoubleFromTo(0, 1)<=recipro) artifact.addEdge(arti, this, birthday+2);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void updatePageRnk() {   // Adapted from the netlogo 'diffusion' code (fingers crossed)
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		int outDegree = artifact.getOutDegree(this);
		// Iterator all = (Iterator) artifact.getSuccessors(this).iterator();
		Iterator allarts = context.getObjects(Artifact.class).iterator();

		while (allarts.hasNext()) {
			Artifact arti = (Artifact) allarts.next();
			arti.setNewRank(0);
		}
		
		if (outDegree > 0) {
			double increment = this.getRank()/outDegree;
			Iterator outl = this.getOutLinks();
			while (outl.hasNext()) {
				Artifact sequent = (Artifact) outl.next();
				double oldrnk = sequent.getNewRank();
				sequent.setNewRank(oldrnk+increment);   
			}
		} else {
			double increment = this.getRank()/context.getObjects(Artifact.class).size();    
			while (allarts.hasNext()) {
				Artifact sequent = (Artifact) allarts.next();
				double oldrnk = sequent.getNewRank();
				sequent.setNewRank(oldrnk+increment);
		}
	}
		while (allarts.hasNext()) {
			Artifact arti = (Artifact) allarts.next();
			// Same as above
			arti.setRank((1-0.85)/(context.getObjects(Artifact.class).size()+(0.85*arti.getNewRank())));
		}
	}
}