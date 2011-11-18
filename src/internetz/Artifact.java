package internetz;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Artifact {
	Context context = (Context)ContextUtils.getContext(this);
	// Network belief = (Network)context.getProjection("belief network");
	// Network memory = (Network)context.getProjection("memory network");
	Network artimeme = (Network)context.getProjection("artimeme network");
	Network artifact = (Network)context.getProjection("artifact network");
	public int views;
	public int votes;
	public double birthday;
	public Agent author;
	public double newrank;
	private double pagerank;


	public Artifact(Agent author, double pagerank) {
		this.views = views;
		this.votes = votes;
		this.pagerank = pagerank;
		this.newrank = 0;
		this.birthday = 0;
		this.author = author;
	}
	
	
	public Iterable getMemes() {
		return artimeme.getAdjacent(this);
	}
	
	public Iterable getOutLinks() {
		return artifact.getSuccessors(this);
	}
	
	public Iterable getInLinks() {
		return artifact.getPredecessors(this);
	}
	
	public double getRank() {
		return pagerank;
	}
	
	public int getViews() {
		return views;
	}
	
	public int getVotes() {
		return votes;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void updatePageRnk() {   // Adapted from the netlogo code
		int degr = artifact.getOutDegree(this);
		Iterator all = (Iterator) artifact.getEdges();
		
		while (all.hasNext()) {
			Artifact arti = (Artifact) all.next();
			arti.newrank = 0;
		}
		
		if (degr > 0) {
			double increment = pagerank / degr;
			Iterator outl = (Iterator) getOutLinks();
			while (outl.hasNext()) {
				Artifact sequent = (Artifact) outl.next();
				sequent.newrank =+ increment;   
			}
		} else {
			double increment = pagerank / artifact.size();
			while (all.hasNext()) {
				Artifact sequent = (Artifact) all.next();
				sequent.newrank =+ increment;
		}
	}
		
		while (all.hasNext()) {
			Artifact arti = (Artifact) all.next();
			arti.pagerank = (1 - 0.85) / artifact.size() + 0.85 * arti.newrank;
		}
			 
	}
	

}
