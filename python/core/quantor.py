from .statement import Statement
from .block import Block
from .knowledgebase import KnowledgeBase
from typing import Dict, List


class Quantor(Statement):
    def __init__(self, statementType: str, params: Dict[str, str],
                 variables: List[str], block: Block):
        super().__init__(statementType, params)
        self.variables = variables
        self.block = block

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        # Check if env contains any variables we're binding. If so, we must rebind them first.
        conflictingVariables = set(env.keys()).intersection(self.variables)

        def computeReplacement(existingVariables, key):
            while key in existingVariables:
                key = key + "_"
            existingVariables.push(key)
            return key

        existingVariables = self.variables + env.keys()
        replacementPairs = [(key, computeReplacement(existingVariables, key)) for key in conflictingVariables]

        replacementEnv = Dict(replacementPairs)

        def replace(key, dict):
            if key in dict: return key[dict]
            else: return key

        variables = [replace(variable, replacementEnv) for variable in self.variables]
        statements = []
        for statementID in self.block.statements:
            statement = kb[statementID]
            statements.push(statement.substitute(statementID, kb, replacementEnv))

        block = Block()
        block.statements = statements
        return Quantor(self.statementType, self.parameters, variables, block)