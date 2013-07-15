package internetz;

import java.util.Comparator;

public class VoteComparator implements Comparator<Skill> {
	public int compare(Skill artifact1, Skill artifact2) {
		if (artifact1.getVotes() > artifact2.getVotes()) return -1;
	    if (artifact1.getVotes() < artifact2.getVotes()) return 1;
	    return 0;
	}

}
