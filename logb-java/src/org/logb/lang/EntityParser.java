package org.logb.lang;

import grammar.EntityGrammarLexer;
import grammar.EntityGrammarParser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.logb.core.Entity;
import org.logb.core.EntityStructure;
import org.logb.core.EntityStructureBase;
import org.logb.core.EntityType;
import org.logb.core.Module;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;

public class EntityParser {

	private List<EntityType> entityTypes;
	private List<StatementType> statementTypes;
	private List<Variable> variables;

	public EntityParser(List<EntityType> entityTypes,
			List<StatementType> statementTypes, List<Variable> variables) {
		this.entityTypes = entityTypes;
		this.statementTypes = statementTypes;
		this.variables = variables;
	}
	
	public EntityStructureBase parse(String text) {
        EntityGrammarLexer lexer = new EntityGrammarLexer(new ANTLRInputStream(text));
        EntityGrammarParser parser = new EntityGrammarParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        
        return recursiveParse(tree.getChild(0));
	}

	private EntityStructureBase recursiveParse(ParseTree tree) {
		String name = tree.getChild(0).getText();

		EntityStructureBase rval = null;
		for (Variable variable : variables) {
			if (variable.getName().equals(name)) {
				rval = variable;
				break;
			}
		}
		for (EntityType type : entityTypes) {
			if (type.getName().equals(name)) {
				rval = new Entity(type);
				break;
			}
		}
		for (StatementType type : statementTypes) {
			if (type.getName().equals(name)) {
				rval = new Statement(type);
				break;
			}
		}

		EntityStructure structure = new EntityStructure();

		if (rval instanceof Entity) {
			ParseTree assignments = tree.getChild(2);
			for (int i = 0; i < assignments.getChildCount(); i+=2) {
				// i+=2 because the separating commas are also children
				ParseTree assignment = assignments.getChild(i);
				System.out.println(assignment.getText());

				String key = assignment.getChild(0).getText();
				EntityStructureBase value = recursiveParse(assignment
						.getChild(2));

				structure.put(key, value);
			}
			((Entity) rval).setStructure(structure);
		}

		return rval;
	}
}