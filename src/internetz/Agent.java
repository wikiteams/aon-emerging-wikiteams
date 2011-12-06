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

public class Agent {

	Parameters param = RunEnvironment.getInstance().getParameters();
	String algo = (String)param.getValue("filteringalgo");
	int ownlinks = (int) param.getValue("linkswithown");
	int maxBeliefs = (int) param.getValue("maxbelief");
	int status = 0;
	Network memory;
	Network artimeme;
	Network<Artifact> artifact;
	Network belief;
	
	
	boolean isPublisher;
	private int readingCapacity;
	private ArrayList<Artifact> bookmarks = new ArrayList();
	private ArrayList<Artifact> creatures = new ArrayList();
	private ArrayList<Artifact> voted = new ArrayList();
	
	public Agent() {

		// this.readingCapacity = readingCapacity;
		// this.isPublisher = isPublisher;
		this.bookmarks = bookmarks;
		this.creatures = creatures;
		this.maxBeliefs = maxBeliefs;
		this.status = status;
		
		// This is now moved in the context:
		// RandomHelper.createPoisson(maxbeliefs/2);
		// int howmany = RandomHelper.getPoisson().nextInt();
	
	}
	
	
	
	public void setReadingCapacity(int readingCapacity) {
		this.readingCapacity = readingCapacity;
		
	}
	
	public void setPublisher(boolean isPublisher) {
		this.isPublisher = isPublisher;
		
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		Context context = (Context)ContextUtils.getContext(this);	
		memory = (Network)context.getProjection("memorys");
		artimeme = (Network)context.getProjection("artimemes");
		artifact = (Network<Artifact>)context.getProjection("artifacts");
		belief = (Network)context.getProjection("beliefs");
		int time = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		
		// Publishers have their chance to publish
		if (isPublisher) {
			if (status == 0){
				publish();
				status = status + changeStatus();
				System.out.println("Status is now " + status);
			}
		}
		
		// Everybody explores
		explore();
		
		// Every now and then we update stuff
		if (time % 5 == 0) {
			System.out.println("Hooray, I made it to the fifth step");
			updatebeliefs();
			corrupt(belief, maxBeliefs);
			corrupt(memory, maxBeliefs);
			corrupt(bookmarks, maxBeliefs);
			if (time > 100) killOldLinks();
		}
	}
	
