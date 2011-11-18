package internetz;

import java.util.Comparator;

public class PageRankComparator implements Comparator<Artifact> {
	public int compare(Artifact artifact1, Artifact artifact2) {
        if (artifact1.getRank() > artifact2.getRank()) return 1;
        if (artifact1.getRank() < artifact2.getRank()) return -1;
        return 0;
    }


}
