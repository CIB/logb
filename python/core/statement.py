from core import KnowledgeBase, Entity, Variable
from typing import Dict, Union


class Statement(Entity):
    def __init__(self, statementType: str, params: Dict[str, Union[str, Variable]]):
        self.statementType = statementType
        self.parameters = params

    def toString(self, kb: KnowledgeBase):
        printValues = []
        for param in self.parameters.values():
            if isinstance(param, Variable):
                printValues.append(param.name)
            else:
                printValues.append(kb[param].toString(kb))
        params = ", ".join(printValues)
        return "{}({})".format(self.statementType, params)

    def equals(self, kb: KnowledgeBase, other: Entity):
        def entry_equals(e1, e2):
            if isinstance(e1, Variable):
                return e1.equals(e2)
            elif isinstance(e2, Variable):
                return False
            else:
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
            if isinstance(valueID, Variable):
                params[key] = valueID.substitute(env)
            else:
                value = kb[valueID]
                params[key] = value.substitute(valueID, kb, env)
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
            rValueID = other.parameters[key]
            if isinstance(valueID, Variable):
                newEnv = {valueID.name: rValueID}
            elif isinstance(rValueID, Variable):
                newEnv = {rValueID.name: valueID}
            else:
                lValue = kb[valueID]
                newEnv = lValue.unify(kb, rValueID)

            env = mergeEnvironments(kb, env, newEnv)

        return env


def expandEnvironment(kb: KnowledgeBase, env : Dict[str, str]):
    env = {**env}
    for key, valueID in env.items():
        if isinstance(valueID, Variable):
            env[key] = valueID.substitute(env)
        else:
            value = kb[valueID]
            newValueID = value.substitute(valueID, kb, env)
            env[key] = newValueID
    return env


def mergeEnvironments(kb: KnowledgeBase, l: Dict, r: Dict) -> Dict:
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
