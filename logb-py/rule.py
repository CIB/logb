import pattern

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
        rval = pattern.Variable(name)
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