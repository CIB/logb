from unittest import TestCase
from core import Statement, Literal, Variable, KnowledgeBase


class TestCore(TestCase):
    def setUp(self):
        self.kb = KnowledgeBase()

    def testEquals(self):
        block = self.kb.root
        literal = self.kb.addEntity(Literal(10))
        statement = Statement("foo", {"x": literal})
        self.kb.addStatement(block, statement)

        literal2 = self.kb.addEntity(Literal(10))
        statement2 = Statement("foo", {"x": literal2})
        self.kb.addStatement(block, statement)
        self.assertTrue(statement.equals(self.kb, statement2))

    def testNotEquals(self):
        block = self.kb.root
        literal = self.kb.addEntity(Literal(10))
        statement = Statement("foo", {"x": literal})
        self.kb.addStatement(block, statement)
        self.assertFalse(self.kb[literal].equals(self.kb, statement))

        literal2 = self.kb.addEntity(Literal(10))
        statement2 = Statement("foo", {"x": literal2, "y": literal2})
        self.kb.addStatement(block, statement)
        self.assertFalse(statement.equals(self.kb, statement2))

        literal3 = self.kb.addEntity(Literal(5))
        statement3 = Statement("foo", {"x": literal3})
        self.kb.addStatement(block, statement)
        self.assertFalse(statement.equals(self.kb, statement3))

    def testMatch(self):
        block = self.kb.root
        variable = self.kb.addEntity(Variable("x"))
        statement = Statement("foo", {"a": variable, "b": variable})

        literal = self.kb.addEntity(Literal(10))
        statement2 = self.kb.addStatement(block, Statement("foo", {"a": literal, "b": literal}))

        self.assertTrue(statement.match(self.kb, statement2) == {"x": literal})

    def testSubstitute(self):
        block = self.kb.root
        variable = self.kb.addEntity(Variable("x"))
        statement = Statement("foo", {"a": variable, "b": variable})
        statementID = self.kb.addStatement(block, statement)

        literal = self.kb.addEntity(Literal(10))
        statement2 = statement.substitute(statementID, self.kb, {"x": literal})

        statement3 = self.kb.addStatement(block, Statement("foo", {"a": literal, "b": literal}))

        self.assertTrue(self.kb[statement2].equals(self.kb, self.kb[statement3]))

    def testUnify(self):
        block = self.kb.root
        x = self.kb.addEntity(Variable("x"))
        y = self.kb.addEntity(Variable("y"))
        z = self.kb.addEntity(Variable("z"))
        statement = Statement("bar", {"a": x})
        statementID = self.kb.addEntity(statement)
        statement2 = Statement("foo", {"l": statementID, "r": y})
        statement2ID = self.kb.addStatement(block, statement2)
        statement3 = Statement("foo", {"l": z, "r": z})
        statement3ID = self.kb.addStatement(block, statement3)

        self.assertTrue(statement2.unify(self.kb, statement3ID) == {"y": z, "z": statementID})