package org.logb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A pattern is an entity with place-holders in place of actual entities
 * somewhere down its sub-tree.
 * 
 * Patterns can be used to "match" an entity tree and get values for its
 * placeholders. Example with X as variable:
 * 
 * <pre><code>
 * EQUALS(X, X)
 * </code></pre>
 * 
 * This pattern matches:
 * <pre><code>
 * EQUALS(10, 10)
 * </code></pre>
 * 
 * With <code>`X := 10`</code>
 */
public class Pattern {
	/**
	 * Tries to add a variable of the given name, ensures the name is unique.
	 * 
	 * If the name is not unique, a different name will be selected for you. The new name
	 * can be extracted from the return value by calling `getName()`.
	 * 
	 * @param name The name to give the variable, null to have the name selected for you.
	 * @return The variable that was added.
	 */
	public Variable addVariable(String name) {
		boolean nameUnique = false;
		while(!nameUnique) {
			nameUnique = true;
			for(Variable var : variables) {
				if(var.getName() == name) {
					nameUnique = false;
				}
			}
			
			if(!nameUnique) {
				// Append _ until the name is unique
				name = name + "_";
			}
		}
		
		Variable rval = new Variable(name);
		variables.add(rval);
		return rval;
	}
	
	/**
	 * Get the root entity of this pattern.
	 */
	public EntityStructureBase getRoot() {
		return this.root;
	}
	
	/**
	 * Set the root of the pattern.
	 * 
	 * @param root An entity or variable, should not be an EntityStructure
	 */
	public void setRoot(EntityStructureBase root) {
		if(root instanceof EntityStructure) {
			throw new IllegalArgumentException("root must not be an EntityStructure");
		}
		
		this.root = root;
	}
	
	public Pattern deepcopy() {
		Pattern copy = new Pattern();
		copy.variables = this.variables;
		copy.root = this.root;
		return copy;
	}
	
	/**
	 * Substitutes occurrences of variables of name `X` with `<code>substitutions[X]</code>`.
	 * 
	 * @param substitutions A map from variable name to the entity that the variable should be substituted with.
	 * @return A new pattern with the given variables substituted.
	 */
	public Pattern substitute(Map<String, EntityStructureBase> substitutions) {
		Pattern copy = this.deepcopy();
		copy.setRoot(copy.root.substitute(substitutions));
		
		// TODO: remove substituted variables from copy.variables
		
		return copy;
	}
	
	/**
	 * Checks whether the given entity matches this pattern, and if so, returns the mapping
	 * of variables to entities that satisfies the match.
	 * 
	 * @param entityToMatch The entity that should be matched by this pattern.
	 * @return A map variable name -> entity that satisfies the match, or null if this pattern doesn't match it.
	 */
	public Map<String, EntityStructureBase> match(EntityStructureBase entityToMatch) {
		Map<String, EntityStructureBase> substitutions = new HashMap<String, EntityStructureBase>();
		if(root.match(entityToMatch, substitutions)) {
			return substitutions;
		} else {
			return null;
		}
	}
	
	private EntityStructureBase root = null;
	private List<Variable> variables = new ArrayList<Variable>();
	
	// TODO: constraints
}