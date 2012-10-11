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

	Parameters param = RunEnvironment.getInstance().getParameters();
	String algo = (String)param.getValue("filteringalgo");
	int ownlinks = (Integer)param.getValue("linkswithown");
	int maxBeliefs = (Integer)param.getValue("maxbelief");
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
	Network<Artifact> artifact;
	Network<Object> belief;
	Network<Agent> sns;

	boolean isPublisher;
	private int readingCapacity;
	//private int artifactsShared;
	private int reads;
	
	private ArrayList<Artifact> bookmarks = new ArrayList();
	private ArrayList<Artifact> creatures = new ArrayList();
	private ArrayList<Artifact> voted = new ArrayList();
	private ArrayList<Artifact> shared = new ArrayList();
	private int name;


	public Agent() {
		this.name = ++totalAgents;
		// this.readingCapacity = readingCapacity;
		// this.isPublisher = isPublisher;
		this.bookmarks = bookmarks;
		this.creatures = creatures;
		this.maxBeliefs = maxBeliefs;
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
	
	public void setID(int i) {
		this.name = i;
	}
	
	public int getID() {
		return this.name;
	}
		

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
		
		Context context = (Context)ContextUtils.getContext(this);
		belief = (Network)context.getProjection("beliefs");
		memory = (Network)context.getProjection("memorys");
		artimeme = (Network)context.getProjection("artimemes");
		artifact = (Network<Artifact>)context.getProjection("artifacts");
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

	public void explore() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		int howmany = RandomHelper.nextIntFromTo(0, this.readingCapacity);
		if (algo.equals("random")) {
			Iterable localarts = context.getRandomObjects(Artifact.class, howmany);
			while (localarts.iterator().hasNext()) {
				Artifact localart = (Artifact)localarts.iterator().next();
				localart.addView();
				if (!localart.author.equals(this)&&!bookmarks.contains(localart)) {
					read(localart,null);
					bookmarks.add(localart);
				}
			}
		} else {
			if (algo.equals("flaneur")) {
				if (!bookmarks.isEmpty()) exploreByLinks(howmany+1, bookmarks);
				else {
					ArrayList allarts = getTransformedIteratorToArrayList((context.getObjects(Artifact.class)).iterator());
					if (allarts.size() > 0) exploreByLinks(howmany, allarts);
				}
			} else explorebymemes();
		}
	}
	
	public ArrayList<Artifact> getShared() {
		ArrayList<Artifact> newShares = new ArrayList<Artifact>();
		for (Artifact a : this.shared) {
			if (a.getAge()<=20) newShares.add(a);
		}
		return newShares;
	}


	public void exploreByLinks(int capacity, ArrayList startingset) {
		int reads = 0;
		//int frustration = 0;
		int whichone = 0;
		Artifact nowreading = null;
		int size = startingset.size();
		if (startingset.size()>0) {
			//System.out.println("OK!");
			//int size = startingset.size();
			whichone = 0;
			nowreading = (Artifact) startingset.get(whichone);
			//if (size<capacity) capacity=size;
			while (reads < capacity) {
				nowreading.addView();  // the artifact gets a page view
				if (!nowreading.author.equals(this)&&!bookmarks.contains(nowreading)) {
					read(nowreading,null);
					//System.out.println("Yeah, I read");
					reads++;
					bookmarks.add(nowreading);
					if (artifact.getOutDegree(nowreading)>0) nowreading = (Artifact) artifact.getRandomSuccessor(nowreading);
					else {
						whichone++;
						if (whichone==size) break;
						nowreading = (Artifact) startingset.get(whichone);
					}
				} else {
				 	if (artifact.getOutDegree(nowreading)>1) {
						nowreading = nowreading = (Artifact) artifact.getRandomSuccessor(nowreading);
						// System.out.println("There are links. I follow....");
						//System.out.println("Follow a link");
					} else {
						whichone++;
						if (whichone==size) break;
						nowreading = (Artifact) startingset.get(whichone);
						// System.out.println(nowreading);
						//frustration++;
						//System.out.println("Random artifact");
					}
				}
			}
		} //else System.out.println("Oh Fuck!!");
	}
	/*
	public boolean checkReads() {
		// Not good. readingCapacity is a upper boudary itself!!!
		double toleranceUp = this.readingCapacity*+0.20;
		double toleranceDown = this.readingCapacity*-0.20;
		if (this.reads>=toleranceDown&&this.reads<=toleranceUp) {
			this.reads = 0;
			return true;
		}
		this.reads = 0;
		return false;
	}
*/
	public void explorebymemes() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		Meme currentmeme = (Meme) belief.getRandomAdjacent(this);
		int oppo = 4999 - currentmeme.getID();
		//System.out.println("Opposite  meme id: " + oppo);
		Hashtable allMemes = ((InternetzCtx)context).getMemez();
		Meme oppositememe = (Meme) allMemes.get(oppo);
		// Iterator mybeliefs = belief.getAdjacent(this).iterator(); 
		// double time = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		ArrayList<Artifact> all = new ArrayList<Artifact>();
		ArrayList<Artifact> all2 = new ArrayList<Artifact>();
		int howmany = RandomHelper.nextIntFromTo(1, readingCapacity);
		all = getTransformedIteratorToArrayList(artimeme.getAdjacent(currentmeme).iterator());
		all2 = getTransformedIteratorToArrayList(artimeme.getAdjacent(oppositememe).iterator());
		Collections.sort(all, new PageRankComparator());
		Collections.sort(all2, new PageRankComparator());
		if (algo.equals("searcher")) {
		
			if (!all2.isEmpty()) {
				suck(howmany/2,all);
				suck(howmany/2,all2);
			} else suck(howmany,all);
		}		
		/*if (algo.equals("flaneur")) {
			ArrayList fava = new ArrayList();
			if (this.bookmarks.size()>1) {
				fava.addAll(this.bookmarks);
			} else {
				fava.addAll(all);
				fava.addAll(all2);
			}
			exploreByLinks(howmany,fava);
		}*/

		if (algo.equals("web2")) {
			// Uncomment the following if you want reddit to feed the most voted artifacts in (i.e. reddit frontpage)
			// Otherwise the agent will read most voted artifacts only relative to his meme of interest (i.e. a subreddit)
			all = new ArrayList();
			all = getTransformedIteratorToArrayList(context.getObjects(Artifact.class).iterator());
			Collections.sort(all, new VoteComparator());
			suck(howmany,all);
		}
		
		if (algo.equals("social")) {
			if (sns.getOutDegree(this)>0) exploreSocial(howmany);
			else {
				suck(howmany/2,all);
				suck(howmany/2,all2);
			}
		}
	}
	
	
	
	public void exploreSocial(int howmany) {
			int reads = 0;
			//ArrayList<Artifact> toRead = new ArrayList<Artifact>();
			Iterator<Agent> following = sns.getSuccessors(this).iterator();
			while (following.hasNext()) {
				Agent a = following.next();
				for (Artifact s : a.getShared()) {
					if (!this.bookmarks.contains(s)&&s.getAuthor()!=this){ 
						read(s,a);
						reads++;
						if (reads>=howmany) break;
					}
				}
				if (reads>=howmany) break;
			}
	}

	public void suck(int capacity, ArrayList startingset) {
		int i=0; // read artifacts
		int a=0; // artifacts not read because unsuitable
		int size = startingset.size();
		if (size < capacity) capacity = size;
		while (i < capacity) {
			// It need not be a creature of the reader nor recently bookmarked
			Artifact arti = (Artifact) startingset.get(a);
			if (!arti.author.equals(this)&&!bookmarks.contains(arti)) {
				read(arti,null);
				arti.addView();  // the artifact gets a page view
				bookmarks.add(arti);
				i++;
				a++;
			} else a++;
			if (a==size) break;   // questa non ve la spiego
		}
	}

	public void read (Artifact arti, Agent sharer) {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		Hashtable allMemes = ((InternetzCtx)context).getMemez();
		this.reads++;
		double sticksInMem = (Double) param.getValue("sticksInMem");
		Iterator<Meme> memez = arti.getMemes();
		int artiTotalMemes = arti.totalMemesInvested();
		boolean known = false;
		int howsimilar = 0;
		int hate = 0;
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();
			Meme oppositememe = (Meme) allMemes.get(4999-thismeme.getID());
			if (belief.isAdjacent(this, thismeme)) {
				known = true;
				howsimilar++;
			}
			if (belief.isAdjacent(this, oppositememe)) {
				known = true;
				hate++;
			}
		}
		if (known) {
			Boolean positive = true;
			double prob;
			sticksInMem+=learnIncreaseIfKnown;
			if (hate>=howsimilar) {
				positive=false;
				prob = ((hate/artiTotalMemes)+0.05);
				if (sharer!=null) decreaseSocial(sharer);
			} else {
				prob = (howsimilar/artiTotalMemes); 
			}
			voteAndLink(arti,prob,sharer,positive);
		} else {
			if (sharer!=null) decreaseSocial(sharer); 
		}
		memez = arti.getMemes();
		while (memez.hasNext()) {
			Meme thismeme = (Meme) memez.next();
			if (belief.isAdjacent(this, thismeme)) {
				RepastEdge lnk = belief.getEdge(this, thismeme);
				double wght = lnk.getWeight();
				if (wght+weightIncrease>1) lnk.setWeight(1);
				else lnk.setWeight(wght+weightIncrease);
			}
			if (memory.isAdjacent(this, thismeme)) {
				RepastEdge lnk = memory.getEdge(this, thismeme);
				double wght = lnk.getWeight();
				lnk.setWeight(wght+weightIncrease);
				//System.out.println("I'm adding to existing memory");
				if (lnk.getWeight()>=1) {
					if (!belief.isAdjacent(this,thismeme )&&thismeme.isSuitable(this)) {
						lnk.setWeight(memeWeightInitial);
						belief.addEdge(this,thismeme,memeWeightInitial);

						//System.out.println("I'm adding a new belief");
					} else {
						//System.out.println("I'm adding to an existing belief + memory");
						if (thismeme.isSuitable(this)) {
							lnk.setWeight(memeWeightInitial);
							double wght2 = belief.getEdge(this, thismeme).getWeight();
							belief.getEdge(this, thismeme).setWeight(wght2+0.2);
						}
					}
				}
			}
			else {
				if (RandomHelper.nextDoubleFromTo(0, 1)<sticksInMem) memory.addEdge(this, thismeme, memeWeightInitial);
				//System.out.println("I'm adding a new memory");
			}
		}
	}
	

	public void publish() {
		Context<Object> context = (Context)ContextUtils.getContext(this);	
		belief = (Network)context.getProjection("beliefs");
		artimeme = (Network)context.getProjection("artimemes");
		double initialPRank = 0.1;
		if (context.getObjects(Artifact.class).size()>1) initialPRank = 1/context.getObjects(Artifact.class).size();
		Artifact newArt = new Artifact(this, initialPRank);
		newArt.views = 0;
		newArt.votes = 0;
		newArt.shares = 0;
		newArt.birthday = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		newArt.name = context.getObjects(Artifact.class).size() + 1;
		// // System.out.println("Just created the artifact #: " + newArt.id);
		context.add(newArt);
		creatures.add(newArt);
		shared.add(newArt);
		//this.artifactsShared++;
		// System.out.println("We have " + creatures.size() + " creatures");

		// WARNINGWARNING: magic number to be replaced here
		int mymemes = (int) ((belief.getDegree(this)*RandomHelper.nextDoubleFromTo(0, 0.20))+1);
		int howmanymemes = RandomHelper.nextIntFromTo(1, mymemes);
		int i = 0;
		while (i<howmanymemes) {
			Meme investingmeme = (Meme) belief.getRandomAdjacent(this);
			//Meme investingmeme2 = (Meme) belief.getRandomAdjacent(this);
			if (belief.getEdge(this, investingmeme).getWeight() > 0) {
				artimeme.addEdge(investingmeme, newArt);
				i++;
			}
			// System.out.println("I have just put " + howmanymemes +" memes in the artifact");
		}

		// MAGIC NUMBER HERE!
		if (this.creatures.size() > 5) linkWithOwn(newArt); 
		if (!this.bookmarks.isEmpty()) link(newArt);
		else linkOnce(newArt);
	}

	// The first time we link with a random artifact (if there is one)
	public void linkOnce(Artifact newart) {
		Context<Object> context = (Context)ContextUtils.getContext(this);
		int howmany = context.getObjects(Artifact.class).size();
		if (howmany > 0) {
			Artifact arti = (Artifact) context.getRandomObjects(Artifact.class, 1).iterator().next();
			if (!arti.equals(newart)) {
				newart.buildLink(arti);
				//System.out.println("Building a randomlink");
				read(arti,null);
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

	// Memetic similarity extractor 
	public ArrayList<Artifact> getMostSimilar(ArrayList<Artifact> list, Artifact source) {
		Context<Object> context = (Context)ContextUtils.getContext(this);
		artimeme = (Network)context.getProjection("artimemes");
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
		//System.out.println("Most similars are: "+mostSimilarArtifacts.size());
		return mostSimilarArtifacts;
	}

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
		if (size>maxArtifactsToLinkTo) size = maxArtifactsToLinkTo;
		// System.out.println("I have to link the artifact with " + size + " others");
		for (int i=0;i<size;i++) {
			Artifact arti = (Artifact) mostsimilar.get(i);
			newart.buildLink(arti);
			// System.out.println("I have linked the artifact with a bookmark");
		}
	}

	@ScheduledMethod(start = 5, interval = 5)
	public void corrupt() {
		int size = this.bookmarks.size();
		int maxBlf = this.maxBeliefs;
		int howManyDeaths = 0;
		if (size>maxBlf) {
			howManyDeaths = size-maxBlf;
			this.bookmarks.subList(0, howManyDeaths).clear();
		}
	}
	

	public void voteAndLink(Artifact arti, double probability, Agent sharer, Boolean positive) {
		double recipro = (Double) param.getValue("avgReciprocating");
		Agent whomToLink = sharer;
		Context context = (Context)ContextUtils.getContext(this);
		sns = (Network) context.getProjection("twitter");
		if (sharer == null) whomToLink = arti.getAuthor();
		if ((RandomHelper.nextDoubleFromTo(0, 1) < probability)) {
			if (!algo.equals("social")){
				if (!this.voted.contains(arti)) {
					this.voted.add(arti);
					if (positive) arti.addVote();
					else arti.subtractVote();
				}
			} else {  /// ERROR HERE!!
				if (!sns.isPredecessor(this, whomToLink)) {
					if (sns.getOutDegree(this)<maxFollowing) {
						sns.addEdge(this, whomToLink, weightIncrease);
						//System.out.println("Got a new friend");
						this.shared.add(arti);
						arti.addShare();
						//this.artifactsShared++;
						if (RandomHelper.nextDoubleFromTo(0, 1)<=(recipro)) {
							sns.addEdge(whomToLink, this, weightIncrease);
							//System.out.println("Got a new friend");

						}

					}
				} else {
					double wght = sns.getEdge(this, whomToLink).getWeight();
					sns.getEdge(this, whomToLink).setWeight(wght+weightIncrease);
					//System.out.println("i'm INcreasing my friendship");
					this.shared.add(arti);
					arti.addShare();
					//this.artifactsShared++;

				}
			}
		} /* else {
			if (algo.equals("social")) {
				decreaseSocial(whomToLink);
			}
		}*/
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
		Context context = (Context)ContextUtils.getContext(this);
		Network belief = (Network) context.getProjection("beliefs");
		double E=0;
		double I=0;
		double total = belief.getDegree(this);
		Iterator<Object> myBeliefs = belief.getAdjacent(this).iterator();
		while (myBeliefs.hasNext()) {
			Meme thisMeme = (Meme) myBeliefs.next();
			if (thisMeme.getGrp()!=this.getGroup()) E++;
			else I++;
		}
		return (E-I)/total; 
	}
	
	
	/*
	 * This Should be turned on only in the social case. 
	 * We explore the people we follow and read something shared by their friends.
	 */
	@ScheduledMethod(start = 4, interval = 5)
	public void trackFriends() {
		Context context = (Context)ContextUtils.getContext(this);
		sns = (Network)context.getProjection("twitter");
		if (sns.getDegree(this)>1) {
			Agent sharer = sns.getRandomAdjacent(this);
			int exploreHowMany = 3;
			for (int i=0; i<exploreHowMany;i++) {
				if (sns.getOutDegree(sharer)>0) {
					Agent friend = sns.getRandomSuccessor(sharer);
					if(friend.shared.size()>0) {
						int siz = friend.shared.size();
						Artifact toRead = friend.shared.get(siz-1); 
						read(toRead,friend);
					}
				}
			}
		}
	}
	
	public double getReads() {
		return this.reads/endTick;
	}
}