from typing import List, Dict


class KnowledgeBase(object):
    def __init__(self):
        self.dict = {}
        self.rules = []
        self.last_id = 0

        self.root = self.addBlock()
        self.blocks = [self.root]

    def allocateID(self):
        self.last_id += 1
        return self.last_id

    def __getitem__(self, item: str):
        return self.dict[item]

    def __setitem__(self, key: str, value):
        self.dict[key] = value

    def addBlock(self):
        new_id = self.allocateID()
        self.dict[new_id] = Block()
        return new_id

    def addEntity(self, entity):
        # Scan for entity in dict
        for key, other in self.dict.items():
            if entity.equals(self, other):
                return key
        new_id = self.allocateID()
        self.dict[new_id] = entity
        return new_id

    def addStatement(self, block_id: str, statement: 'Statement') -> str:
        block = self[block_id]  # type: Block
        
        for otherID in block.statements:
            other = self[otherID]
            if statement.equals(self, other):
                return otherID
        
        newID = self.addEntity(statement)
        block.statements.append(newID)
        return newID


class Block(object):
    def __init__(self):
        self.statements = []  # type: List[str]


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


def dict_equals(d1, d2, comparator):
    if d1.keys() != d2.keys():
        return False
    for key in d1.keys():
        if not comparator(d1[key], d2[key]):
            return False
    return True


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
    return {**l, **r}


class Variable(Entity):
    def __init__(self, name):
        self.name = name

    def equals(self, kb, other):
        if not isinstance(other, Variable):
            return False
        return self.name == other.name

    def match(self, kb, otherID):
        return {self.name: otherID}

    def unify(self, kb, otherID):
        return {self.name: otherID}

    def substitute(self, selfID: str, kb: KnowledgeBase, env: Dict[str, str]):
        if self.name in env:
            return env[self.name]
        else:
            return selfID


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

    def match(self, kb: KnowledgeBase, otherID: str) -> bool:
        env = {}
        other = kb[otherID]

        if not isinstance(other, Statement):
            return None

        if self.parameters.keys() != other.parameters.keys():
            return None

        for key, valueID in self.parameters.items():
            value = kb[valueID]
            newEnv = value.match(kb, other.parameters[key])
            env = merge_environments(kb, env, newEnv)

        return env

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

            if not isinstance(lValue, Entity) and isinstance(rValue, Variable):
                newEnv = rValue.unify(kb, valueID)
            else:
                newEnv = lValue.unify(kb, rValueID)

            env = merge_environments(kb, env, newEnv)

        return env


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