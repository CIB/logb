from .statement import Statement
from .block import Block
from .entity import Entity
from .knowledgebase import KnowledgeBase
from .variable import Variable
from typing import Dict, List


class Quantor(Statement):
    def __init__(self, statementType: str, params: Dict[str, str],
                 variables: List[str], block: Block):
        super().__init__(statementType, params)
        self.variables = variables
        self.block = block

    def toString(self, kb: KnowledgeBase):
        return self.statementType + str(self.variables) + kb[self.block].toString(kb)

    def equals(self, kb: KnowledgeBase, otherEntity: Entity):
        if not isinstance(otherEntity, Quantor):
            return False

        # TODO: Actually compare the blocks..
        return False

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        # Check if env contains any variables we're binding. If so, we must rebind them first.
        statements = []

        newEnv = dict([(key, value) for (key, value) in env.items() if key not in self.variables])

        block = kb[self.block]
        for statementID in block.statements:
            statement = kb[statementID]
            statements.append(statement.substitute(statementID, kb, newEnv))

        blockID = kb.addBlock()
        block = kb[blockID]
        block.statements = statements
        newID = kb.addEntity(Quantor(self.statementType, self.parameters, self.variables, blockID))
        return newID