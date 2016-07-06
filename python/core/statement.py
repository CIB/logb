from core import KnowledgeBase, Entity, Variable
from typing import Dict


class Statement(Entity):
    def __init__(self, statement_type: str, params: Dict[str, str]):
        self.statementType = statement_type
        self.parameters = params

    def equals(self, kb: KnowledgeBase, other: Entity):
        def entry_equals(e1, e2):
            # First extract the entries from the KB
            s1 = kb[e1]
            s2 = kb[e2]
            return s1.equals(kb, s2)

        if not isinstance(other, Statement):
            return False

        return (
            self.statementType == other.statementType and
            dict_equals(self.parameters, other.parameters, entry_equals)
        )

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        params = {}
        for key, valueID in self.parameters.items():
            value = kb[valueID]
            substitution = value.substitute(valueID, kb, env)
            params[key] = substitution
        newStatement = Statement(self.statementType, params)
        newID = kb.addEntity(newStatement)
        return newID

    def unify(self, kb: KnowledgeBase, otherID: str) -> Dict[str, str]:
        env = {}
        other = kb[otherID]

        if not isinstance(other, Statement):
            return None

        if self.parameters.keys() != other.parameters.keys():
            return None

        for key, valueID in self.parameters.items():
            lValue = kb[valueID]
            rValueID = other.parameters[key]
            rValue = kb[rValueID]

            if not isinstance(lValue, Variable) and isinstance(rValue, Variable):
                newEnv = rValue.unify(kb, valueID)
            else:
                newEnv = lValue.unify(kb, rValueID)

            env = merge_environments(kb, env, newEnv)

        return env

def expandEnvironment(kb: KnowledgeBase, env : Dict[str, str]):
    env = {**env}
    for key, valueID in env.items():
        value = kb[valueID]
        newValueID = value.substitute(valueID, kb, env)
        env[key] = newValueID
    return env

def merge_environments(kb: KnowledgeBase, l: Dict, r: Dict) -> Dict:
    # If l is None or r is None, yield None
    if not (l is not None and r is not None):
        return None

    # Disjoint keys must be equal
    for key in set(l.keys()).intersection(set(r.keys())):
        lValue = kb[l[key]]
        rValue = kb[r[key]]
        if not lValue.equals(kb, rValue):
            return None

    # Merge the two dicts
    return expandEnvironment(kb, {**l, **r})


def dict_equals(d1, d2, comparator):
    if d1.keys() != d2.keys():
        return False
    for key in d1.keys():
        if not comparator(d1[key], d2[key]):
            return False
    return True
