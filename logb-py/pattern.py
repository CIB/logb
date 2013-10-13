from copy import deepcopy
from statement import Statement, StatementType
from entity import Entity, EntityType

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
        """ Substitute variables with entities and return the resulting pattern.
        
        Example: Foo(%X).substitute("X", Bar) -> Foo(Bar)
        
        @param dict substitutions: A dict mapping variable names to entities to replace them with 
        @return: A copy of this pattern, with variables replaced by the given substitutions.
        """

        rval = self.deepcopy()

        # Assume root is a statement for now.
        assert isinstance(rval.root, Statement)

        rval.recursive_substitute(rval.root, substitutions)

        # Remove the variables that we substituted.
        for key in substitutions.keys():
            for variable in self.variables:
                if variable.name == key:
                    self.variables.remove(variable)

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

    def match(self, statement):
        """ Check if the given statement matches this pattern. 
            @return If the statement matches, a dict `variable -> substitution`, otherwise None.
        """

        substitutions = {}
        success = self.match_recursive(statement, self.root, substitutions)
        if success:
            return substitutions
        else:
            return None

    def match_recursive(self, statement, pattern_statement, substitutions):
        assert isinstance(statement, Statement)
        assert isinstance(pattern_statement, Statement)
        assert isinstance(substitutions, dict)

        if statement.statement_type != pattern_statement.statement_type:
            return False

        for key, entity in pattern_statement.structure["arguments"].items():
            assert isinstance(entity, Entity) or isinstance(entity, Variable)
            entity_to_match = statement.structure["arguments"][key]

            if isinstance(entity, Statement):
                if not self.match_recursive(entity_to_match, entity, substitutions):
                    return False

            elif isinstance(entity, Variable):
                # Check if substituting works
                if substitutions.has_key(entity.name) and substitutions[entity.name] != entity_to_match:
                    return False

                substitutions[entity.name] = entity_to_match

            else:
                if not entity.equals(entity_to_match):
                    return False

        return True
