class ProofSearchTree(object):
    """ A tree representing the search for a specific statement. """

    def __init__(self, root):
        self.root = ProofSearchTreeNode(root)

    def get_root(self):
        return self.root


class ProofSearchTreeNode(object):
    def __init__(self, statement):
        self.statement = statement
        self.alternatives = []

    def add_alternative(self, statements):
        """ Adds one combination of statements that might yield the desired result. 
            
            @param statements: A set of statements that, if all true(AND) imply self.statement
        
        """

        # TODO: check the alternative isn't already there

        statement_nodes = []
        for statement in statements:
            statement_nodes.append(ProofSearchTreeNode(statement))

        self.alternatives.append(statement_nodes)

