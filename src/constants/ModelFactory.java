package constants;

/**
 * Tells whether we want to make model validations or just move to main
 * execution of simulation universe
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * 
 */
public class ModelFactory {

	public enum model {
		NORMAL, VALIDATION, NORMAL_AND_VALIDATION
	};

	private static final int DEFAULT = 0;
	private int functionality;

	public ModelFactory() {
		this(DEFAULT);
	}

	public ModelFactory(int model) {
		this.functionality = model;
	}

	public int getFunctionality() {
		return functionality;
	}

	public void setFunctionality(int functionality) {
		this.functionality = functionality;
	}

	@Override
	public String toString() {
		switch (functionality) {
		case 0: {
			return "Normal";
		}
		case 1: {
			return "Validation";
		}
		case 2: {
			return "Normal+Validation";
		}
		}
		return "ERR";
	}
}
