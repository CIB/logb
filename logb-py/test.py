from statement import Statement, StatementType
from entity import Entity, EntityType
from pattern import Pattern
from module import Module

m_Test = Module("Test")
st_And = StatementType("And", m_Test)
st_A = StatementType("A", m_Test)
A = Statement(st_A, {})

testpattern = Pattern()
varX = testpattern.add_variable("X")
varY = testpattern.add_variable("Y")

myAnd = Statement(st_And, {"lefthand" : varX, "righthand" : varY})
testpattern.set_root(myAnd)

substituted = testpattern.substitute({"X":A, "Y":A})

print(substituted.root.structure["arguments"]["lefthand"].statement_type.name)

print(testpattern.match(substituted.root))
print(testpattern.match(A))