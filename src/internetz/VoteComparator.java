package internetz;

import java.util.Comparator;

public class VoteComparator implements Comparator<Artifact> {
	public int compare(Artifact artifact1, Artifact artifact2) {
		if (artifact1.getVotes() > artifact2.getVotes()) return -1;
	    if (artifact1.getVotes() < artifact2.getVotes()) return 1;
	    return 0;
	}

}
