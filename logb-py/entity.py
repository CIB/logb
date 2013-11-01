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
        assert isinstance(typ, EntityType)
        
        self.type = typ
        self.pointer = None
        self.structure = {}

    def set_pointer(self, pointer):
        """ Set the pointer to some "external" data which defines the entity.
            Could, for instance, be a number.
        """
        assert self.typ.has_pointer
        self.pointer = pointer

    def set_structure(self, structure):
        """ Set the own structure. Entity structures can be used to painlessly
            represent hierarchical entities like trees.
        """
        assert self.typ.has_structure
        self.structure = structure

    def deepcopy(self):
        """ Recursively copies this entity and all its structural contents. """
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

    def equals(self, other):
        """ Check if this entity equals another. This means that the type must be the same,
            that if there's a pointer, the pointer must be the same, and that if there's
            a structure, the structure must (recursively) be the same.
        """
        assert isinstance(other, Entity)
        
        if self.type != other.type:
            return False
        
        if self.type.has_pointer != other.type.has_pointer:
            return False
        
        if self.type.has_structure != other.type.has_structure:
            return False
        
        if self.type.has_pointer and self.pointer != other.pointer:
            return False
        
        if self.type.has_structure:
            # Recursively compare the structure
            if not self.recursive_structure_compare(self.structure, other.structure):
                return False
            
        return True
        
    def recursive_structure_compare(self, self_structure, other_structure):
        if type(self_structure) != type(other_structure):
            return False
        
        if isinstance(self_structure, dict):
            if len(self_structure.items()) != len(other_structure.items()):
                return False
            
            for key, value in self_structure.iteritems():
                if (not other_structure.has_key(key)) or (not self.recursive_structure_compare(value, other_structure[key])):
                    return False
                
            return True
        
        elif isinstance(self_structure, Entity):
            return self_structure.equals(other_structure)
        
        else:
            return self_structure == other_structure
