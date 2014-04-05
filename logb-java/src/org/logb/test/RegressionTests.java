package org.logb.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.logb.core.Entity;
import org.logb.core.EntityStructure;
import org.logb.core.EntityStructureBase;
import org.logb.core.EntityType;
import org.logb.core.Module;
import org.logb.core.Pattern;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.lang.EntityParser;

public class RegressionTests {
	private EntityType typeA;
	private EntityType typeB;
	private EntityType typeD;
	private StatementType typeC;
	private StatementType AND;
	private Module testModule;
	
	@Before
	public void before() {
		Statement.initialize();
		testModule = new Module("test");
		typeA = new EntityType("A", testModule, false, true);
		typeB = new EntityType("B", testModule, true, false);
		typeD = new EntityType("D", testModule, false, false);
		typeC = new StatementType("C", testModule);
		AND = new StatementType("And", testModule);
	}
	
	@Test
	public void testEqualityEntities() {
		// Create several entities and compare them.
		Entity one = new Entity(typeA);
		EntityStructure structureOne = new EntityStructure();
		structureOne.put("foo", new Entity(typeB));
		one.setStructure(structureOne);
		

		Entity two = new Entity(typeA);
		EntityStructure structureTwo = new EntityStructure();
		structureTwo.put("foo", new Entity(typeB));
		two.setStructure(structureTwo);
		
		Entity three = new Entity(typeA);
		EntityStructure structureThree = new EntityStructure();
		structureThree.put("foo", new Entity(typeD));
		three.setStructure(structureThree);
		
		assertTrue(one.equals(two));
		assertTrue(two.equals(one));
		assertFalse(three.equals(one));
	}

	@Test
	public void testEqualityStatements() {
		// Create several statements and compare them.
		Entity and = new Statement(AND);
		Statement left = new Statement(typeC);
		Statement right = new Statement(typeC);
		EntityStructure andStructure = new EntityStructure();
		andStructure.put("lefthand", left);
		andStructure.put("righthand", right);
		and.setStructure(andStructure);
		
		Entity and2 = new Statement(AND);
		Statement left2 = new Statement(typeC);
		Statement right2 = new Statement(typeC);
		EntityStructure andStructure2 = new EntityStructure();
		andStructure2.put("lefthand", left2);
		andStructure2.put("righthand", right2);
		and2.setStructure(andStructure2);

		Entity two = new Entity(typeA);
		EntityStructure structureTwo = new EntityStructure();
		structureTwo.put("foo", new Entity(typeB));
		two.setStructure(structureTwo);
		
		assertFalse(two.equals(and));
		assertTrue(and.equals(and2));
	}

	@Test
	public void testParser() {
		// Manual setup of entities to compare with.
		Entity and = new Statement(AND);
		Statement left = new Statement(typeC);
		Statement right = new Statement(typeC);
		EntityStructure andStructure = new EntityStructure();
		andStructure.put("lefthand", left);
		andStructure.put("righthand", right);
		and.setStructure(andStructure);

		Entity two = new Entity(typeA);
		EntityStructure structureTwo = new EntityStructure();
		structureTwo.put("foo", new Entity(typeB));
		two.setStructure(structureTwo);
		
		// Setting up our actual parser.
		List<StatementType> statementTypes = testModule.getStatementTypes();
		List<Variable> variables = new ArrayList<Variable>();
		EntityParser parser = new EntityParser(testModule.getEntityTypes(), statementTypes, variables);
		Statement and2 = (Statement) parser.parse("And(lefthand=C, righthand=C)");
		
		assertNotNull(and2);
		assertFalse(two.equals(and));
		assertTrue(and.equals(and2));
	}
	
	@Test
	public void testMatch() {
		// Setting up our actual parser.
		List<StatementType> statementTypes = testModule.getStatementTypes();
		List<Variable> variables = new ArrayList<Variable>();
		Variable X = new Variable("X");
		variables.add(X);
		EntityParser parser = new EntityParser(testModule.getEntityTypes(), statementTypes, variables);
		Statement andPattern = (Statement) parser.parse("And(lefthand=X, righthand=X)");
		Statement andInstance = (Statement) parser.parse("And(lefthand=C, righthand=C)");
		
		Pattern pattern = new Pattern(); pattern.setRoot(andPattern);
		
		Map<String, EntityStructureBase> substitutions = pattern.match(andInstance).leftToRightMatches;
		
		assertNotNull(substitutions);
		assertNotNull(substitutions.get("X"));
		assertTrue(substitutions.get("X").equals(new Statement(typeC)));
	}
	
	@Test
	public void testTwoWayMatch() {
		// Setting up our actual parser.
		List<StatementType> statementTypes = testModule.getStatementTypes();
		List<Variable> variables = new ArrayList<Variable>();
		Variable X = new Variable("X");
		variables.add(X);
		Variable Y = new Variable("Y");
		variables.add(Y);
		EntityParser parser = new EntityParser(testModule.getEntityTypes(), statementTypes, variables);
		Statement andPattern = (Statement) parser.parse("And(lefthand=X, righthand=B)");
		Statement andInstance = (Statement) parser.parse("And(lefthand=C, righthand=Y)");
		
		Pattern pattern = new Pattern(); pattern.setRoot(andPattern);
		
		Map<String, EntityStructureBase> substitutions = pattern.match(andInstance).leftToRightMatches;
		
		assertNotNull(substitutions);
		assertNotNull(substitutions.get("X"));
		assertNotNull(substitutions.get("Y"));
		assertTrue(substitutions.get("X").equals(new Statement(typeC)));
		assertTrue(substitutions.get("Y").equals(new Entity(typeB)));
	}
	
}
