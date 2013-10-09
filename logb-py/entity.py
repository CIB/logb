import module

class EntityType(object):
    def __init__(self, name, mod):
        ':type mod: Module'
        self.name = name
        self.module = module
        self.has_pointer = False
        self.has_structure = False
        mod.add_entity_type(self)

class Entity(object):
    def __init__(self, typ):
        ':type typ: EntityType'
        self.type = typ
        self.pointer = None
        self.structure = {}

    def set_pointer(self, pointer):
        assert self.typ.has_pointer
        self.pointer = pointer

    def set_structure(self, structure):
        assert self.typ.has_structure
        self.structure = structure

    def deepcopy(self):
        rval = Entity(self.type)
        rval.module = self.module
        rval.pointer = self.pointer
        rval.structure = self.copy_structure(self.structure)
        return rval

    def copy_structure(self, obj):
        """ Copy an entity structure recursively. The recursive part includes strings, dicts and other Entity's """

        if isinstance(obj, dict):
            rval = {}
            for key, value in obj.items():
                rval[key] = self.copy_structure(value)
            return rval
        elif isinstance(obj, Entity):
            return obj.deepcopy()
        else:
            return obj
