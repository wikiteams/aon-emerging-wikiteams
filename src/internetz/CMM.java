package internetz;

public class CMM {

	public enum level {
		INITIAL, REPEATABLE, DEFINED, MANAGED, OPTIMIZING
	};

	private int stage;

	public CMM() {

	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public String toString() {
		switch (stage) {
		case 1: {
			return "Initial";
		}
		case 2: {
			return "Repeatable";
		}
		case 3: {
			return "Defined";
		}
		case 4: {
			return "Managed";
		}
		case 5: {
			return "Optimizing";
		}
		}
		return "ERR";
	}

}
