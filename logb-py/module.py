class Module(object):
    def __init__(self, name):
        self.name = name
        self.entity_types = []
        self.statement_types = []

    def add_entity_type(self, typ):
        self.entity_types.append(typ)

    def add_statement_type(self, typ):
        self.statement_types.append(typ)
