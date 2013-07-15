package internetz;

public class ModelFactory {

	public enum model {
		BASIC, EXTENDED
	};

	private int complexity;

	public ModelFactory() {

	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public String toString() {
		switch (complexity) {
		case 1: {
			return "Basic";
		}
		case 2: {
			return "Extended";
		}
		}
		return "ERR";
	}
}
