from typing import Dict
from core import KnowledgeBase


class Entity(object):
    def equals(self, kb, other) -> bool:
        pass

    def unify(self, kb, otherID: str) -> Dict[str, str]:
        """ Find a

        Both statements should live within the same scope, so that
        variables by the same name refer to the same entity across
        compared statements.
        """

        # TODO: the resulting mapping should be fully expanded,
        #       e.g. { x -> y, y -> z } should become { x -> z, y -> z }

        pass

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]) -> "Entity":
        pass