package internetz;

import java.util.Comparator;

// CHECK whether we can do this with a different approach, given that the values are int.
public class PopularityComparator implements Comparator<Artifact> {
	public int compare(Artifact artifact1, Artifact artifact2) {
		if (artifact1.getViews() > artifact2.getViews()) return -1;
	    if (artifact1.getViews() < artifact2.getViews()) return 1;
	    return 0;
	}
}
