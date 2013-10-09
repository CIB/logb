from statement import Statement, StatementType
from entity import Entity, EntityType
from pattern import Pattern
from module import Module

m_Test = Module("Test")
st_And = StatementType("And", m_Test)
st_A = StatementType("A", m_Test)

testpattern = Pattern()
varX = testpattern.add_variable("X")
varY = testpattern.add_variable("Y")

myAnd = Statement(st_And, {"lefthand" : varX, "righthand" : varY})
testpattern.set_root(myAnd)

substituted = testpattern.substitute({"X":st_A, "Y":st_A})

print(substituted.root.structure["arguments"]["lefthand"].name)
