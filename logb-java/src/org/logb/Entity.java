package org.logb;

import java.util.Map;

/**
 * The base class to represent any kind of `thing` we want to model.
 * 
 * Entities are `instances` of their corresponding EntityType. The EntityType
 * holds more information, such as whether an entity has a structure or an
 * external pointer. The structure/pointer itself is then defined in each
 * Entity instance.
 */
public class Entity implements EntityStructureBase {
	/**
	 * @param type The EntityType that this object instanciates.
	 */
	public Entity(EntityType type) {
		this.type = type;
	}

	public Object getPointer() {
		assert (type.hasPointer());
		return pointer;
	}

	public void setPointer(Object pointer) {
		assert (type.hasPointer());
		this.pointer = pointer;
	}

	public EntityStructureBase getStructure() {
		assert (type.hasStructure());
		return structure;
	}

	public void setStructure(EntityStructureBase structure) {
		assert (type.hasStructure());
		this.structure = structure;
	}

	@Override
	public EntityStructureBase deepcopy() {
		Entity copy = new Entity(this.type);
		copy.setPointer(this.pointer);
		copy.setStructure(this.structure.deepcopy());
		return copy;
	}

	@Override
	public boolean equals(EntityStructureBase otherBase) {
		if (!(otherBase instanceof Entity)) {
			return false;
		}

		Entity other = (Entity) otherBase;

		if (this.type != other.type) {
			return false;
		}

		if (type.hasStructure() && !structure.equals(other.structure)) {
			return false;
		}

		if (type.hasPointer() && this.pointer != other.pointer) {
			return false;
		}

		return true;
	}
	
	@Override
	public EntityStructureBase substitute(
			Map<String, EntityStructureBase> substitutions) {
		Entity copy = new Entity(this.type);
		copy.setPointer(this.pointer);
		copy.setStructure(this.structure.substitute(substitutions));
		return copy;
	}
	
	@Override
	public boolean match(EntityStructureBase entityToMatchBase,
			Map<String, EntityStructureBase> substitutions) {
		if (!(entityToMatchBase instanceof Entity)) {
			return false;
		}

		Entity entityToMatch = (Entity) entityToMatchBase;

		if (this.type != entityToMatch.type) {
			return false;
		}

		if (type.hasStructure() && !structure.match(entityToMatch.structure, substitutions)) {
			return false;
		}

		if (type.hasPointer() && this.pointer != entityToMatch.pointer) {
			return false;
		}

		return true;
	}

	private EntityType type;
	private Object pointer;
	private EntityStructureBase structure;
}