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
	public int name;


	public Artifact(Agent author, double pagerank) {
		this.views = views;
		this.votes = votes;
		this.shares = shares;
		this.pagerank = pagerank;
		this.newrank = 0;
		this.birthday = 0;
		this.author = author;
		this.name = name++;
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
	
	
	public double getAge(){
		double now = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		double age = now-this.birthday;
		return age;
	}
	
	public void addView() {
		views++;
	}
	
	public void addVote() {
		votes++;
	}
	
	public void subtractVote() {
		votes--;
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
	
	public void setVotes(int voti) {
		this.votes = voti;
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
	
	public void buildLink(Artifact arti) {
		Parameters param = RunEnvironment.getInstance().getParameters();
		double recipro = (Double) param.getValue("avgReciprocating");
		Context context = (Context)ContextUtils.getContext(this);
		Network artifact = (Network)context.getProjection("artifacts");
		double birthday = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		artifact.addEdge(this, arti, birthday);
		if (RandomHelper.nextDoubleFromTo(0, 1)<=recipro) artifact.addEdge(arti, this, birthday+2);
	}

	
	
	@ScheduledMethod(start = 50, interval = 50)
	public void halveVotes() {
		if (this.getAge()>50) {
			int newVotes = this.getVotes()/2;
			this.setVotes(newVotes);
		}
	}
}