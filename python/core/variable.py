from core import KnowledgeBase, Entity
from typing import Dict


class Variable(object):
    def __init__(self, name):
        self.name = name

    def toString(self, kb: KnowledgeBase):
        return ":{}".format(self.name)

    def equals(self, other):
        if not isinstance(other, Variable):
            return False
        return self.name == other.name

    def unify(self, kb, otherID):
        return {self.name: otherID}

    def substitute(self, env: Dict[str, str]):
        if self.name in env:
            return env[self.name]
        else:
            return self


