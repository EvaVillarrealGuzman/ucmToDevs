package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import Transformer.Domain.Component;
import Transformer.Domain.Path;
import Transformer.Domain.Responsibility;

//TODO mejorar separación del armado del camino con el de los puertos

/**
 * This class creates the ports of each element of the path, as well as the path
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class Port {

	static Path PATH;

	/**
	 * Create port
	 * 
	 * @param xmlTree
	 */
	public static void createPort(JTree xmlTree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		createPortRoot(xmlTree, rootNode);
		createPortResponsability(xmlTree);
		createPortComponent(xmlTree, rootNode);
		createPortElementOrAnd(xmlTree);
	}

	/**
	 * Creates input and output ports for the view
	 * 
	 * @param xmlTree
	 * @param rootNode
	 */
	private static void createPortRoot(JTree xmlTree, DefaultMutableTreeNode rootNode) {
		Component root = (Component) rootNode.getUserObject();

		root.getInputPorts().add("erip");
		root.getOutputPorts().add("esop");
	}

	/**
	 * Create input port array to currentComponent
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private static ArrayList<String> getInputPort(Component currentComponent, DefaultMutableTreeNode currentNode) {
		boolean haveParent = true;

		ArrayList<String> inputPorts = new ArrayList<String>();

		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;

		while (inputPorts.size() < 1 && haveParent) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) auxCurrentNode.getParent();
			if (parentData != null) {
				Component parent = (Component) parentData.getUserObject();
				for (int j = 0; j < parent.getInternalCouplingSecond().size(); j++) {
					if (parent.getInternalCouplingSecond().get(j).equals(auxChild.getName())) {
						inputPorts.add(parent.getInternalCouplingName().get(j));
					}
				}
				auxChild = parent;
				auxCurrentNode = parentData;

			} else {
				haveParent = false;
			}

		}
		if (currentComponent.isStart()) {
			inputPorts.add("0");
		}
		return inputPorts;
	}

	/**
	 * Creates input and output ports of components
	 * 
	 * @param xmlTree
	 * @param currentNode
	 */
	private static void createPortComponent(JTree xmlTree, DefaultMutableTreeNode currentNode) {

		Component currentComponent = (Component) currentNode.getUserObject();

		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node domainNode = (Transformer.Domain.Node) node.getUserObject();

			if (domainNode instanceof Component) {
				Component component = (Component) domainNode;
				for (int j = 0; j < currentComponent.getInternalCouplingFirts().size(); j++) {
					// Gets the first node of a relationship existing between an
					// internal coupling of currentComponent
					DefaultMutableTreeNode firstNode = getDefaultMutableTreeNode(xmlTree,
							currentComponent.getInternalCouplingFirts().get(j));
					// Generate internalCouplingName array of
					// currentComponent
					currentComponent.getInternalCouplingName().add(getNameRelation(currentComponent, xmlTree, firstNode,
							currentComponent.getInternalCouplingSecond().get(j)));
				}
				// Creates input and output ports, which will serve for external
				// coupling
				ArrayList<String> inputPorts = getInputPort(component, node);
				for (int j = 0; j < inputPorts.size(); j++) {
					component.getInputPorts().add("peip" + inputPorts.get(j));
				}
				ArrayList<String> outputPorts = getOutputPort(component, node);
				for (int j = 0; j < outputPorts.size(); j++) {
					component.getOutputPorts().add("seop" + outputPorts.get(j));
				}
				createPortComponent(xmlTree, node);
			}
		}
	}

	/**
	 * Creates input and output ports of responsibilities. It also
	 * simultaneously creates the path.
	 * 
	 * @param xmlTree
	 */
	private static void createPortResponsability(JTree xmlTree) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		int count = 1;

		PATH = new Path();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			// Only leaves of the tree are responsibilities
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Responsibility) {
					domainNode.getInputPorts().add("prip");
					domainNode.getOutputPorts().add("srop");
					// Create path, assigning each relation a name
					PATH.getFirst().add(domainNode.getName());
					// TODO ampliar no solo a 0 para and y or
					PATH.getSecond().add(((Transformer.Domain.Responsibility) domainNode).getSuccessor().get(0));
					PATH.getPort().add(String.valueOf(count));
					count++;
				}
				// TODO hacer un if para or y and
			}
		}
	}

	// TODO
	/**
	 * Creates the input and output ports of the elements and and or of the path
	 * 
	 * @param xmlTree
	 */
	private static void createPortElementOrAnd(JTree xmlTree) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Element) {
					Transformer.Domain.Element element = (Transformer.Domain.Element) domainNode;
					if (element.getType().equals("ucm.map:AndJoin") || element.getType().equals("ucm.map:OrJoin")) {
						element.getOutputPorts().add("pathout");
						for (int j = 0; j < element.getPredecessor().size(); j++) {
							element.getInputPorts().add("pathin" + j);
						}
					} else {
						element.getInputPorts().add("pathin");
						for (int j = 0; j < element.getSuccessor().size(); j++) {
							element.getOutputPorts().add("pathout" + j);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the name for the relation between firstNode and secondNode
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @param xmlTree
	 * @param firstNode
	 * @param SecondNode
	 * @return
	 */
	private static String getNameRelation(Component currentComponent, JTree xmlTree, DefaultMutableTreeNode firstNode,
			String SecondNode) {

		// The array will contain the responsibilities that are within inside of
		// currentComponent
		ArrayList<Responsibility> internalResponsibilities = new ArrayList<Responsibility>();

		Component firstComponent = (Component) firstNode.getUserObject();
		getInternalResponsibilities(firstNode, xmlTree, internalResponsibilities);

		// The process consists of finding the two responsibilities that connect
		// the firstNode to the SecondNode, and thus return the name of the
		// relationship between those two responsibilities, since it is also the
		// name of the relationship between those components

		// It goes through each internal responsibility until it finds the one
		// that is associated with the corresponding responsibility of
		// secondNode
		for (int i = 0; i < internalResponsibilities.size(); i++) {
			boolean haveParent = true;
			Responsibility responsibility = internalResponsibilities.get(i);
			// TODO join and
			DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(xmlTree, responsibility.getSuccessor().get(0));
			if (nextNode != null) {
				while (haveParent) {
					if (nextNode.getParent() != null) {
						// Gets the parent of the nextNode, and if it is equal
						// to secondNode it returns the name of the relation
						DefaultMutableTreeNode nextParent = (DefaultMutableTreeNode) nextNode.getParent();
						Transformer.Domain.Node nextParentNode = (Transformer.Domain.Node) nextParent.getUserObject();
						if (nextParentNode.getName().equals(SecondNode)) {
							for (int j = 0; j < getPath().getFirst().size(); j++) {
								// To obtain the name it looks for a relation of
								// the way between firstNode and secondNode, and
								// looks in name assigned to the same one
								if (getPath().getFirst().get(j).equals(responsibility.getName())
										&& getPath().getSecond().get(j).equals(responsibility.getSuccessor().get(0))) {
									// Between a firstNode and secondNode there
									// may be more than one relationship, this
									// checks that the name that is returned has
									// not been used before
									boolean esDiferente = true;
									for (int k = 0; k < currentComponent.getInternalCouplingName().size(); k++) {
										if (getPath().getPort().get(j)
												.equals(currentComponent.getInternalCouplingName().get(k))) {
											esDiferente = false;
										}
									}
									if (esDiferente) {
										return getPath().getPort().get(j);
									}
								}
							}
						}
						nextNode = nextParent;
					} else {
						haveParent = false;
					}

				}
			}
		}

		return null;
	}

	/**
	 * Gets all responsibilities that are inside a component
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @param xmlTree
	 * @param internalResponsibilities
	 */
	private static void getInternalResponsibilities(DefaultMutableTreeNode currentNode, JTree xmlTree,
			ArrayList<Responsibility> internalResponsibilities) {

		// The process consists of traversing all the children of the current
		// node. If the child node is a responsibility, then add it to the
		// internalResponsabilities array; But being a component seeks the
		// children of the same (recursive process) and obtains its
		// responsibilities

		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node nodeData = (Transformer.Domain.Node) node.getUserObject();

			if (nodeData instanceof Transformer.Domain.Component) {
				getInternalResponsibilities(node, xmlTree, internalResponsibilities);
			} else if (nodeData instanceof Transformer.Domain.Element
					|| nodeData instanceof Transformer.Domain.Responsibility) {
				// TODO ver para and y or
				Responsibility nodeDataComponent = (Responsibility) nodeData;
				internalResponsibilities.add(nodeDataComponent);
			}
		}
	}

	/**
	 * Create output port array to currentComponent
	 * 
	 * @param child
	 * @param currentNode
	 * @return
	 */
	private static ArrayList<String> getOutputPort(Component child, DefaultMutableTreeNode currentNode) {
		boolean haveParent = true;

		ArrayList<String> outputPorts = new ArrayList<String>();

		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = child;

		while (outputPorts.size() < 1 && haveParent) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) auxCurrentNode.getParent();
			if (parentData != null) {
				Component parent = (Component) parentData.getUserObject();
				for (int j = 0; j < parent.getInternalCouplingFirts().size(); j++) {
					if (parent.getInternalCouplingFirts().get(j).equals(auxChild.getName())) {
						outputPorts.add(parent.getInternalCouplingName().get(j));
					}
				}
				auxChild = parent;
				auxCurrentNode = parentData;
			} else {
				haveParent = false;
			}

		}

		if (child.isEnd()) {
			outputPorts.add("0");
		}

		return outputPorts;
	}

	/***
	 * Returns the node of the tree whose name matches to the name parameter
	 * 
	 * @param name
	 * @return
	 */
	private static DefaultMutableTreeNode getDefaultMutableTreeNode(JTree xmlTree, String name) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			Transformer.Domain.Node data = (Transformer.Domain.Node) node.getUserObject();
			if ((data).getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public static Path getPath() {
		return PATH;
	}

}