	public void explore() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		if (algo.equals("random")) {
			// System.out.println("hello, i'm here");
			Iterable localarts = context.getRandomObjects(Artifact.class, this.readingCapacity);
			while (localarts.iterator().hasNext()) {
				Artifact localart = (Artifact)localarts.iterator().next();
				if (!localart.author.equals(this)) read(localart);
				// System.out.println("I am now about to read an artifact");
			}
		} else {
			if (algo.equals("none")) {
				if (!bookmarks.isEmpty()) {
					exploreByLinks(readingCapacity, bookmarks);
				} else {
					ArrayList allarts = getTransformedIteratorToArrayList((context.getObjects(Artifact.class)).iterator());
					if (allarts.size() > 0) exploreByLinks(readingCapacity, allarts);
				}
			} else {
				explorebymemes();
			}
		}
	}
	
	public void exploreByLinks(int capacity, ArrayList startingset) {
		int reads = 0;
		int howmany = RandomHelper.nextIntFromTo(0, capacity);
		int whichone = RandomHelper.nextIntFromTo(0, startingset.size()-1);
		Artifact nowreading = (Artifact) startingset.get(whichone);
		if (startingset.size() < howmany) howmany = startingset.size()-1;
		// System.out.println("E' uscito il numero " + whichone + " su " + startingset.size());
		// INFINITE LOOP HERE IN THE FIRST RUNS READS IS ALWAYS < HOWMANY
		while (reads < howmany) {
			if (!nowreading.author.equals(this)) {
				// System.out.println("not me. now i read");
				read(nowreading);
				reads++;
				bookmarks.add(nowreading);
				if (nowreading.getOutLinks().hasNext()) nowreading = (Artifact) nowreading.getOutLinks().next();
				else {
					whichone = RandomHelper.nextIntFromTo(0, startingset.size()-1);
					nowreading = (Artifact) startingset.get(whichone);
				}
			} else {
				// System.out.println("This is my artifact. i read something else");
				if (nowreading.getOutLinks().hasNext()) {
					nowreading = (Artifact) nowreading.getOutLinks().next();
					//System.out.println("There are links. I follow....");
				}
				else {
					//System.out.println("No links. I select another");
					whichone = RandomHelper.nextIntFromTo(0, startingset.size()-1);
					nowreading = (Artifact) startingset.get(whichone);
				}
			}
		}
	}
	
	public void explorebymemes() {
		Meme currentmeme = (Meme) belief.getRandomAdjacent(this);
		int howmany = RandomHelper.nextIntFromTo(0, readingCapacity);
		ArrayList all = getTransformedIteratorToArrayList(artimeme.getAdjacent(currentmeme).iterator());
		switch (algo) {
		case "pagerank": Collections.sort(all, new PageRankComparator());
		case "popularity": Collections.sort(all, new PopularityComparator());
		case "reddit": Collections.sort(all, new VoteComparator());
		}
		int i = 0;
		int size = all.size();
		if (size < howmany) howmany = size;
		while (i < howmany) {
			// Here we need a constraint. It need not be a creature of the reader
			// nor recently bookmarked
			Artifact arti = (Artifact) all.get(i);
			if (!arti.author.equals(this)) {
				i++;
				read(arti);
				bookmarks.add(arti);
			}
		}	
	}
	
	
	public void read (Artifact arti) {
		arti.views++;  // the artifact gets a page view
		Iterator memez = arti.getMemes();
		boolean known = false;
		int howsimilar = 0;
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();				
			if (belief.isAdjacent(this, thismeme)) {
				known = true;
				howsimilar++;
				System.out.println("I know this stuff: " + howsimilar);
			}

			// WARNINGWARNING WARNING
			// We are adding weight to ALL the re-encountered memory memes.
			// Is this correct? Is this desirable?? 
			// Shouldn't we add a probability?

			if (memory.isAdjacent(thismeme, this)) {
				known = true;
				RepastEdge lnk = memory.getEdge(this, thismeme);
				lnk.setWeight(lnk.getWeight() + 1);
			}
		}
		if (known) {
			double prob = (howsimilar / 8) + 0.05; // Questo 8 va controllato. 
			vote(arti, prob);
		}
		// The artifact is completely new. 
		// We build a memory with memes contained
		while (memez.hasNext()) {
			System.out.println("I'm creating memories.....");
			if ((RandomHelper.nextDoubleFromTo(0, 1)>=0.50)&&!memory.isAdjacent(this, memez.next())) memory.addEdge(this, memez.next(), 1);
		}
	}

	public void publish() {
		Context<Object> context = (Context)ContextUtils.getContext(this);		
		Artifact newArt = new Artifact(this, 0);
		newArt.views = 0;
		newArt.votes = 0;
		newArt.birthday = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		newArt.id = context.getObjects(Artifact.class).size() + 1;
		System.out.println("Just created the artifact #: " + newArt.id);
		context.add(newArt);
		creatures.add(newArt);
		System.out.println("We have " + creatures.size() + " creatures");
		
		// WARNINGWARNING: magic number to be replaced here
		for (int i=0; i<4; i++) {
			Meme investingmeme = (Meme) belief.getRandomAdjacent(this);
			artimeme.addEdge(investingmeme, newArt, 1);
		}
		
		// MAGIC NUMBER HERE!
		if (creatures.size() > 5) linkWithOwn(newArt); 
		if (!bookmarks.isEmpty()) link(newArt);
		else linkOnce(newArt);
	}
	
	// The first time we link with a random artifact (if there is one)
	public void linkOnce(Artifact newart) {
		Context<Object> context = (Context)ContextUtils.getContext(this);
		int howmany = context.getObjects(Artifact.class).size();
		System.out.println("We have " + howmany + " artifacts");
		if (howmany > 0) {
			Artifact arti = (Artifact) context.getRandomObjects(Artifact.class, 1).iterator().next();
			if (!arti.equals(newart)) {
				newart.buildLink(arti);
				System.out.println("i have successfully linked the artifact");
				read(arti);
			}
		}
	}
	
	public void linkWithOwn(Artifact arti) {
		Iterator<Artifact> towhom = (getMostSimilar(creatures, arti)).iterator();
		for (int i=0; i<=ownlinks; i++) {
			Artifact oldart = towhom.next();
			arti.buildLink(oldart);
			oldart.buildLink(arti);
		}
	}
	
	// The next function is meant to be a generic memetic similarity extractor and should replace
	// the chunks of code where memetic comparations are performed: in link() and linkwithown() 
	
	public ArrayList<Artifact> getMostSimilar(ArrayList<Artifact> list, Artifact source) {
		ArrayList<Artifact> mostsimilar = new ArrayList();
		int oldbest = 0;
		ArrayList newlist = list;
		newlist.remove(source);
		while (newlist.iterator().hasNext()) {
			Artifact oldart = (Artifact) newlist.iterator().next();
			Iterator oldmemes = oldart.getMemes();
			int memesimilar = 0;
			while (oldmemes.hasNext()) {
				if (artimeme.isAdjacent(oldmemes.next(),source)) memesimilar++;
			}
			if (oldbest == memesimilar) mostsimilar.add(oldart);
			else {
				if (oldbest < memesimilar) {
					mostsimilar.clear();
					mostsimilar.add(oldart);
					oldbest = memesimilar;
				}
			}
		}
		if (mostsimilar.isEmpty()) System.out.println("Something went wrong");
		else System.out.println("I have just selected an artifact");
		return mostsimilar;
	}
	
	// This is a brilliant Iterator --> ArrayList converter made by Ali' in 27 seconds while I was
	// asking myself how to do it.
	public ArrayList getTransformedIteratorToArrayList(Iterator itr){
		ArrayList arr = new ArrayList();
		while(itr.hasNext()){
			arr.add(itr.next());
		}
		return arr;
	}
	
	public void link(Artifact newart) { // RECHECK THIS
		ArrayList mostsimilar = getMostSimilar(bookmarks, newart);
		int index = 0;
		while (index < mostsimilar.size()) {
			newart.buildLink((Artifact) mostsimilar.get(index));
			index++;
		}
	}
	

	public void corrupt(Network net, int max) {
		ArrayList alledges = getTransformedIteratorToArrayList(net.getEdges(this).iterator());
		int alledgesNo = alledges.size();
		Collections.sort(alledges, new InverseWeightComparator());
		if (alledgesNo > max) {
			int howmanydeaths = alledgesNo - max; 
			for (int i=0; i<howmanydeaths; i++) {
				RepastEdge link = (RepastEdge) alledges.get(i);
			//	System.out.println(link.getWeight()); // This is to TEST that it does what it does.
				net.removeEdge(link);
			}
		} else {
			// RepastEdge link = (RepastEdge) alledges.iterator().next(); // Hopefully this kills only one edge. CHECK!!
			net.removeEdge((RepastEdge) alledges.get(alledgesNo-1));
		}
		System.out.println("I am CORRUPTING!!");
	}
	
	public void corrupt(ArrayList list, int max) {
		int size = list.size();
		System.out.println("This list is " + size + " long. Should be " + max);
		if (size>max) {
			int howmanydeaths = size-max;
			for (int i=0; i<howmanydeaths; i++) list.remove(i);
		} else {
			if (size>2) list.remove(0);
		}
	}
	
	public void vote(Artifact arti, double probability) {
		if ((RandomHelper.nextDoubleFromTo(0, 1) < probability) && (!voted.contains(arti))) {
			arti.votes++;
			voted.add(arti);
		}
	}
	
	public void killOldLinks() {
		ArrayList allinks = getTransformedIteratorToArrayList(artifact.getEdges().iterator());
		Collections.sort(allinks, new InverseWeightComparator());
		// RepastEdge max = (RepastEdge) allinks.get(0);
		double maxweight = ((RepastEdge) allinks.get(0)).getWeight();
		for (int i=0; i<allinks.size(); i++) {
			System.out.println(" index " + i);
			RepastEdge link = (RepastEdge) allinks.get(i);
			double wght = link.getWeight(); 
			if (wght <= maxweight) {
				if (RandomHelper.nextDoubleFromTo(0, 1)<=0.25) allinks.remove(i);
			} 
			else break;
		}
	}
	
	public void updatebeliefs() {
		ArrayList memz = getTransformedIteratorToArrayList(memory.getEdges(this).iterator());
		System.out.println("We have " + memz.size() + " memories");
		Collections.sort(memz, new WeightComparator());
		
		RepastEdge max = (RepastEdge) memz.get(0);
		double maxweight = max.getWeight();

		// System.out.println("Maximum weight = " + maxweight);

		for (int i=0; i<memz.size(); i++) {
			System.out.println(" index " + i);
			RepastEdge link = (RepastEdge) memz.get(i);
			double wght = link.getWeight(); 
			if (wght >= maxweight) {
				System.out.println("This link's weight is " + wght);
				Meme meme = (Meme) link.getTarget(); // WARNING: Using 'target' on unoriented network
				// if (meme == null) System.out.println("HELL NO");
				if (belief.isAdjacent(this, meme)) {
					RepastEdge thisbelief = belief.getEdge(this, meme);
					thisbelief.setWeight(thisbelief.getWeight() + 1);
				} else {
					link.setWeight(1);
					belief.addEdge(this, meme, 1);
				}
			} else break;
		}

		// if (ispublisher) {  	// This is no longer necessary.
		//	relink(meme);		// We now have memetic similarity in linkwithown()
		// }
	}
	
	public int changeStatus() {
		if (RandomHelper.nextDoubleFromTo(0, 1) > 0.5) return 1;
		else return -1;
	}
}