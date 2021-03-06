package org.logb.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Maps strings to EntityStructureBase objects, used to create entity structure
 * trees.
 * 
 * See <code>EntityStructureBase</code> for more details.
 */
public class EntityStructure extends HashMap<String, EntityStructureBase>
		implements EntityStructureBase {
	private static final long serialVersionUID = 1L;

	@Override
	public EntityStructureBase deepcopy() {
		EntityStructure copy = new EntityStructure();
		for (String key : this.keySet()) {
			EntityStructureBase value = this.get(key).deepcopy();
			copy.put(key, value);
		}
		return copy;
	}

	@Override
	public boolean equals(EntityStructureBase otherBase) {
		if(!(otherBase instanceof EntityStructure)) {
			return false;
		}
		
		EntityStructure other = (EntityStructure) otherBase;
		
		if (this.keySet().size() != other.keySet().size()) {
			return false;
		}
		
		for (String key : this.keySet()) {
			EntityStructureBase value = this.get(key).deepcopy();
			if(!other.containsKey(key) || !other.get(key).equals(this.get(key))) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public EntityStructureBase substitute(
			Map<String, EntityStructureBase> substitutions) {
		
		EntityStructure copy = new EntityStructure();
		for(String key : this.keySet()) {
			EntityStructureBase value = this.get(key);
			copy.put(key, value.substitute(substitutions));
		}
		return copy;
	}

	@Override
	public boolean match(EntityStructureBase entityToMatchBase,
			Map<String, EntityStructureBase> substitutions) {
		
		// at the moment it's not possible to have variables represent the whole
		// "argument list" of an entity, e.g. `foo(%X)` where `%X` could be `A=1, B=2`
		if(!(entityToMatchBase instanceof EntityStructure)) {
			return false;
		}
		
		EntityStructure entityToMatch = (EntityStructure) entityToMatchBase;
		
		if (this.keySet().size() != entityToMatch.keySet().size()) {
			return false;
		}
		
		for (String key : this.keySet()) {
			if(!entityToMatch.containsKey(key) || !this.get(key).match(entityToMatch.get(key), substitutions)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		if(this.size() == 0) {
			return "";
		}
		
		String rval = "(";
		Iterator<String> iter = this.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			EntityStructureBase child = this.get(key);
			rval += key + " = " + child.toString();
			
			if(iter.hasNext()) {
				rval += ", ";
			}
		}
		rval += ")";
		return rval;
	}
}
