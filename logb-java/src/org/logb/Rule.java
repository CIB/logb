package org.logb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Inspired by prolog, this class represents a "ForAll implication". To be
 * precise:
 * 
 * <pre>
 * <code>
 *         ForAll %X: A(%X) => B(%X),
 *         where %X can be any number of arguments, 
 *         and A() and B() can be any statement.
 *    </code>
 * </pre>
 * 
 * <code>`B(%X)`</code> is called the head of the rule. <code>`A(%X)`</code> is
 * the tail.
 */
public class Rule {
	/**
	 * Tries to add a variable of the given name, ensures the name is unique.
	 * 
	 * If the name is not unique, a different name will be selected for you. The
	 * new name can be extracted from the return value by calling `getName()`.
	 * 
	 * @param name
	 *            The name to give the variable, null to have the name selected
	 *            for you.
	 * @return The variable that was added.
	 */
	public Variable addVariable(String name) {
		boolean nameUnique = false;
		while (!nameUnique) {
			nameUnique = true;
			for (Variable var : variables) {
				if (var.getName() == name) {
					nameUnique = false;
				}
			}

			if (!nameUnique) {
				// Append _ until the name is unique
				name = name + "_";
			}
		}

		Variable rval = new Variable(name);
		variables.add(rval);
		return rval;
	}

	/**
	 * Add a dependency. In the rule (A && B && C) => D, A, B and C are
	 * individual dependencies that can be added with this method.
	 * 
	 * @param dependency The statement to add as dependency for the rule
	 */
	public void addDependency(Statement dependency) {
		dependencies.add(dependency);
	}

	/**
	 * Sets the conclusion(head) of the rule.
	 * 
	 * @param conclusion The conclusion to set.
	 */
	public void setConclusion(Statement conclusion) {
		this.conclusion = conclusion;
	}

	/**
	 * @return The conclusion of this rule as pattern.
	 */
	public Pattern getConclusionPattern() {
		Pattern rval = new Pattern();
		for(Variable var : variables) {
			rval.addVariable(var.getName());
		}
		rval.setRoot(this.conclusion);
		return rval;
	}

	/**
	 * Return this rule's dependencies, with variables substituted through the
	 * given substitutions.
	 * 
	 * @param substitutions
	 *            Maps variable names to strings to substitute them with.
	 * @return The substituted list of statements.
	 */
	public List<Statement> getDependencies(
			Map<String, EntityStructureBase> substitutions) {
		List<Statement> rval = new ArrayList<Statement>();
		
		for(Statement dependency: dependencies) {
			// In order to apply the substitutions to the statement,
			// we first pack the statement into a pattern with the
			// same variables as this rule, then call substitute
			// on the pattern, then extract the resulting statement.
			
			Pattern tmpPattern = new Pattern();
			tmpPattern.setRoot(dependency);
			tmpPattern.substitute(substitutions);
			
			// We know it's a Statement because we just set the root
			// with pattern.setRoot, and substitute only changes variables,
			// not existing statements.
			rval.add((Statement) tmpPattern.getRoot());
		}
		return rval;
	}

	private List<Variable> variables;
	private List<Statement> dependencies;
	private Statement conclusion;
}