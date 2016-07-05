from typing import Dict
from core import KnowledgeBase


class Entity(object):
    def equals(self, kb, other) -> bool:
        pass

    def match(self, kb, otherID: str) -> Dict[str, str]:
        pass

    def unify(self, kb, otherID: str) -> Dict[str, str]:
        """ Unification is the generalized version of pattern matching.

        Both statements should live within the same scope, so that
        variables by the same name refer to the same entity across
        compared statements.
        """

        pass

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]) -> "Entity":
        pass
