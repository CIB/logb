package org.logb;

/**
 * Represents a `class` of entities.
 * 
 * Can be used to model many things, such as `Integer`, `PlusOperator`,
 * `AssemblerInstruction`, `CodeState` and so on.
 */
public class EntityType {
	// TODO: better checking for instances of entity types, e.g. constraints on
	// `this.structure`.

	/**
	 * Create a new entity type with the given name, and put it in the given module.
	 * 
	 * @param name The name of the entity type. Should be unique within its module.
	 * @param mod The module to put the entity type in.
	 */
	EntityType(String name, Module mod, boolean hasPointer, boolean hasStructure) {
		this.name = name;
		this.mod = mod;
		this.hasPointer = hasPointer;
		this.hasStructure = hasStructure;
		mod.addEntityType(this);
	}

	/**
	 * Check whether entities of this type have opaque pointers to some value.
	 */
	public boolean hasPointer() {
		return hasPointer;
	}

	/**
	 * Check whether entities of this type have a tree-like structure.
	 */
	public boolean hasStructure() {
		return hasStructure;
	}

	/**
	 * Get the name of this entity type.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the module this entity type has been placed in.
	 */
	public Module getMod() {
		return mod;
	}

	final private String name;
	final private Module mod;

	final private boolean hasPointer;
	final private boolean hasStructure;
}