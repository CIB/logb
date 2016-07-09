from typing import Dict
from core import KnowledgeBase


class Entity(object):
    def equals(self, kb, other) -> bool:
        pass

    def unify(self, kb, otherID: str) -> Dict[str, str]:
        """ Find a mapping for the variables in two statements such that
            both statements are equal after replacing the variables with
            their substitutions.

        Both statements should live within the same scope, so that
        variables by the same name refer to the same entity across
        compared statements.
        """

        pass

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]) -> "Entity":
        pass