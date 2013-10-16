class ProofSearchTree(object):
    """ A tree representing the search for a specific statement. """

    def __init__(self, root):
        self.root = ProofSearchTreeNode(root)

    def get_root(self):
        return self.root


class ProofSearchTreeNode(object):
    def __init__(self, statement):
        self.statement = statement
        self.result = None # can also be "proven" and "disproven"
        self.alternatives = []
        self.parent = None

    def add_alternative(self, statements):
        """ Adds one combination of statements that might yield the desired result. 
            
            @param statements: A set of statements that, if all true(AND) imply self.statement
        
        """

        # TODO: check the alternative isn't already there

        statement_nodes = []
        for statement in statements:
            newnode = ProofSearchTreeNode(statement)
            newnode.parent = self
            statement_nodes.append(newnode)

        self.alternatives.append(statement_nodes)

    def set_proven(self):
        self.result = "proven"
    
    def set_disproven(self):
        self.result = "disproven"
    
    def check_dependencies(self):
        for alternative in self.alternatives:
            allTrue = True
            for dependency in alternative:
                if dependency.result != "proven":
                    allTrue = False
            if allTrue:
                self.set_proven()