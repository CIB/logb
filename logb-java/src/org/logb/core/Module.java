package org.logb.core;

import java.util.ArrayList;
import java.util.List;

// TODO: generators that can reply to queries in custom ways, rather than doing standard logic rule stuff, e.g. query X + 6 = 10, get X=4
public class Module {
	public static Module MODULE_BASE;

	public Module(String name) {
		this.name = name;
	}

	public void addEntityType(EntityType type) {
		this.entityTypes.add(type);
	}

	public void addStatementType(StatementType type) {
		this.statementTypes.add(type);
	}

	private final String name;
	private final List<EntityType> entityTypes = new ArrayList<EntityType>();
	private final List<StatementType> statementTypes = new ArrayList<StatementType>();
}
