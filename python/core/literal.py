from core import KnowledgeBase, Entity
from typing import Dict


class Literal(Entity):
    def __init__(self, value):
        self.value = value

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        return selfID

    def equals(self, kb, other):
        if not isinstance(other, Literal):
            return False
        return self.value == other.value

    def match(self, kb: KnowledgeBase, other: str) -> Dict[str, str]:
        if self.equals(kb, kb[other]):
            return {}
        else:
            return None

    def unify(self, kb: KnowledgeBase, otherID: str) -> Dict[str, str]:
        return self.match(kb, otherID)
