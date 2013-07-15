package internetz;

import java.util.Comparator;

public class PageRankComparator implements Comparator<Skill> {
	public int compare(Skill skill1, Skill skill2) {
        if (skill1.getRank() > skill2.getRank()) return -1;
        if (skill1.getRank() < skill2.getRank()) return 1;
        return 0;
    }


}
