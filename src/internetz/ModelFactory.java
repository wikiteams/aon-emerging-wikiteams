package internetz;

public class ModelFactory {

	public enum model {
		BASIC, EXTENDED, EXTRA_EXTENDED
	};
	
	private static final int DEFAULT = 0;

	private int complexity;

	public ModelFactory() {
		this(DEFAULT);
	}
	
	public ModelFactory(int model) {
		this.complexity = model;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public String toString() {
		switch (complexity) {
		case 0: {
			return "Basic";
		}
		case 1: {
			return "Extended";
		}
		case 2: {
			return "Extra extended";
		}
		}
		return "ERR";
	}
}
