package org.logb.lang;

import grammar.EntityGrammarLexer;
import grammar.EntityGrammarParser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.logb.core.EntityStructureBase;
import org.logb.core.EntityType;
import org.logb.core.Module;
import org.logb.core.StatementType;
import org.logb.core.Variable;

public class EntityParserTest {
    public void testParser() throws Exception {
        Module testModule = new Module("TestModule");
        EntityType testType = new EntityType("TestEntityType", testModule, true, true);
        List<EntityType> entityTypes = new ArrayList<EntityType>();
        List<StatementType> statementTypes = new ArrayList<StatementType>();
        entityTypes.add(testType);
        
        EntityParser entityParser = new EntityParser(entityTypes, statementTypes, new ArrayList<Variable>());
        EntityStructureBase rval = entityParser.parse("TestEntityType( F = TestEntityType() )");
        System.out.println("rval: "+rval);
    }
}