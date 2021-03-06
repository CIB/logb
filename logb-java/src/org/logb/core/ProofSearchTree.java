package org.logb.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree where each node has children that represent what must be proven for
 * the parent to be proven.
 *
 */
public class ProofSearchTree {
	Node root;
	
	public ProofSearchTree(Statement statement) {
		this.root = new Node(statement);
	}
	
	public Node getRoot() {
		return root;
	}
	
	public enum Result {
		PROVEN,
		DISPROVEN,
		UNKNOWN
	}
	
	public class Node {
		// A list of alternatives, and the alternatives themselves are a list of dependencies for that alternative.
		private List< List<Node> > alternatives = new ArrayList<List<Node>>();
		
		// The parent of this node in the proof search tree.
		private Node parent;
		
		// The statement this node represents.
		private Statement statement;
		
		private Result isProven = Result.UNKNOWN;
		
		public Node(Statement statement) {
			this.statement = statement;
		}
		
		public void setProven() {
			isProven = Result.PROVEN;
			if(parent != null) {
				parent.checkDependencies();
			}
		}
		
		public void setDisproven() {
			isProven = Result.DISPROVEN;
			if(parent != null) {
				parent.checkDependencies();
			}
		}
		
		public Result getResult() {
			return isProven;
		}
		
		public Statement getStatement() {
			return this.statement;
		}
		
		public List<List<Node>> getAlternatives() {
			return alternatives;
		}
		
		/** Add a new alternative with which this node can be proven.
		 * 
		 *  An alternative consists of several statements. The implication is that
		 *  if all these statements are true, the statement of this node is also true.
		 *  
		 * @param dependencies The set of statements that make up this alternative.
		 */
		public void addAlternative(List<Statement> dependencies) {
			List<Node> dependencyNodes = new ArrayList<Node>();
			
	        for(Statement statement: dependencies) {
	            Node newNode = new Node(statement);
	            newNode.parent = this;
	            dependencyNodes.add(newNode);
	        }
			
			alternatives.add(dependencyNodes);
		}
		
		/**
		 * Checks this node's children, and if one of the dependency sets is set to be proven,
		 * sets this node to be proven. Meanwhile, if *all* dependency sets are disproven, sets
		 * this node to be disproven.
		 */
		public void checkDependencies() {
			// This algorithm needs to do several things.
			// 1. To check whether `this` is proven, it needs to check whether there is one alternative
			//    for which ALL dependencies are proven. If there is, `this` is proven.
			
			for(List<Node> dependencies : alternatives) {
				boolean proven = true;
				
				for(Node dependency : dependencies) {
					if(dependency.getResult() != Result.PROVEN) {
						proven = false;
						break;
					}
				}
				
				if(proven) {
					this.setProven();
					break;
				}
			}
		}
		
		@Override
		public String toString() {
			return statement.toString();
		}
	}
}
