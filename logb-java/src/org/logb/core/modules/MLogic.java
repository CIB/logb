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
	static public Rule orElimination;
	static public Rule orElimination2;

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
		EntityStructureBase dependency2;
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
		
		// orElimination
		dependency = parser.parse("X");
		conclusion = parser.parse("Or(lefthand=X, righthand=Y)");
		
		orElimination = new Rule();
		orElimination.addDependency(dependency);
		orElimination.setConclusion(conclusion);
		this.addRule(orElimination);
		
		// orElimination2
		dependency = parser.parse("Or(lefthand=X, righthand=Y)");
		dependency2 = parser.parse("Not(what=X)");
		conclusion = parser.parse("Y");
		
		orElimination2 = new Rule();
		orElimination2.addDependency(dependency);
		orElimination2.addDependency(dependency2);
		orElimination2.setConclusion(conclusion);
		this.addRule(orElimination2);
	}
}
