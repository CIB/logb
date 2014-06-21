package org.logb.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	
	/**
	 * Create a deep copy of this pattern, ensuring that modifying
	 * the copy will have no effect on the original.
	 * 
	 * @return The deep copy of this pattern.
	 */
	public Pattern deepcopy() {
		Pattern copy = new Pattern();
		copy.variables = new ArrayList<>(this.variables);
		copy.root = this.root.deepcopy();
		return copy;
	}
	
	/**
	 * Substitutes occurrences of variables of name `X` with `<code>substitutions[X]</code>`.
	 * 
	 * @param substitutions A map from variable name to the entity that the variable should be substituted with.
	 * @return A new pattern with the given variables substituted.
	 */
	public Pattern substitute(Map<String, EntityStructureBase> substitutions) {
		// Recursively substitute occurrences of variables with the provided substitutions.
		Pattern copy = this.deepcopy();
		copy.setRoot(copy.root.substitute(substitutions));
		
		// Remove all substituted variables from this pattern's variables.
		for(String variableName : substitutions.keySet()) {
			Iterator<Variable> variablesIterator = variables.iterator();
			while(variablesIterator.hasNext()) {
				Variable next = variablesIterator.next();
				if(next.getName() == variableName) {
					variablesIterator.remove();
				}
			}
		}
		
		return copy;
	}
	
	/**
	 * The result of a two-way watch between two entity structures.
	 */
	public class MatchResult {
		/** Matches from variables of the left-hand entity to values
		 *  of the right-hand entity.
		 */
		public Map<String, EntityStructureBase> leftToRightMatches = new HashMap<>();
		
		/** Matches from variables of the right-hand entity to values
		 *  of the left-hand entity.
		 */
		public Map<String, EntityStructureBase> rightToLeftMatches = new HashMap<>();
	}
	
	/**
	 * Checks whether the given entity matches this pattern, and if so, returns the mapping
	 * of variables to entities that satisfies the match.
	 * 
	 * @param entityToMatch The entity that should be matched by this pattern.
	 * @return A map variable name -> entity that satisfies the match, or null if this pattern doesn't match it.
	 */
	public MatchResult match(EntityStructureBase entityToMatch) {
		MatchResult matchResult = new MatchResult();
		
		// We first check whether the variables in the left-hand entity can be matched against something consistent
		// in the right-hand entity. Then we check the same from right to left.
		//
		// Example:
		// match A(%X, 2), A(10, %Y)
		//
		// First we match from left to right, which yields { %X = 10 }, 
		//  we store this result in matchResult.leftToRightMatches
		//
		// Then we match from right to left, which yields  { %Y =  2 },
		//  we store this result in matchResult.rightToLeftMatches
		if(
				root.match(entityToMatch, matchResult.leftToRightMatches) && 
				entityToMatch.match(root, matchResult.rightToLeftMatches)
		  ) {
			return matchResult;
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		String rval = "Pattern(";
		for(int i=0; i < variables.size(); i++) {
			Variable v = variables.get(i);
			rval += v.getName();
			if(i + 1 < variables.size()) {
				rval += ", ";
			}
		}
		rval += ") ";
		rval += root.toString();
		return rval;
	}
	
	private EntityStructureBase root = null;
	private List<Variable> variables = new ArrayList<Variable>();
	
	// TODO: constraints
}