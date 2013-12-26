package org.logb.lang;

import grammar.EntityGrammarBaseVisitor;
import grammar.EntityGrammarParser;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.logb.core.Entity;
import org.logb.core.EntityStructure;
import org.logb.core.EntityStructureBase;
import org.logb.core.EntityType;
import org.logb.core.Statement;
import org.logb.core.StatementType;

public class EntityParser {
	
	private List<EntityType> entityTypes;
	private List<StatementType> statementTypes;
	
	public EntityParser(List<EntityType> entityTypes, List<StatementType> statementTypes) {
		this.entityTypes = entityTypes;
		this.statementTypes = statementTypes;
	}
	
	public EntityStructureBase recursiveParse(ParseTree tree) {
		String name = tree.getChild(0).getText();
		
		Entity rval = null;
		for(EntityType type : entityTypes) {
			if(type.getName().equals(name)) {
				rval = new Entity(type);
				break;
			}
		}		
		for(StatementType type : statementTypes) {
			if(type.getName().equals(name)) {
				rval = new Statement(type);
				break;
			}
		}
		
		EntityStructure structure = new EntityStructure();

		ParseTree assignments = tree.getChild(2);
		for(int i=0; i < assignments.getChildCount(); i++) {
			ParseTree assignment = assignments.getChild(i);
			
			String key = assignment.getChild(0).getText();
			EntityStructureBase value = recursiveParse(assignment.getChild(2));
			
			structure.put(key, value);
		}
		rval.setStructure(structure);
		
		return rval;
	}
}