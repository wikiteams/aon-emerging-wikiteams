package internetz;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.util.ContextUtils;

public class Artifact {
	
	public int views;
	public int votes;
	public double birthday;
	public Agent author;
	public double newrank;
	private double pagerank;
	public int id;


	public Artifact(Agent author, double pagerank) {
		this.views = views;
		this.votes = votes;
		this.pagerank = pagerank;
		this.newrank = 0;
		this.birthday = 0;
		this.author = author;
		this.id = id;
	}
	
	
	public Iterator getMemes() {
		Context context = (Context)ContextUtils.getContext(this);
		Network artimeme = (Network)context.getProjection("artimemes");
		return artimeme.getSuccessors(this).iterator();
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
	
	public double getRank() {
		return pagerank;
	}
	
	public int getViews() {
		return views;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public void buildLink(Artifact arti) {
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		artifact.addEdge(this, arti);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void updatePageRnk() {   // Adapted from the netlogo 'diffusion' code (fingers crossed)
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		int degr = artifact.getOutDegree(this);
		Iterator all = (Iterator) artifact.getSuccessors(this).iterator();
		
		while (all.hasNext()) {
			Artifact arti = (Artifact) all.next();
			arti.newrank = 0;
		}
		
		if (degr > 0) {
			double increment = pagerank / degr;
			Iterator outl = (Iterator) this.getOutLinks();
			while (outl.hasNext()) {
				Artifact sequent = (Artifact) outl.next();
				sequent.newrank =+ increment;   
			}
		} else {
			double increment = pagerank / context.getObjects(Artifact.class).size();    
			// We don't use artifact.size() in the above computation because (probably) network projections
			// contain all the agents in the context!!!
			while (all.hasNext()) {
				Artifact sequent = (Artifact) all.next();
				sequent.newrank =+ increment;
		}
	}
		
		while (all.hasNext()) {
			Artifact arti = (Artifact) all.next();
			// Same as above			
			arti.pagerank = (1 - 0.85) / context.getObjects(Artifact.class).size() + 0.85 * arti.newrank;
		}
			 
	}
	

}
