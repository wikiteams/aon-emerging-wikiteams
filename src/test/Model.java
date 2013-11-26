package test;

public class Model {

	private String name;

	private int hashCode;

	public Model(String name) {
		this.name = name;
		hashCode = 37 * name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Model) {
			Model other = (Model) obj;
			return other.name.equals(this.name)
					&& (other.hashCode == this.hashCode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Model(" + name + ")";
	}

}