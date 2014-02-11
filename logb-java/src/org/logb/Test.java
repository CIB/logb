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
import java.util.Collections;
import java.util.List;

import org.logb.core.EntityStructureBase;
import org.logb.core.KnowledgeBase;
import org.logb.core.Module;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.core.modules.MLogic;
import org.logb.lang.EntityParser;

public class Test {

	public static void main(String[] args) {
		Statement.initialize();
		
		MLogic mLogic = new MLogic();
		mLogic.initialize();
		
		KnowledgeBase kb = new KnowledgeBase();
		
		Module m_Test = new Module("Test");
		StatementType st_A = new StatementType("A", m_Test);
		StatementType st_B = new StatementType("B", m_Test);
		
		kb.addModule(mLogic);

		List<StatementType> statementTypes = new ArrayList<StatementType>(mLogic.getStatementTypes());
		statementTypes.add(st_A);
		statementTypes.add(st_B);
		EntityParser parser = new EntityParser(mLogic.getEntityTypes(), statementTypes, new ArrayList<Variable>());
		Statement testStatement = (Statement) parser.parse("And(lefthand=A, righthand=A)");
		Statement toInfer = (Statement) parser.parse("A");
		
		kb.addStatement(testStatement);
		System.out.println(kb.inferStatement(toInfer));
	}

}
