package internetz;

import java.util.Comparator;

import repast.simphony.space.graph.RepastEdge;

public class InverseWeightComparator implements Comparator<RepastEdge> {
	public int compare(RepastEdge edge1, RepastEdge edge2) {
		if (edge1.getWeight() > edge2.getWeight()) return -1;
	    if (edge1.getWeight() < edge2.getWeight()) return 1;
	    return 0;
	}
}