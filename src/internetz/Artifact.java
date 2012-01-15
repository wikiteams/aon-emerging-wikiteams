package internetz;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
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
	
	
	public void addView() {
		views++;
	}
	
	public void addVote() {
		votes++;
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
			double increment = this.getRank() / outDegree;
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