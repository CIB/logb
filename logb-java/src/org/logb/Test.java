package org.logb;

import java.util.HashMap;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
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
	}

}
