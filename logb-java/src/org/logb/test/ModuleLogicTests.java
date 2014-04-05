package org.logb.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.logb.core.EntityStructureBase;
import org.logb.core.Module;
import org.logb.core.Pattern;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.core.modules.MLogic;
import org.logb.lang.EntityParser;

public class ModuleLogicTests {

	@Test
	public void testPatternMatch() {
		Statement.initialize();
		
		MLogic mLogic = new MLogic();
		mLogic.initialize();
		
		Module m_Test = new Module("Test");
		StatementType st_A = new StatementType("A", m_Test);
		StatementType st_B = new StatementType("B", m_Test);
		
		List<StatementType> statementTypes = new ArrayList<StatementType>(mLogic.getStatementTypes());
		statementTypes.add(st_A);
		statementTypes.add(st_B);
		EntityParser parser = new EntityParser(mLogic.getEntityTypes(), statementTypes, new ArrayList<Variable>());
		Statement testStatement = (Statement) parser.parse("And(lefthand=A, righthand=A)");
		
		Pattern conclusionPattern = MLogic.andElimination.getConclusionPattern();
		Map<String, EntityStructureBase> substitutions = conclusionPattern.match(testStatement).leftToRightMatches;
		assertTrue(substitutions != null);
		
		/*for(String key : substitutions.keySet()) {
			System.out.println(key + ": "+substitutions.get(key).toString());
		}*/
	}

}
