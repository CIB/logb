package org.logb;

/**
 * And { lefthand:A, righthand:A }
 * And { A, A }
 * And { And { A, A }, A}
 * 
 * And {
 *   And { A, A },
 *   A
 * }
 * 
 * And (
 *   And (A, A )
 *   A
 * )
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logb.core.EntityStructure;
import org.logb.core.EntityStructureBase;
import org.logb.core.KnowledgeBase;
import org.logb.core.Module;
import org.logb.core.Pattern;
import org.logb.core.Rule;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.lang.EntityParser;

public class Test {

	public static void main(String[] args) {
		Statement.initialize();
		
		List<Variable> variables = new ArrayList<Variable>();
		
		Module m_Test = new Module("Test");
		StatementType st_And = new StatementType("And", m_Test);
		StatementType st_A = new StatementType("A", m_Test);
		StatementType st_B = new StatementType("B", m_Test);
		Statement A = new Statement(st_A, new EntityStructure());
		Statement B = new Statement(st_B, new EntityStructure());
		
		Pattern testPattern = new Pattern();
		Variable X = testPattern.addVariable("X");
		Variable Y = testPattern.addVariable("Y");
		variables.add(X);
		variables.add(Y);
		
		EntityParser parser = new EntityParser(m_Test.getEntityTypes(), m_Test.getStatementTypes(), variables);
		
		EntityStructureBase andStructure = parser.parse("And(lefthand=X(), righthand=Y())");
		
		testPattern.setRoot(andStructure);
		
		Map<String,EntityStructureBase> substitutions = new HashMap<String,EntityStructureBase>();
		substitutions.put("X", A);
		substitutions.put("Y", B);
		Pattern substituted = testPattern.substitute(substitutions);
		System.out.println(substituted);
		System.out.println(testPattern);
		
		Map<String,EntityStructureBase> substitutions2 = testPattern.match(substituted.getRoot());
		System.out.println(substitutions2);
		
		KnowledgeBase kb = new KnowledgeBase();
		Rule myRule = new Rule();
		Variable F = myRule.addVariable("F");
		variables.clear(); variables.add(F);
		parser = new EntityParser(m_Test.getEntityTypes(), m_Test.getStatementTypes(), variables);
		andStructure = parser.parse("And(lefthand=F(), righthand=F()");
		myRule.addDependency((Statement) andStructure);
		myRule.setConclusion(F);
		
		kb.addRule(myRule);
		parser = new EntityParser(m_Test.getEntityTypes(), m_Test.getStatementTypes(), variables);
		andStructure = parser.parse("And(lefthand=A(), righthand=A()");
		kb.addStatement((Statement) andStructure);
		
		System.out.println(kb.inferStatement(A));
	}

}
