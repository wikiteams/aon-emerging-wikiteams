package internetz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
	int maxBeliefs;
	Network memory;
	Network artimeme;
	Network<Artifact> artifact;
	Network belief;
	
	
	private boolean isPublisher;
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
		double time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (isPublisher) {
			publish();
		}
		explore();
		if (time % 5 == 0) {
			updatebeliefs();
			corrupt(belief, maxBeliefs);
			corrupt(memory, maxBeliefs);
			corrupt(bookmarks, maxBeliefs);
			if (time > 100) killoldlinks();
		}
	}
	
	public void explore() {
		Context<Object> context = (Context)ContextUtils.getContext(this);		
		if (algo == "random") {
			Iterable localarts = context.getRandomObjects(Artifact.class, this.readingCapacity);
			while (localarts.iterator().hasNext()) {
				Artifact localart = (Artifact)localarts.iterator().next();
				if (localart.author != this) read(localart);
			}
		} else {
			if (algo == "none") {
				if (bookmarks != null) {  // we need to make sure that null means "empty array"
					explorebylinks(readingCapacity, bookmarks);
				}
			} else {
				explorebymemes();
			}
		}
	}
	
	public void explorebylinks(int howmany, ArrayList startingset) {
		int reads = 0;
		Artifact nowreading = null;
		while (reads < howmany) {
			nowreading = (Artifact) startingset.get(RandomHelper.nextIntFromTo(0, startingset.size()));
			if (nowreading.author != this) {
				read(nowreading);
				reads++;
				bookmarks.add(nowreading);
				nowreading = (Artifact) nowreading.getOutLinks().iterator().next();
			}
		}
	}
	
	public void explorebymemes() {
		Meme currentmeme = (Meme) belief.getRandomAdjacent(this);
		int howmany = RandomHelper.nextIntFromTo(0, readingCapacity);
		List<Artifact> all = (List<Artifact>) artimeme.getAdjacent(currentmeme);
		
		switch (algo) {
		case "pagerank": Collections.sort(all, new PageRankComparator());
		case "popularity": Collections.sort(all, new PopularityComparator());
		case "reddit": Collections.sort(all, new VoteComparator());
		}
		int i = 0;
		while (i < howmany) {
			// Here we need a constraint. It need not be a creature of the reader
			// nor recently bookmarked
			Artifact arti = (Artifact) ((Iterator) all).next();
			if (arti.author != this) {
				i++;
				read(arti);
				bookmarks.add(arti);
			}
		}	
	}
	
	
	public void read (Artifact arti) {
		arti.views++;  // the artifact gets a page view
		Iterator memez = arti.getMemes().iterator();
		boolean known = false;
		int howsimilar = 0;
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();				
				if (belief.isAdjacent(thismeme, this)) {
					known = true;
					howsimilar++;
				}
				 
				// WARNINGWARNING WARNING
				// We are adding weight to ALL the re-encountered memory memes.
				// Is this correct? Is this desirable?? 
				// Shouldn't we add a probability?

				if (memory.isAdjacent(thismeme, this)) {
					known = true;
					RepastEdge lnk = memory.getEdge(thismeme, this);
					lnk.setWeight(lnk.getWeight() + 1);
				}
			}
		if (known) {
			double prob = (howsimilar / 8) + 0.05; // Questo 8 va controllato. 
			vote(arti, prob);
			}
		 else {
			// The artifact is completely new. 
			// We build a memory with a couple of memes contained
			// WARNING. Another magic number
			for (int i=0; i<=2; i++) {
				Meme meme = (Meme) artimeme.getRandomAdjacent(arti);
				memory.addEdge(this, meme, 1);			
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

		context.add(newArt);
		creatures.add(newArt);
		
		// WARNINGWARNING: magic number to be replaced here
		for (int i=0; i<4; i++) {
		Meme investingmeme = (Meme) belief.getRandomAdjacent(this);
		artimeme.addEdge(investingmeme, newArt, 1);
		}
		
		if (!creatures.isEmpty()) linkwithown(newArt); 
		
		if (bookmarks != null) link(newArt);
		else linkonce(newArt);
	}
	
	
	public void linkonce(Artifact newart) {  // We should have birthday as edge attribute
		Iterator allarts = artifact.getNodes().iterator();
		while (allarts.hasNext()) {
			for (int i=0; i < artifact.size()/4; i++) {
				Artifact arti = (Artifact) allarts.next();
				if (newart != arti) {
					double birthday = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
					artifact.addEdge(newart, arti, birthday);
					read(arti);
				}
			}
		}
	}
	
	public void linkwithown(Artifact arti) {
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
		while (list.iterator().hasNext()) {
			Artifact oldart = (Artifact) list.iterator().next();
			Iterator oldmemes = oldart.getMemes().iterator();
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
		return mostsimilar;
	}
	
	public void link(Artifact newart) { // RECHECK THIS
		ArrayList mostsimilar = getMostSimilar(bookmarks, newart);
		while (mostsimilar.iterator().hasNext()) {
		newart.buildLink((Artifact) mostsimilar.iterator().next());
		}
	}
	
	public void updatebeliefs() {
		List<RepastEdge> memz = (List<RepastEdge>) memory.getEdges(this);
		Collections.sort(memz, new WeightComparator());
		RepastEdge link = memz.iterator().next();
		Meme meme = (Meme) link.getTarget(); // WARNING: Using 'target' on unoriented network
		if (belief.isAdjacent(this, meme)) {
			RepastEdge thisbelief = belief.getEdge(this, meme);
			thisbelief.setWeight(thisbelief.getWeight() + 1);
			
		} else {
			link.setWeight(1);
			belief.addEdge(this, meme, 1);
		}
		
		// if (ispublisher) {  	// This is no longer necessary.
		//	relink(meme);		// We now have memetic similarity in linkwithown()
		// }
	}
	
	public void corrupt(Network net, int max) {
		List<RepastEdge> alledges = (List<RepastEdge>) net.getEdges(this);
		Collections.sort(alledges, new InverseWeightComparator());
		if (net.numEdges() > max) {
			while (net.numEdges() > max) {
				RepastEdge link = alledges.iterator().next();
			//	System.out.println(link.getWeight()); // This is to TEST that it does what it does.
				net.removeEdge(link);
			}
		} else {
			RepastEdge link = alledges.iterator().next(); // Hopefully this kills only one edge. CHECK!!
			net.removeEdge(link);
		}
	}
	
	public void corrupt(ArrayList list, int max) {
		if (list.size() > max) {
			while (list.size()>max) {
				list.remove(list.iterator().next());
			}
		} else {
			if (list.size() > 2) {
				list.remove(0);
			}
		}
	}
	
	public void vote(Artifact arti, double probability) {
		if ((RandomHelper.nextDoubleFromTo(0, 1) < probability) && (!voted.contains(arti))) {
			arti.votes++;
			voted.add(arti);
		}
	}
	
	public void killoldlinks() {
		List<RepastEdge> allinks = (List) artifact.getEdges();
		Collections.sort(allinks, new InverseWeightComparator());
		RepastEdge moriturus = allinks.iterator().next();
		artifact.removeEdge(moriturus);
	}
}