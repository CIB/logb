package org.logb;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		Statement.initialize();
		
		Module m_Test = new Module("Test");
		StatementType st_And = new StatementType("And", m_Test);
		StatementType st_A = new StatementType("A", m_Test);
		StatementType st_B = new StatementType("B", m_Test);
		Statement A = new Statement(st_A, new EntityStructure());
		Statement B = new Statement(st_B, new EntityStructure());
		
		Pattern testPattern = new Pattern();
		Variable X = testPattern.addVariable("X");
		Variable Y = testPattern.addVariable("Y");
		
		EntityStructure andStructure = new EntityStructure();
		andStructure.put("lefthand",  X);
		andStructure.put("righthand", Y);
		
		Statement myAnd = new Statement(st_And, andStructure);
		testPattern.setRoot(myAnd);
		
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
		andStructure = new EntityStructure();
		andStructure.put("lefthand", F);
		andStructure.put("righthand", F);
		Statement ruleAnd = new Statement(st_And, andStructure);
		myRule.addDependency(ruleAnd);
		myRule.setConclusion(F);
		
		kb.addRule(myRule);
		andStructure = new EntityStructure();
		andStructure.put("lefthand", A);
		andStructure.put("righthand", A);
		kb.addStatement(new Statement(st_And, andStructure));
		
		System.out.println(kb.inferStatement(A));
	}

}
