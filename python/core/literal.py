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

    def unify(self, kb: KnowledgeBase, otherID: str) -> Dict[str, str]:
        if self.equals(kb, kb[otherID]):
            return {}
        else:
            return None