package org.logb.core;

public class Statement extends Entity {
	static public EntityType ENTITY_TYPE_STATEMENT;
	
	public static void initialize() {
		ENTITY_TYPE_STATEMENT = new EntityType("Statement", new Module("Core"), false, true);
	}
	
	public Statement(StatementType type) {
		this(type, null);
	}

	public Statement(StatementType type, EntityStructureBase arguments) {
		super(ENTITY_TYPE_STATEMENT);
		this.statementType = type;
		setStructure(arguments);
	}

	@Override
	public EntityStructureBase deepcopy() {
		EntityStructureBase structure = null;
		if(this.getStructure() != null) {
			structure = this.getStructure().deepcopy();
		}
		return new Statement(this.statementType, structure);
	}

	@Override
	public boolean equals(EntityStructureBase otherBase) {
		if (!(otherBase instanceof Statement)) {
			return false;
		}

		Statement other = (Statement) otherBase;
		return other.statementType.equals(this.statementType)
				&& other.getStructure().equals(this.getStructure());
	}
	
	@Override
	public String toString() {
		String rval = statementType.getName();
		if(getStructure() != null) {
			rval += " " + getStructure().toString();
		}
		return rval;
	}

	private final StatementType statementType;
}