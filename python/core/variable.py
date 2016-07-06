from core import KnowledgeBase, Entity
from typing import Dict


class Variable(Entity):
    def __init__(self, name):
        self.name = name

    def equals(self, kb, other):
        if not isinstance(other, Variable):
            return False
        return self.name == other.name

    def unify(self, kb, otherID):
        return {self.name: otherID}

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        if self.name in env:
            return env[self.name]
        else:
            return selfID


