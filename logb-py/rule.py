from pattern import Pattern, Variable

class Rule:
    """ Inspired by prolog, this class represents a "ForAll implication". 
    
        To be precise:
    
        ForAll %X: A(%X) => B(%X),
        where %X can be any number of arguments, 
        and A() and B() can be any statement.
    """

    def __init__(self):
        self.variables = []
        self.dependencies = []
        self.conclusion = None

    def add_variable(self, name):
        rval = Variable(name)
        self.variables.append(rval)
        return rval

    def add_dependency(self, dependency):
        """ Add a new dependency for this rule. """
        self.dependencies.append(dependency)

    def set_conclusion(self, conclusion):
        """ Add a new conclusion for this rule. """
        self.conclusion = conclusion

    def to_statement(self):
        """ Creates a regular statement, consisting of ForAll and implication, for this rule. """

        # TODO
        
    def conclusion_pattern(self):
        """ Checks whether the given statement matches this rule's conclusion(head) and if so, returns the substitutions """
        
        # Build a new pattern with our conclusion
        pattern_to_check = Pattern()
        pattern_to_check.root = self.conclusion
        pattern_to_check.variables = self.variables
        
        return pattern_to_check
    
    def get_dependencies(self, substitutions):
        """ Get the dependencies with all variables substituted through `substitutions`. """
        
        rval = []
        
        for dependency in self.dependencies:
            # For each dependency, we create a pattern from it, then substitute all variables.
            # Once finished, all variables should be gone(there should have been substitutions for them).
            # Since there no longer are any variables, we can remove the statement from its pattern,
            # and just return the statement itself.
            used_pattern = Pattern()
            used_pattern.root = dependency
            used_pattern.variables = self.variables
            
            used_pattern = used_pattern.substitute(substitutions)
            
            #if len(used_pattern.variables) > 0:
            #    # Not all variables have been substituted, oops!
            #    return None
            
            rval.append(used_pattern.root)
            
        return rval