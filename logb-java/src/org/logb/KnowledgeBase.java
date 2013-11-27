package org.logb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
	 * Try to infer the given statement from all the statements and rules currently in this
	 * knowledge base.
	 * 
	 * @param statement The statement we're trying to infer.
	 * @return PROVEN, DISPROVEN or UNKNOWN, depending on whether we can prove the statement, disprove it, or neither disprove nor prove it.
	 */
	public ProofSearchTree.Result inferStatement(Statement statement) {
		// Initialize the proof search tree with our statement we're looking for as root.
		ProofSearchTree tree = new ProofSearchTree(statement);
		
		// Create a work queue. The work queue is there to implement a breadth first search on our proof search tree.
		Queue<ProofSearchTree.Node> workQueue = new LinkedList<ProofSearchTree.Node>();
		workQueue.add(tree.getRoot());
		
		// One by one, check nodes in the work queue.
		// - If the statement already exists in our KnowledgeBase, the statement is proven
		// - If NOT(statement) already exists in our KnowledgeBase, the statement is disproven
		// - By doing setProven/setDisproven, we trigger an algorithm that "propagates" the new proven/disproven value
		//   of the statement to all statements that depend on it.
		while(workQueue.size() > 0) {
			ProofSearchTree.Node currentNode = workQueue.remove();
			Statement currentStatement = currentNode.getStatement();
			
			for(Statement other : statements) {
				if(other.equals(currentNode.getStatement())) {
					currentNode.setProven();
				} // TODO: compare to NOT(statement) as well
			}
			
			for(Rule rule : rules) {
				Map<String,EntityStructureBase> substitutions = rule.getConclusionPattern().match(currentStatement);
				if(substitutions != null) {
					currentNode.addAlternative(rule.getDependencies(substitutions));
				}
			}
			
			for(List<ProofSearchTree.Node> dependencies : currentNode.getAlternatives()) {
				for(ProofSearchTree.Node dependency : dependencies) {
					workQueue.add(dependency);
				}
			}
		}
		
		return tree.getRoot().getResult();
	}
}