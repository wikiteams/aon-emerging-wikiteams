package internetz;

import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;


public class MemEdge extends RepastEdge {
	String edgeType = "";
	double weight = 0.0;

	public MemEdge(Object source, Object target, boolean directed, double weight, String type) {
		super(source, target, directed, weight);
		this.edgeType = type;
	}

	public MemEdge(Object source, Object target, boolean directed, double weight) {
		super(source, target, directed, weight);
	}
	
	public String getType() {
		return edgeType;
	}
	
	
	public void setType(String type) {
		this.edgeType = type;
	}
}
