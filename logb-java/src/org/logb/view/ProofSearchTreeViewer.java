/*
 * SimpleGraphView.java
 *
 * Created on March 8, 2007, 7:49 PM
 *
 * Copyright March 8, 2007 Grotto Networking
 */

package org.logb.view;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.logb.core.ProofSearchTree;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class ProofSearchTreeViewer {
	int edgeNumber = 1;
	Graph<ProofSearchTree.Node, Integer> g;
	Map<ProofSearchTree.Node, String> nodes = new HashMap<ProofSearchTree.Node, String>();

	public ProofSearchTreeViewer() {
	}
	
	public void display(ProofSearchTree pst) {
		g = new SparseMultigraph<ProofSearchTree.Node, Integer>();
		
		this.recursiveAdd(pst.getRoot());

		// Layout<V, E>, VisualizationComponent<V,E>
		Layout<ProofSearchTree.Node, Integer> layout = new ISOMLayout<ProofSearchTree.Node, Integer>(g);
		layout.setSize(new Dimension(1400, 800));
		VisualizationViewer<ProofSearchTree.Node, Integer> vv = new VisualizationViewer<ProofSearchTree.Node, Integer>(
				layout);
		vv.setPreferredSize(new Dimension(1500, 800));
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		// Create a graph mouse and add it to the visualization component
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		JFrame frame = new JFrame("Interactive Graph View 1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	public ProofSearchTree.Node recursiveAdd(ProofSearchTree.Node node) {
		
		if (nodes.containsKey(node)) {
			return node;
		}

		String mappedTo = node.getStatement().toString();
		nodes.put(node, mappedTo);
		g.addVertex(node);

		for (List<ProofSearchTree.Node> alternative : node.getAlternatives()) {
			for (ProofSearchTree.Node dependency : alternative) {
				ProofSearchTree.Node dependencyNode = recursiveAdd(dependency);
				g.addEdge(edgeNumber++, node, dependencyNode);
			}
		}

		return node;
	}

}
