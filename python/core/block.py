from typing import List

class Block(object):
    def __init__(self):
        self.statements = []  # type: List[str]

    def toString(self, kb):
        return "{ " + ", ".join([kb[s].toString(kb) for s in self.statements]) + " }"
