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

public class EntityParserTest {
    public static void main(String[] args) throws Exception {
        EntityGrammarLexer lexer = new EntityGrammarLexer(new ANTLRFileStream("test.mu"));
        EntityGrammarParser parser = new EntityGrammarParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.parse();
        
        Module testModule = new Module("TestModule");
        EntityType testType = new EntityType("TestEntityType", testModule, true, true);
        List<EntityType> entityTypes = new ArrayList<EntityType>();
        List<StatementType> statementTypes = new ArrayList<StatementType>();
        entityTypes.add(testType);
        
        EntityParser entityParser = new EntityParser(entityTypes, statementTypes);
        EntityStructureBase rval = entityParser.recursiveParse(tree.getChild(0));
        System.out.println("rval: "+rval);
    }
}