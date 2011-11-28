package internetz;

import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.graph.Network;
import repast.simphony.random.RandomHelper;


public class memEdges implements EdgeCreator { 	
	
	public MemEdge createEdge(Object source, Object target,
			boolean isDirected, double weight, double age) {
		return new MemEdge(source, target, isDirected, weight, age); 
	}
	

	@Override
	public Class getEdgeType() {
		return MemEdge.class;
	}
	

	@Override
	public RepastEdge createEdge(Object source, Object target,
			boolean isDirected, double weight) {
		return new MemEdge(source, target, isDirected, weight);
	}
		  
}
