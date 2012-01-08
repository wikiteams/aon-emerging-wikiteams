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
	int ownlinks = (Integer) param.getValue("linkswithown");
	int maxBeliefs = (Integer) param.getValue("maxbelief");
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
			if (status == 0) publish();
			status+=changeStatus();
			// System.out.println("Status now: " + status);
		}

		// Everybody explores
		explore();

		// Every now and then we update stuff
		if (time%5==0) {
			//// System.out.println("Hooray, I made it to the fifth step");
			updatebeliefs();
			corrupt(belief, maxBeliefs);
			corrupt(memory, maxBeliefs);
			corrupt(bookmarks, maxBeliefs);
			if (time > 100) killOldLinks();
		}
		// if ((time == 1)||(time % 100 == 0)) {
		//	int alivememes = countMemes();
		//	int allmemes = context.getObjects(Meme.class).size();
		//	// System.out.println("Alive memes: " + alivememes + "/" + allmemes);
		//	// // System.out.println("Alive memes: " + alivememes/allmemes);
		// }
	}

	public void explore() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		if (algo.equals("random")) {
			// // System.out.println("hello, i'm here");
			Iterable localarts = context.getRandomObjects(Artifact.class, this.readingCapacity);
			while (localarts.iterator().hasNext()) {
				Artifact localart = (Artifact)localarts.iterator().next();
				localart.addView();
				if (!localart.author.equals(this)&&!bookmarks.contains(localart)) {
					read(localart);
					bookmarks.add(localart);
				}
				// // System.out.println("I am now about to read an artifact");
			}
		} else {
			if (algo.equals("none")) {
				if (!bookmarks.isEmpty()) {
					int howmany = RandomHelper.nextIntFromTo(0, readingCapacity);
					exploreByLinks(howmany, bookmarks);
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
		int frustration = 0;
		int whichone = 0;
		Artifact nowreading = null;
		int size = startingset.size();
		// System.out.println(size);
		if (size>0) { 
			whichone = RandomHelper.nextIntFromTo(0, size-1);
			nowreading = (Artifact) startingset.get(whichone);
			if (size < capacity) capacity=size;
			// // System.out.println("E' uscito il numero " + whichone + " su " + startingset.size());
			// INFINITE LOOP HERE IN THE FIRST RUNS READS IS ALWAYS < HOWMANY
			while (reads < capacity) {
				if (!nowreading.author.equals(this)&&!bookmarks.contains(nowreading)) {
					//System.out.println(nowreading.author);
					read(nowreading);
					reads++;
					bookmarks.add(nowreading);
					nowreading.addView();  // the artifact gets a page view
					if (nowreading.getOutLinks().hasNext()) nowreading = (Artifact) nowreading.getOutLinks().next();
					else {
						//System.out.println("No links. I select another");
						whichone = RandomHelper.nextIntFromTo(0, size-1);
						nowreading = (Artifact) startingset.get(whichone);
						nowreading.addView();  // the artifact gets a page view
					}

				} else {
					//System.out.println("This is my artifact. i read something else");
					if (nowreading.getOutLinks().hasNext()) {
						nowreading = (Artifact) nowreading.getOutLinks().next();
						// System.out.println("There are links. I follow....");
						nowreading.addView();  // the artifact gets a page view
						frustration++;
					}

					else {
						// System.out.println("No links. I select another");
						whichone = RandomHelper.nextIntFromTo(0, size-1);
						nowreading = (Artifact) startingset.get(whichone);
						nowreading.addView();  // the artifact gets a page view
						// System.out.println(nowreading);
						frustration++;
					}
				}
				if (frustration>15) break;
			}
		}
	}

	public void explorebymemes() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		Meme currentmeme = (Meme) belief.getRandomAdjacent(this);
		int howmany = RandomHelper.nextIntFromTo(0, readingCapacity);
		if (algo.equals("mix")) howmany/=2 ;
		if (algo.equals("redditmix")) howmany/=3;
		ArrayList all = getTransformedIteratorToArrayList(artimeme.getAdjacent(currentmeme).iterator());
		if (algo.equals("pagerank")||algo.equals("mix")||algo.equals("redditmix")) Collections.sort(all, new PageRankComparator());
		// if (algo.equals("popularity")) Collections.sort(all, new PopularityComparator());
		suck(howmany,all);
		if (algo.equals("mix")||algo.equals("redditmix")) exploreByLinks(howmany+1,all);
		if (algo.equals("redditmix")) {
			// Uncomment the following if you want reddit to feed the most voted artifacts in (i.e. reddit frontpage)
			// Otherwise the agent will read most voted artifacts only relative to his meme of interest (i.e. a subreddit)
			all = getTransformedIteratorToArrayList(context.getObjects(Artifact.class).iterator());
			Collections.sort(all, new VoteComparator());
			suck(howmany+1,all);
		}
	}

	public void suck(int capacity, ArrayList startingset) {
		int i=0; // read artifacts
		int a=0; // artifacts not read because unsuitable
		int size = startingset.size();
		if (size < capacity) capacity = size;
		while (i < capacity) {
			// Here we need a constraint. It need not be a creature of the reader
			// nor recently bookmarked
			Artifact arti = (Artifact) startingset.get(i);
			if (!arti.author.equals(this)&&!bookmarks.contains(arti)) {
				read(arti);
				arti.addView();  // the artifact gets a page view
				bookmarks.add(arti);
				i++;
			} else a++;
			if (a+i==size) break;
		}
	}

	public void read (Artifact arti) {
		double sticksInMem = (Double) param.getValue("sticksInMem");
		Iterator memez = arti.getMemes();
		boolean known = false;
		int howsimilar = 0;
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();				
			if (belief.isAdjacent(this, thismeme)) {
				known = true;
				howsimilar++;
				// // System.out.println("I know this stuff: " + howsimilar);
			}
		}
		if (known) {
			sticksInMem+=0.25;
			double prob = (howsimilar / 8) + 0.05; // Questo 8 va controllato. 
			vote(arti, prob);
		} 
		// else sticksInMem-=0.25;
		memez = arti.getMemes();
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();	
			if (memory.isAdjacent(this, thismeme)) {
				RepastEdge lnk = memory.getEdge(this, thismeme);
				if (RandomHelper.nextDoubleFromTo(0, 1)<=sticksInMem) lnk.setWeight(lnk.getWeight() + 1);
			}
			else {
				if (RandomHelper.nextDoubleFromTo(0, 1)<=sticksInMem) {
					// // System.out.println("We have " + memory.size() + " memories overall");
					memory.addEdge(this, thismeme, 1);
				}
			}
		}
	}

	public void publish() {
		Context<Object> context = (Context)ContextUtils.getContext(this);		
		Artifact newArt = new Artifact(this, 0);
		newArt.views = 0;
		newArt.votes = 0;
		newArt.birthday = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		newArt.id = context.getObjects(Artifact.class).size() + 1;
		// // System.out.println("Just created the artifact #: " + newArt.id);
		context.add(newArt);
		creatures.add(newArt);
		// System.out.println("We have " + creatures.size() + " creatures");

		// WARNINGWARNING: magic number to be replaced here
		for (int i=0; i<6; i++) {
			Meme investingmeme = (Meme) belief.getRandomAdjacent(this);
			artimeme.addEdge(investingmeme, newArt, 1);
			// System.out.println("I have just put some memes in the artifact");
		}

		// MAGIC NUMBER HERE!
		if (creatures.size() > 5) {
			// System.out.println("I have a lot of creatures, will link them");
			linkWithOwn(newArt); 
		}
		if (!bookmarks.isEmpty()) {
			// System.out.println("I have some bookmarks, will link them");
			link(newArt);
		}
		else {
			// System.out.println("I do not have bookmarks, will link randomly");
			linkOnce(newArt);
		}
	}

	// The first time we link with a random artifact (if there is one)
	public void linkOnce(Artifact newart) {
		Context<Object> context = (Context)ContextUtils.getContext(this);
		int howmany = context.getObjects(Artifact.class).size();
		//// System.out.println("We have " + howmany + " artifacts");
		if (howmany > 0) {
			Artifact arti = (Artifact) context.getRandomObjects(Artifact.class, 1).iterator().next();
			if (!arti.equals(newart)) {
				newart.buildLink(arti);
				// System.out.println("i have successfully linked the artifact");
				read(arti);
			}
		}
	}

	public void linkWithOwn(Artifact arti) {
		ArrayList<Artifact> towhom = (getMostSimilar(creatures, arti));
		int similars = towhom.size();
		int howmany = 0;
		if (similars >= ownlinks) howmany = ownlinks;
		else howmany = similars;
		for (int i=0; i<=howmany-1; i++) {
			Artifact oldart = towhom.get(i);
			arti.buildLink(oldart);
			oldart.buildLink(arti);
		}
	}

	// The next function is meant to be a generic memetic similarity extractor and should replace
	// the chunks of code where memetic comparations are performed: in link() and linkwithown() 

	public ArrayList<Artifact> getMostSimilar(ArrayList<Artifact> list, Artifact source) {
		// System.out.println("Hello. I'm now looking for the most similar artifact");
		ArrayList<Artifact> mostSimilarArtifacts = new ArrayList();
		int oldMostSimilarMemes = 0;
		ArrayList newlist = list;
		if (newlist.contains(source)) newlist.remove(source);
		int listSize = newlist.size();
		int i = 0;
		while (i<listSize) {
			Artifact oldart = (Artifact) newlist.get(i);
			Iterator oldArtifactMemes = oldart.getMemes();
			int newMostSimilarMemes = 0;
			while (oldArtifactMemes.hasNext()) if (artimeme.isAdjacent(oldArtifactMemes.next(),source)) newMostSimilarMemes++;
			if (oldMostSimilarMemes == newMostSimilarMemes) mostSimilarArtifacts.add(oldart);
			else {
				if (newMostSimilarMemes > oldMostSimilarMemes) {
					mostSimilarArtifacts.clear();
					mostSimilarArtifacts.add(oldart);
					oldMostSimilarMemes = newMostSimilarMemes;
				}
			}
			i++;
		}
		// if (mostSimilarArtifacts.isEmpty()) // System.out.println("Something went wrong");
		// else // System.out.println("I have just selected an artifact");
		return mostSimilarArtifacts;
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
		int size = mostsimilar.size();
		// System.out.println("I have to link the artifact with " + size + " others");
		for (int i=0; i<size;i++) {
			Artifact arti = (Artifact) mostsimilar.get(i);
			newart.buildLink(arti);
		}
		// // System.out.println("I have linked the artifact with a bookmark");
	}


	public void corrupt(Network net, int max) {
		ArrayList alledges = getTransformedIteratorToArrayList(net.getEdges(this).iterator());
		int alledgesNo = alledges.size();
		int howmanydeaths = 0;
		Collections.sort(alledges, new InverseWeightComparator());
		if (alledgesNo > max) {
			howmanydeaths = (alledgesNo-max)-1;
			if (alledges.size() >= howmanydeaths) {
				for (int i=0; i<howmanydeaths; i++) {
					// RepastEdge link = (RepastEdge) alledges.get(i);
					// // System.out.println(link.getWeight()); // This is to TEST that it does what it does.
					net.removeEdge((RepastEdge) alledges.get(i));
					//System.out.println("Removing...");
				}
			}
		} 
		else {
			// RepastEdge link = (RepastEdge) alledges.iterator().next(); // Hopefully this kills only one edge. CHECK!!
			if (alledges.size()>2) {
				net.removeEdge((RepastEdge) alledges.get(alledgesNo-1));
				// // System.out.println("Only removing one");
			}
		}
		// // System.out.println("I am CORRUPTING!!");
	}

	public void corrupt(ArrayList list, int max) {
		int size = list.size();
		int howmanydeaths = 0;
		// // System.out.println("This list is " + size + " long. Should be " + max);
		if (size>max) {
			howmanydeaths = size-max;	
			if (list.size() >= howmanydeaths) for (int i=0; i<howmanydeaths-2; i++) list.remove(i);
		} else if (size>2) list.remove(0);
	}

	public void vote(Artifact arti, double probability) {
		if ((RandomHelper.nextDoubleFromTo(0, 1) < probability) && (!voted.contains(arti))) {
			arti.addVote();
			voted.add(arti);
			//System.out.println("I voted");
		}
	}

	public void killOldLinks() {
		ArrayList allinks = getTransformedIteratorToArrayList(artifact.getEdges().iterator());
		Collections.sort(allinks, new InverseWeightComparator());
		// RepastEdge max = (RepastEdge) allinks.get(0);
		double maxweight = ((RepastEdge) allinks.get(0)).getWeight();
		for (int i=0; i<allinks.size(); i++) {
			//	// System.out.println(" index " + i);
			RepastEdge link = (RepastEdge) allinks.get(i);
			double wght = link.getWeight(); 
			if (wght <= maxweight) {
				if (RandomHelper.nextDoubleFromTo(0, 1)<=0.25) allinks.remove(i);
			} 
			else break;
		}
	}

	public void updatebeliefs() {
		// // System.out.println("We have " + memory.getDegree(this) + " memories");
		ArrayList memz = getTransformedIteratorToArrayList(memory.getEdges(this).iterator());
		if (memz.size() > 1) {
			Collections.sort(memz, new WeightComparator());
			RepastEdge max = (RepastEdge) memz.get(0);
			double maxweight = max.getWeight();
			// // System.out.println("Maximum weight = " + maxweight);
			for (int i=0; i<memz.size(); i++) {
				//		// System.out.println(" index " + i);
				RepastEdge link = (RepastEdge) memz.get(i);
				double wght = link.getWeight(); 
				if (wght >= maxweight) {
					// // System.out.println("This link's weight is " + wght);
					Meme meme = (Meme) link.getTarget(); // WARNING: Using 'target' on unoriented network
					// if (meme == null) // System.out.println("HELL NO");
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
	}

	public int changeStatus() {
		if (RandomHelper.nextDoubleFromTo(0, 1) > 0.5) return 1;
		return -1;
	}
}