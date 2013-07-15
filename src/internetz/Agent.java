package internetz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
//import repast.simphony.engine.watcher.Watch;
//import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.PropertyEquals;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class Agent {

//	Parameters param = RunEnvironment.getInstance().getParameters();
//	String algo = (String)param.getValue("filteringalgo");
//	int ownlinks = (Integer)param.getValue("linkswithown");
//	int maxBeliefs = (Integer)param.getValue("maxbelief");
	SimulationParameters Sims = new SimulationParameters();
	int status = 0;
	int socialreads = 0;
	
	final static double weightIncrease = 0.1;
	final static double memeWeightDecrease = 0.0005;
	final static double memeWeightInitial = 0.5;
	final static double learnIncreaseIfKnown = 0.25;
	final static int maxArtifactsToLinkTo = 15;
	final static int maxFollowing = 500;
	final static int endTick = 2000;
	int grp;
	static int totalAgents = 0;
	Network<Object> memory;
	Network<Object> artimeme;
	Network<Object> belief;
	Network<Agent> sns;

	boolean isPublisher;
	private int readingCapacity;
	//private int artifactsShared;
	private int reads;
	
	private int id;
	private String firstname;
	private String lastname;

	public Agent() {
		say ("Agent constructor called");
		this.id = ++totalAgents;
		// this.readingCapacity = readingCapacity;
		// this.isPublisher = isPublisher;
		this.status = status;
		this.grp = grp;
		//this.artifactsShared=0;
		this.reads = 0;

	}

	public void setReadingCapacity(int readingCapacity) {
		this.readingCapacity = readingCapacity;

	}

	public void setGroup(int group) {
		this.grp = group;
	}

	public int getGroup() {
		return this.grp;
	}

	public void setPublisher(boolean isPublisher) {
		this.isPublisher = isPublisher;

	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	private void say(String s) {
		System.out.println(s);
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
		say("Step() of Agent scheduled method launched..");
		
		Context context = (Context)ContextUtils.getContext(this);
		belief = (Network)context.getProjection("beliefs");
		memory = (Network)context.getProjection("memorys");
		artimeme = (Network)context.getProjection("artimemes");
		sns = (Network)context.getProjection("twitter");
		
		//if (RunEnvironment.getInstance().getCurrentSchedule().getTickCount() == 15) {
		//	System.out.println("Ending. Total number of links is: " + sns.getDegree());
		//	RunEnvironment.getInstance().endRun();
		//}
		RunEnvironment.getInstance().endAt(endTick);

	
		if (isPublisher) {
			if (status == 0) publish();
			status+=changeStatus();
		}
		
		explore();

		updateblfs();
		updatememz();
	}


	private void explore() {
		// TODO Auto-generated method stub
		
	}

	private void publish() {
		// TODO Auto-generated method stub
		
	}

	public void exploreByLinks(int capacity, ArrayList startingset) {
		//
	}

	public void explorebymemes() {
		//....??????????
	}
	
	public void exploreSocial(int howmany) {
		//.......??
	}

	public void suck(int capacity, ArrayList startingset) {
		//....
	}


	public ArrayList getTransformedIteratorToArrayList(Iterator itr){
		ArrayList arr = new ArrayList();
		while(itr.hasNext()){
			arr.add(itr.next());
		}
		return arr;
	}


	@ScheduledMethod(start = 5, interval = 5)
	public void corrupt() {
		say("corrupt scheduled method lunched...");
	}
	

	public void decreaseSocial(Agent friend) {
		Context context = (Context)ContextUtils.getContext(this);
		sns = (Network)context.getProjection("twitter");
		if (sns.isPredecessor(this, friend)) {
			RepastEdge link = sns.getEdge(this, friend);
			double weight = link.getWeight();
			if (weight > weightIncrease) {
				link.setWeight(weight-weightIncrease);
				
				//System.out.println("i'm decreasing my friendship. It is now "+link.getWeight());
			} else {
				sns.removeEdge(link);
				//System.out.println("A friend is no more");
			}
		}
	}
	

	public void updateblfs() {
		Context context = (Context)ContextUtils.getContext(this);
		belief = (Network)context.getProjection("beliefs");
		ArrayList blfs = getTransformedIteratorToArrayList(belief.getEdges(this).iterator());
		for (int i=0;i<blfs.size();i++) {
			RepastEdge blf = (RepastEdge) blfs.get(i);
			double wght = blf.getWeight();
			if (wght<=0) {
				//if (RandomHelper.nextDoubleFromTo(0, 1)>0.50) belief.removeEdge(blf);
				// We don't remove a meme anymore.
				 blf.setWeight(0);
			} else blf.setWeight(wght-memeWeightDecrease);
		}
	}

		
	public void updatememz() {
		Context context = (Context)ContextUtils.getContext(this);
		memory = (Network)context.getProjection("memorys");
		ArrayList mmrs = getTransformedIteratorToArrayList(memory.getEdges(this).iterator());
		for (int i=0;i<mmrs.size();i++) {
			RepastEdge mmr = (RepastEdge) mmrs.get(i);
			double wgt = mmr.getWeight();
			if (wgt<=0) {
				if (RandomHelper.nextDoubleFromTo(0, 1)>0.50) memory.removeEdge(mmr);
			} else mmr.setWeight(wgt-memeWeightDecrease);	
		}
	}
		
	

	public int changeStatus() {
		if (RandomHelper.nextDoubleFromTo(0, 1) > 0.5) return 1;
		return -1;
	}

	public double getSilo() {
		return 0;//...?????????
	}
	
	
	/*
	 * This Should be turned on only in the social case. 
	 * We explore the people we follow and read something shared by their friends.
	 */
	@ScheduledMethod(start = 4, interval = 5)
	public void trackFriends() {
		Context context = (Context)ContextUtils.getContext(this);
		sns = (Network)context.getProjection("linkedin");
		if (sns.getDegree(this)>1) {
			Agent sharer = sns.getRandomAdjacent(this);
			int exploreHowMany = 3;
			for (int i=0; i<exploreHowMany;i++) {
				if (sns.getOutDegree(sharer)>0) {
					Agent friend = sns.getRandomSuccessor(sharer);
//					if(friend.shared.size()>0) {
//						int siz = friend.shared.size();
//						Artifact toRead = friend.shared.get(siz-1); 
//						read(toRead,friend);
//					}
				}
			}
		}
	}
	
	public double getReads() {
		return this.reads/endTick;
	}
	
	public String toString(){
		return "id: " + id + " name: " + firstname + " " + lastname;
	}
}