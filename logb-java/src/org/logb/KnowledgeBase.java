package org.logb;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage a repository of statements and rules, and allow to infer new statements
 * from the existing set of statements.
 */
public class KnowledgeBase {
	private List<Statement> statements = new ArrayList<Statement>();
	private List<Rule> rules = new ArrayList<Rule>();
	
	public void addStatement(Statement statement) {
		statements.add(statement);
	}
	
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	/**
	 * Represents the result of an inference.
	 */
	public enum InferenceResult {
		UNKNOWN, // Don't know whether true or false
		TRUE,    // Statement could be inferred from knowledge base
		FALSE    // NOT(Statement) could be inferred from knowledge base
	}
	
	public InferenceResult inferStatement(Statement statement) {
		
	}
}

/**
from proof_search import ProofSearchTree, ProofSearchTreeNode

class Context:
    """ Manage a repository of statements and rules. """

    def __init__(self):
        self.scopes = []
        self.statements = []
        self.rules = []

    def add_statement(self, statement):
        self.statements.append(statement)

    def add_rule(self, rule):
        """ Add a new rule to this context.
        
            @param forall: This statement should be of the form: ForAll %X: A(%X) => B(%X),
                          where %X can be any number of arguments, and A() and B() can be
                          any statement.
        """

        self.rules.append(rule)

    def infer_statement(self, statement):
        """ Try to infer a statement out of the current context.

            This works as follows:
            - For each statement that we're trying to prove, we try to find it in the current database
            - If we can't find the statement in the database, we check each rule for whether it could
              produce what we're looking for, and if so, add its dependencies to the work queue
            - This way, we build a sort of dependency tree, and in this tree try to prove a "path".
              If we manage to prove a path, our original statement is proven.
        """

        tree = ProofSearchTree(statement.deepcopy())

        workqueue = [tree.get_root()]

        while len(workqueue):
            node = workqueue.pop(0)
            statement = node.statement

            for other_statement in self.statements:
                if other_statement.equals(statement):
                    node.set_proven()
                    break
                # TODO: handle disproving by matching NOT

            for rule in self.rules:
                substitutions = rule.conclusion_pattern().match(statement)
                if substitutions:
                    node.add_alternative(rule.get_dependencies(substitutions))

            for alternative in node.alternatives:
                for dependency in alternative:
                    workqueue.append(dependency)

            if node.parent:
                node.parent.check_dependencies()

            # TODO: if the node is now proven, remove all workqueue items in its subtree
            
        # Okay, we're done here. Did we prove/disprove anything?
        return tree.get_root().result
*/