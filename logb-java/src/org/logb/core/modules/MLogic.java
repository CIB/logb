package org.logb.core.modules;

import java.util.ArrayList;
import java.util.List;

import org.logb.core.EntityStructureBase;
import org.logb.core.Module;
import org.logb.core.Rule;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.lang.EntityParser;

public class MLogic extends Module {
	
	static public StatementType AND;
	static public StatementType OR;
	static public StatementType NOT;
	static public Rule notElimination;
	static public Rule andElimination;

	public MLogic() {
		super("core.Logic");
	}
	
	public void initialize() {
		AND = new StatementType("And", this);
		OR = new StatementType("Or", this);
		NOT = new StatementType("Not", this);

		List<Variable> variables = new ArrayList<Variable>();
		variables.add(new Variable("X"));
		variables.add(new Variable("Y"));
		EntityParser parser = new EntityParser(getEntityTypes(), getStatementTypes(), variables);
		
		// notElimination
		EntityStructureBase dependency = parser.parse("X");
		EntityStructureBase conclusion = parser.parse("Not(what=Not(what=X))");
		
		notElimination = new Rule();
		notElimination.addDependency(dependency);
		notElimination.setConclusion(conclusion);
		this.addRule(notElimination);
		
		// andElimination
		dependency = parser.parse("X");
		conclusion = parser.parse("And(lefthand=X, righthand=X)");
		
		andElimination = new Rule();
		andElimination.addDependency(dependency);
		andElimination.setConclusion(conclusion);
		this.addRule(andElimination);
	}
}
