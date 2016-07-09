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

    def testSubstitute(self):
        # foo(a: x, b: x)[x -> 10] == foo(a: 10, b: 10)

        block = self.kb.root
        variable = self.kb.addEntity(Variable("x"))
        statement = Statement("foo", {"a": variable, "b": variable})
        statementID = self.kb.addStatement(block, statement)

        literal = self.kb.addEntity(Literal(10))
        statement2 = statement.substitute(statementID, self.kb, {"x": literal})

        statement3 = self.kb.addStatement(block, Statement("foo", {"a": literal, "b": literal}))

        self.assertTrue(self.kb[statement2].equals(self.kb, self.kb[statement3]))

    def testUnify(self):
        # foo(l: bar(a: x), r: y) ~~ foo(l: z, r:z) == { y -> bar(a: x), z -> bar(a: x) }

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

        self.assertTrue(statement2.unify(self.kb, statement3ID) == {"y": statementID, "z": statementID})

    def testQuery(self):
        block = self.kb.root

        x = self.kb.addEntity(Variable("x"))
        literal = self.kb.addEntity(Literal(10))
        literal2 = self.kb.addEntity(Literal(15))
        statement = Statement("foo", {"a": literal})
        statement2 = Statement("foo", {"a": literal2})
        statementID = self.kb.addStatement(block, statement)
        statement2ID = self.kb.addStatement(block, statement2)

        pattern = Statement("foo", {"a": x})
        result = [r for r in self.kb.query(pattern)]
        self.assertTrue(len(result) == 2)
        self.assertTrue(result[0][0] == statementID)
        self.assertTrue(result[0][1]['x'] == literal)
        self.assertTrue(result[1][0] == statement2ID)
        self.assertTrue(result[1][1]['x'] == literal2)

