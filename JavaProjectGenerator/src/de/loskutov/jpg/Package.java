package de.loskutov.jpg;

public class Package {
	private String fqn;

	public Package(String name, Package parent){
		this.fqn = parent == null? name : parent.fqn + "." + name;
	}

	public String getFqn() {
		return fqn;
	}
}
