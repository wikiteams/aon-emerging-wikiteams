package constants;

import test.Model;

/**
 * Tells whether we want to make model validations or just move to main
 * execution of simulation universe
 * 
 * @author Oskar Jarczyk
 * @since 1.3
 * 
 */
public class ModelFactory {

	public enum ModelEnum {
		NORMAL, VALIDATION, NORMAL_AND_VALIDATION
	};

	private static final ModelEnum DEFAULT = ModelEnum.NORMAL;
	private ModelEnum functionality;

	public ModelFactory() {
		this(DEFAULT);
	}

	public ModelFactory(ModelEnum model) {
		this.functionality = model;
	}

	public ModelFactory(Model model) {
		String name = model.getName().toLowerCase();
		if (name.equals("normal")) {
			this.functionality = ModelEnum.NORMAL;
		} else if (name.equals("validation")) {
			this.functionality = ModelEnum.VALIDATION;
		} else if (name.equals("normal+validation")) {
			this.functionality = ModelEnum.NORMAL_AND_VALIDATION;
		}
	}

	public ModelEnum getFunctionality() {
		return functionality;
	}

	public void setFunctionality(ModelEnum functionality) {
		this.functionality = functionality;
	}

	@Override
	public String toString() {
		switch (functionality) {
		case NORMAL: {
			return "Normal";
		}
		case VALIDATION: {
			return "Validation";
		}
		case NORMAL_AND_VALIDATION: {
			return "Normal+Validation";
		}
		}
		return "ERR";
	}
}
