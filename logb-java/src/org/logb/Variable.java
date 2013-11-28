package org.logb;

import java.util.Map;

/**
 * A placeholder for some other EntityStructureBase, with a name that should be unique in
 * the entity structure tree it's in.
 */
public class Variable implements EntityStructureBase {
	public Variable(String name) {
		this.name = name;
	}
	
	@Override
	public EntityStructureBase deepcopy() {
		return new Variable(this.name);
	}

	@Override
	public boolean equals(EntityStructureBase otherBase) {
		if(!(otherBase instanceof Variable)) {
			return false;
		}
		
		Variable other = (Variable) otherBase;
		
		return other.name == this.name;
	}

	public String getName() {
		return name;
	}
	
	private final String name;

	@Override
	public EntityStructureBase substitute(
			Map<String, EntityStructureBase> substitutions) {
		if(substitutions.containsKey(this.name)) {
			return substitutions.get(this.name);
		} else {
			return this.deepcopy();
		}
	}

	@Override
	public boolean match(EntityStructureBase entityToMatch,
			Map<String, EntityStructureBase> substitutions) {
		if(substitutions.containsKey(this.name) && !substitutions.get(this.name).equals(entityToMatch)) {
			return false;
		} else {
			substitutions.put(this.name, entityToMatch);
			return true;
		}
	}
	
	@Override public String toString() {
		return getName();
	}
}
