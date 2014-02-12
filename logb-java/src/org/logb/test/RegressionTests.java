package org.logb.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.logb.core.Entity;
import org.logb.core.EntityStructure;
import org.logb.core.EntityType;
import org.logb.core.Module;
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
	
}
