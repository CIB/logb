from copy import deepcopy
from statement import Statement, StatementType

class Variable(object):
    def __init__(self, name):
        self.name = name

class Pattern(object):
    def __init__(self):
        self.root = None
        self.variables = []
        self.constraints = []

    def add_variable(self, name):
        rval = Variable(name)
        self.variables.append(rval)
        return rval

    def add_constraint(self, constraint):
        self.constraints.append(constraint)

    def set_root(self, root):
        self.root = root

    def deepcopy(self):
        new_constraints = []
        for value in self.constraints:
            new_constraints.append(value.deepcopy())

        rval = Pattern()
        rval.root = self.root.deepcopy()
        rval.variables = deepcopy(self.variables)
        rval.constraints = new_constraints
        return rval

    def substitute(self, substitutions):
        """ :param substitutions: A dict mapping variable IDs to entities to replace them with """

        rval = self.deepcopy()

        # Assume root is a statement for now.
        assert isinstance(rval.root, Statement)

        self.recursive_substitute(rval.root, substitutions)

        return rval

    def recursive_substitute(self, obj, substitutions):
        for key, value in obj.structure["arguments"].items():
            if isinstance(value, Variable):
                if substitutions.has_key(value.name):
                    # Replace `value` with its substitution
                    obj.structure["arguments"][key] = substitutions[value.name]
                elif isinstance(value, Statement):
                    # This call modifies `value` in-place.
                    self.recursive_subsitute(value, substitutions)
