from statement import Statement, StatementType
from entity import Entity, EntityType
from pattern import Pattern
from module import Module
from context import Context
from proof_search import ProofSearchTree, ProofSearchTreeNode
from rule import Rule

m_Test = Module("Test")
st_And = StatementType("And", m_Test)
st_A = StatementType("A", m_Test)
st_B = StatementType("B", m_Test)
A = Statement(st_A, {})
B = Statement(st_B, {})

testpattern = Pattern()
varX = testpattern.add_variable("X")
varY = testpattern.add_variable("Y")

myAnd = Statement(st_And, {"lefthand" : varX, "righthand" : varY})
testpattern.set_root(myAnd)

substituted = testpattern.substitute({"X":A, "Y":A})

print(substituted.root.structure["arguments"]["lefthand"].statement_type.name)

print(testpattern.match(substituted.root))
print(testpattern.match(A))


context = Context()
context.add_statement(A)

myrule = Rule()
X = myrule.add_variable("X")
myrule.add_dependency(X)
myrule.set_conclusion(Statement(st_And, {0:X, 1:X}))

context.add_rule(myrule)

print(context.infer_statement(Statement(st_And, {0:A, 1:A})))
print(context.infer_statement(Statement(st_And, {0:B, 1:A})))

# TODO:
# print(context.infer_statement(Statement(st_And, {0:B, 1:A}))) should be None!
# All the combinations of matching/unifying
# Make patterns remove the variables that have been substituted on substitution