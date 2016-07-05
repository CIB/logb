from core import Block


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

