import module
from entity import Entity, EntityType

import sys
__thismodule__ = sys.modules[__name__]

class StatementType(object):
    def __init__(self, name, mod):
        ':type mod: Module'
        self.name = name
        self.module = module
        mod.add_statement_type(self)

class Statement(Entity):
    def __init__(self, typ, arguments):
        ':type typ: EntityType'
        super(self.__class__, self).__init__(__thismodule__.et_statement)
        self.statement_type = typ
        self.structure = {}
        self.structure["arguments"] = arguments

    def deepcopy(self):
        rval = Statement(self.statement_type, {})
        rval.structure = self.copy_structure(self.structure)
        return rval

m_statement = module.Module("statement")
et_statement = EntityType("statement", m_statement)
et_statement.has_structure = True  # The structure are the statement parameters.
