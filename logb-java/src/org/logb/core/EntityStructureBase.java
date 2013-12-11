package org.logb.core;

import java.util.Map;

/**
 * Base class for nodes of an entity structure tree.
 * 
 * An entity tree consists of intermixed entities and string -> value maps, e.g.
 * The following example represents <code>`3 + 5`</code> written in entity
 * format.
 * 
 * <pre>
 * PlusExpression:
 *   "lefthand"  : Integer 3
 *   "righthand" : Integer 5
 * </pre>
 * 
 * Here PlusExpression is an Entity, with a key-value map, mapping "lefthand"
 * and "righthand" to Integer entities.
 */
public interface EntityStructureBase {
	/**
	 * Returns a full copy of this structure tree node and its sub-tree.
	 */
	public EntityStructureBase deepcopy();

	/**
	 * Check whether this structure tree node is equivalent to another structure
	 * tree node.
	 * 
	 * @param other
	 *            The structure tree node to compare to.
	 */
	public boolean equals(EntityStructureBase other);
	
	/**
	 * Substitute all occurrences of the variable with the given name with the associated entity.
	 * 
	 * @param substitutions A map from variable names to entities to replace them with.
	 * @return A copy of this entity with the substitutions made.
	 */
	public EntityStructureBase substitute(Map<String,EntityStructureBase> substitutions);
	
	/**
	 * Treat this entity structure as a pattern, and try to match it against `entityToMatch`. Store the necessary
	 * substitutions in the `substitutions` parameter.
	 * 
	 * @param entityToMatch The entity to match against.
	 * @param substitutions The substitutions, this function can add new substitutions and must ensure there are no conflicts.
	 * @return true if match successful, false if unsuccessful.
	 */
	public boolean match(EntityStructureBase entityToMatch, Map<String,EntityStructureBase> substitutions);
}
