package org.logb;

public class Statement extends Entity {
	static public EntityType ENTITY_TYPE_STATEMENT;
	
	public static void initialize() {
		ENTITY_TYPE_STATEMENT = new EntityType("Statement", new Module("Core"), false, true);
	}

	public Statement(StatementType type, EntityStructureBase arguments) {
		super(ENTITY_TYPE_STATEMENT);
		this.statementType = type;
		this.setStructure(arguments);
	}

	@Override
	public EntityStructureBase deepcopy() {
		return new Statement(this.statementType, this.getStructure().deepcopy());
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
		rval += " " + getStructure().toString();
		return rval;
	}

	private final StatementType statementType;
}