package internetz;

import java.util.Comparator;

// CHECK whether we can do this with a different approach, given that the values are int.
public class PopularityComparator implements Comparator<Skill> {
	public int compare(Skill skill1, Skill skill2) {
		if (skill1.getViews() > skill2.getViews()) return -1;
	    if (skill1.getViews() < skill2.getViews()) return 1;
	    return 0;
	}
}
