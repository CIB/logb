package org.logb.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.logb.core.Entity;
import org.logb.core.EntityStructure;
import org.logb.core.EntityStructureBase;
import org.logb.core.EntityType;
import org.logb.core.Module;
import org.logb.core.Pattern;
import org.logb.core.Rule;
import org.logb.core.Statement;
import org.logb.core.StatementType;
import org.logb.core.Variable;
import org.logb.lang.EntityParser;

/** A little "game" that allows you to manually perform inferences
 *  using a GUI.
 */
public class InferenceGame extends JFrame {

	JTree tree;
	DefaultTreeModel model;
	DefaultMutableTreeNode ruleContainer;
	DefaultMutableTreeNode statementContainer;
	
	List<Statement> currentStatements = new ArrayList<>();
	List<Rule> activeRules = new ArrayList<>();
	
	Rule selectedRule = null;
	String selectedVariable = null;
	Map<String, EntityStructureBase> variableMap = new HashMap<>();
	
	private Set<String> getVariablesToSelect() {
		Set<String> rval = new HashSet<>();
		for(String variable : selectedRule.getVariableNames()) {
			if(!variableMap.containsKey(variable)) {
				rval.add(variable);
			}
		}
		
		return rval;
	}
	
	private void update() {
		if(selectedRule == null) {
			// List all rules and statements
		} else if(selectedVariable != null) {
			// Select a statement to insert for the variable
		} else {
			// Check if we have all variables selected
			Set<String> variablesToSelect = getVariablesToSelect();
			
			if(variablesToSelect.size() > 0) {
				// Allow to select a variable
			} else {
				// Try to find the statements matching the tail,
				// and generate the head of the rule from it.
			}
		}
	}
	
	private void select(TreePath path) {
		
	}
	
	private void addContextMenuListener() {
	    tree.addMouseListener ( new MouseAdapter ()
	    {
	        public void mousePressed ( MouseEvent e )
	        {
	            if ( e.isPopupTrigger() )
	            {
	                TreePath path = tree.getPathForLocation ( e.getX (), e.getY () );
	                tree.setSelectionPath(path);
	                Rectangle pathBounds = tree.getUI ().getPathBounds ( tree, path );
	                if ( pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) )
	                {
	                    JPopupMenu menu = new JPopupMenu ();
	                    JMenuItem selectItem = new JMenuItem("Select");
	                    menu.add ( selectItem );
	                    menu.show ( tree, pathBounds.x, pathBounds.y + pathBounds.height );
	                    
	                    ActionListener listener = new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								System.out.println("Selected: "+e.getActionCommand());
							}
	                    };
	                    
	                    selectItem.addActionListener(listener);
	                }
	            }
	        }
	    } );
	}
	
	public InferenceGame() {
		setLayout(new BorderLayout(0, 0));

		tree = new JTree();
		model = new DefaultTreeModel(new DefaultMutableTreeNode("JTree"));
		tree.setModel(model);
		add(tree, BorderLayout.CENTER);
		addContextMenuListener();
	}
	
	private void clear() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		ruleContainer = new DefaultMutableTreeNode("Rules");
		root.add(ruleContainer);
		statementContainer = new DefaultMutableTreeNode("Statements");
		root.add(statementContainer);
		model.setRoot(root);
	}
	
	private void addRule(Rule rule) {
		DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule.getConclusionPattern().toString());
		
		DefaultMutableTreeNode variablesContainer = new DefaultMutableTreeNode("Variables");
		for(String varName : rule.getVariableNames()) {
			variablesContainer.add(new DefaultMutableTreeNode(varName));
		}
		
		DefaultMutableTreeNode conclusionContainer = new DefaultMutableTreeNode();
		displayEntityRecursive(conclusionContainer, rule.getConclusionPattern().getRoot());
		
		DefaultMutableTreeNode dependenciesContainer = new DefaultMutableTreeNode("Dependencies");
		
		for(Statement dependency : rule.getDependencies(new HashMap<String, EntityStructureBase>())) {
			DefaultMutableTreeNode dependencyNode = new DefaultMutableTreeNode();
			displayEntityRecursive(dependencyNode, dependency);
		}
		ruleNode.add(dependenciesContainer);
		ruleNode.add(conclusionContainer);
		ruleContainer.add(ruleNode);
	}
	
	private void addStatement(Statement statement) {
		DefaultMutableTreeNode container = new DefaultMutableTreeNode();
		displayEntityRecursive(container, statement);
		statementContainer.add(container);
	}
	
	private void displayEntityRecursive(DefaultMutableTreeNode treeNode, EntityStructureBase node) {
		if(node instanceof Variable) {
			Variable variable = (Variable) node;
			treeNode.setUserObject(variable.getName());
		} else if(node instanceof Entity) {
			Entity entity = (Entity) node;
			
			treeNode.setUserObject(entity.getDisplayName());
			
			if(entity.getStructure() != null) {
				EntityStructure structure = (EntityStructure) entity.getStructure();
				for(String key : structure.keySet()) {
					EntityStructureBase child = structure.get(key);
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("");
					displayEntityRecursive(childNode, child);
					String currentName = (String) childNode.getUserObject();
					childNode.setUserObject(key + ": " + currentName);
					treeNode.add(childNode);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InferenceGame frame = new InferenceGame();
					
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setBounds(100, 100, 450, 300);
					
					Statement.initialize();

					Module testModule = new Module("test");
					new EntityType("A", testModule, false, true);
					new EntityType("B", testModule, true, false);
					new EntityType("D", testModule, false, false);
					new StatementType("C", testModule);
					new StatementType("And", testModule);
					
					List<Variable> variables = new ArrayList<Variable>();
					Variable X = new Variable("X");
					variables.add(X);
					Variable Y = new Variable("Y");
					variables.add(Y);
					EntityParser parser = new EntityParser(testModule.getEntityTypes(), testModule.getStatementTypes(), variables);
					Statement andPattern = (Statement) parser.parse("And(lefthand=And(lefthand=X, righthand=A), righthand=B)");
					
					frame.displayEntityRecursive((DefaultMutableTreeNode) frame.model.getRoot(), andPattern);
					
					frame.setTitle("Inference Game");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
