package internetz;

import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;


public class MemEdge extends RepastEdge {
	double edgeAge = 0;

	public MemEdge(Object source, Object target, boolean directed, double weight, double age) {
		super(source, target, directed, weight);
		this.edgeAge = age;
	}

	public MemEdge(Object source, Object target, boolean directed, double weight) {
		super(source, target, directed, weight);
	}
	
	public double getAge() {
		return edgeAge;
	}
	
	
	public void setAge(int age) {
		this.edgeAge = age;
	}
}
