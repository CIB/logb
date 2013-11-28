package org.logb;

public class StatementType {
	public StatementType(String name, Module mod) {
		this.name = name;
		this.mod = mod;
		mod.addStatementType(this);
	}

	private final String name;
	private final Module mod;
	
	public String getName() {
		return this.name;
	}
}