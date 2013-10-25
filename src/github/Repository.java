package github;

public class Repository {
	
	private String name;
	private String cluster;

	public Repository(String name, String cluster) {
		this.name = name;
		this.cluster = cluster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return name.hashCode() * cluster.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.name == ((Repository) obj).name)
				&& (this.cluster.equals((((Repository) obj).cluster))))
			return true;
		else
			return false;
	}

}
