package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import Transformer.Domain.Component;
import Transformer.Domain.Element;
import Transformer.Domain.Node;
import Transformer.Domain.Responsibility;

//TODO mejorar separaciÃ³n del armado del camino con el de los puertos

/**
 * This class creates the ports of each element of the path, as well as the path
 * 
 * @author: MarÃ­a Eva Villarreal GuzmÃ¡n. E-mail: villarrealguzman@gmail.com
 *
 */
public class Port {

	static Path PATH;

	/**
	 * Create port
	 * 
	 * @param xmlTree
	 */
	public static void createPort(JTree xmlTree, Path path) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		PATH = path;

		createPortRoot(xmlTree, rootNode);
		createPortSimpleComponent(xmlTree);
		createPortCompositeComponent(xmlTree, rootNode);
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
	 * Creates input and output ports of simple components.
	 * 
	 * @param xmlTree
	 */
	private static void createPortSimpleComponent(JTree xmlTree) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			// Only leaves of the tree are simple components
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Responsibility) {
					domainNode.getInputPorts().add("prip");
					domainNode.getOutputPorts().add("srop");
				} else if (domainNode instanceof Transformer.Domain.Element) {
					// para crear los puertos de entrada, busca en el camino
					// donde se encuentra al principio
					for (int j = 0; j < PATH.getFirst().size(); j++) {
						if (PATH.getFirst().get(j) != null && PATH.getFirst().get(j).equals(domainNode.getName())) {
							domainNode.getOutputPorts().add("seop" + PATH.getPort().get(j));
						}
					}
					for (int j = 0; j < PATH.getSecond().size(); j++) {
						if (PATH.getSecond().get(j) != null && PATH.getSecond().get(j).equals(domainNode.getName())) {
							domainNode.getInputPorts().add("peip" + PATH.getPort().get(j));
						}
					}
				}
			}
		}
	}

	/**
	 * Creates input and output ports of composite components
	 * 
	 * @param xmlTree
	 * @param currentNode
	 */
	private static void createPortCompositeComponent(JTree xmlTree, DefaultMutableTreeNode currentNode) {

		Component currentComponent = (Component) currentNode.getUserObject();

		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node domainNode = (Transformer.Domain.Node) node.getUserObject();

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

			if (domainNode instanceof Component) {
				Component component = (Component) domainNode;
				// Creates input and output ports
				getInputPort(xmlTree, component, node);
				getOutputPort(xmlTree, component, node);

				createPortCompositeComponent(xmlTree, node);
			}
		}
	}

	/**
	 * Create input port array to currentComponent
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private static void getInputPort(JTree xmlTree, Component currentComponent, DefaultMutableTreeNode currentNode) {
		boolean haveParent = true;

		for (int j = 0; j < currentComponent.getExternalInputCoupling().size(); j++) {
			currentComponent.getInputPorts().add(null);
		}

		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;

		while (haveParent) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) auxCurrentNode.getParent();
			if (parentData != null) {
				Component parent = (Component) parentData.getUserObject();
				for (int j = 0; j < parent.getInternalCouplingSecond().size(); j++) {
					if (parent.getInternalCouplingSecond().get(j).equals(auxChild.getName())) {
						DefaultMutableTreeNode predecessor = getDefaultMutableTreeNode(xmlTree,
								parent.getInternalCouplingFirts().get(j));
						DefaultMutableTreeNode successor = getDefaultMutableTreeNode(xmlTree,
								parent.getInternalCouplingSecond().get(j));
						ArrayList<String> result = getNamePath(xmlTree, predecessor, successor, currentComponent,
								currentNode, "INPUT");
						if (Integer.parseInt(result.get(1))!=-1) {
							currentComponent.getInputPorts().set(Integer.parseInt(result.get(1)),
									"peip" + result.get(0));
						}
					}
				}
				auxChild = parent;
				auxCurrentNode = parentData;

			} else {
				haveParent = false;
			}

		}
		if (currentComponent.isStart()) {
			for (int j = 0; j < PATH.getFirst().size(); j++) {
				if (PATH.getFirst().get(j) == null) {
					currentComponent.getInputPorts().set(getIndexToExternalCoupling(xmlTree, PATH.getSecond().get(j),
							currentComponent, currentNode, "INPUT"), "peip" + PATH.getPort().get(j));
				}
			}
		}

	}

	/**
	 * Create output port array to currentComponent
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private static void getOutputPort(JTree xmlTree, Component currentComponent, DefaultMutableTreeNode currentNode) {
		boolean haveParent = true;

		for (int j = 0; j < currentComponent.getExternalOutputCoupling().size(); j++) {
			currentComponent.getOutputPorts().add(null);
		}

		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;

		while (haveParent) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) auxCurrentNode.getParent();
			if (parentData != null) {
				Component parent = (Component) parentData.getUserObject();
				for (int j = 0; j < parent.getInternalCouplingFirts().size(); j++) {
					if (parent.getInternalCouplingFirts().get(j).equals(auxChild.getName())) {
						DefaultMutableTreeNode predecessor = getDefaultMutableTreeNode(xmlTree,
								parent.getInternalCouplingFirts().get(j));
						DefaultMutableTreeNode successor = getDefaultMutableTreeNode(xmlTree,
								parent.getInternalCouplingSecond().get(j));
						ArrayList<String> result = getNamePath(xmlTree, predecessor, successor, currentComponent,
								currentNode, "OUTPUT");
						if (Integer.parseInt(result.get(1))!=-1) {
							currentComponent.getOutputPorts().set(Integer.parseInt(result.get(1)),
									"seop" + result.get(0));
						}
					}
				}
				auxChild = parent;
				auxCurrentNode = parentData;
			} else {
				haveParent = false;
			}

		}

		if (currentComponent.isEnd()) {
			for (int j = 0; j < PATH.getSecond().size(); j++) {
				if (PATH.getSecond().get(j) == null) {
					currentComponent.getOutputPorts().set(getIndexToExternalCoupling(xmlTree, PATH.getFirst().get(j),
							currentComponent, currentNode, "OUTPUT"), "seop" + PATH.getPort().get(j));
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
	 * @param firstNodeData
	 * @param SecondNode
	 * @return
	 */
	private static String getNameRelation(Component currentComponent, JTree xmlTree,
			DefaultMutableTreeNode firstNodeData, String SecondNode) {

		// The array will contain the responsibilities that are within inside of
		// currentComponent
		ArrayList<Node> internalResponsibilities = new ArrayList<Node>(); // puede
																			// tener
																			// tambien
																			// and
																			// y
																			// or

		Transformer.Domain.Node firstNode = (Transformer.Domain.Node) firstNodeData.getUserObject();

		if (firstNode instanceof Transformer.Domain.Component) {
			Component firstComponent = (Component) firstNode;
			getInternalResponsibilities(firstNodeData, xmlTree, internalResponsibilities);
		} else if (firstNode instanceof Transformer.Domain.Responsibility) {
			Responsibility firstResponsibility = (Responsibility) firstNode;
			internalResponsibilities.add(firstResponsibility);
		} else if (firstNode instanceof Transformer.Domain.Element) {
			Element firstElement = (Element) firstNode;
			internalResponsibilities.add(firstElement);
		}

		// The process consists of finding the two responsibilities that connect
		// the firstNode to the SecondNode, and thus return the name of the
		// relationship between those two responsibilities, since it is also the
		// name of the relationship between those components

		// It goes through each internal responsibility until it finds the one
		// that is associated with the corresponding responsibility of
		// secondNode
		for (int i = 0; i < internalResponsibilities.size(); i++) {
			int countSuccessor = 0; // ver, es para cambiar el sucesor en and y
									// or

			DefaultMutableTreeNode nextNodeData;
			String currentSuccessor = "";
			ArrayList<String> successors = null;

			Node node = internalResponsibilities.get(i);
			// TODO join and
			if (node instanceof Transformer.Domain.Element) {
				Element element = (Element) node;
				successors = element.getSuccessor();
			}
			if (node instanceof Transformer.Domain.Responsibility) {
				Responsibility responsability = (Responsibility) node;
				successors = responsability.getSuccessor();
			}
			for (int l = 0; l < successors.size(); l++) {
				boolean haveParent = true;
				if (node instanceof Transformer.Domain.Element) {
					currentSuccessor = successors.get(countSuccessor);
					countSuccessor++;
				}
				if (node instanceof Transformer.Domain.Responsibility) {
					currentSuccessor = successors.get(0);
				}
				nextNodeData = getDefaultMutableTreeNode(xmlTree, currentSuccessor);
				if (nextNodeData != null) {
					while (haveParent) {
						if (nextNodeData.getParent() != null) {
							Transformer.Domain.Node nextNode = (Transformer.Domain.Node) nextNodeData.getUserObject();
							if (nextNode.getName().equals(SecondNode)) {
								for (int j = 0; j < PATH.getFirst().size(); j++) {
									// To obtain the name it looks for a
									// relation of
									// the way between firstNode and secondNode,
									// and
									// looks in name assigned to the same one
									if (PATH.getFirst().get(j) != null && PATH.getFirst().get(j).equals(node.getName())
											&& PATH.getSecond().get(j).equals(currentSuccessor)) {
										// Between a firstNode and secondNode
										// there
										// may be more than one relationship,
										// this
										// checks that the name that is returned
										// has
										// not been used before
										boolean esDiferente = true;
										for (int k = 0; k < currentComponent.getInternalCouplingName().size(); k++) {
											if (PATH.getPort().get(j)
													.equals(currentComponent.getInternalCouplingName().get(k))) {
												esDiferente = false;
											}
										}
										if (esDiferente) {
											return PATH.getPort().get(j);
										}
									}
								}
							}
							// Gets the parent of the nextNode, and if it is
							// equal
							// to secondNode it returns the name of the relation
							DefaultMutableTreeNode nextParent = (DefaultMutableTreeNode) nextNodeData.getParent();
							Transformer.Domain.Node nextParentNode = (Transformer.Domain.Node) nextParent
									.getUserObject();
							nextNodeData = nextParent;
							nextNode = nextParentNode;
						} else {
							haveParent = false;
						}

					}
				}
			}
		}

		return null;
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

	public static ArrayList<String> getNamePath(JTree xmlTree, DefaultMutableTreeNode predecessor,
			DefaultMutableTreeNode successor, Component currentComponent, DefaultMutableTreeNode currentNode,
			String externalCoupling) {
		ArrayList<String> result = new ArrayList<String>(); // la primera
															// posición
															// devolverá el
															// índice, la
															// segunda el nombre
															// del puerto

		ArrayList<Node> predecessorCS = new ArrayList<Node>();
		ArrayList<Node> successorCS = new ArrayList<Node>();

		Transformer.Domain.Node predecessorNode = (Transformer.Domain.Node) predecessor.getUserObject();
		Transformer.Domain.Node successorNode = (Transformer.Domain.Node) successor.getUserObject();

		if (predecessorNode instanceof Transformer.Domain.Component) {
			getInternalResponsibilities(predecessor, xmlTree, predecessorCS);
		} else if (predecessorNode instanceof Transformer.Domain.Responsibility) {
			Responsibility firstResponsibility = (Responsibility) predecessorNode;
			predecessorCS.add(firstResponsibility);
		} else if (predecessorNode instanceof Transformer.Domain.Element) {
			Element firstElement = (Element) predecessorNode;
			predecessorCS.add(firstElement);
		}

		if (successorNode instanceof Transformer.Domain.Component) {
			getInternalResponsibilities(successor, xmlTree, successorCS);
		} else if (successorNode instanceof Transformer.Domain.Responsibility) {
			Responsibility responsability = (Responsibility) successorNode;
			successorCS.add(responsability);
		} else if (successorNode instanceof Transformer.Domain.Element) {
			Element element = (Element) successorNode;
			successorCS.add(element);
		}

		for (int j = 0; j < PATH.getFirst().size(); j++) {
			for (int k = 0; k < predecessorCS.size(); k++) {
				for (int i = 0; i < successorCS.size(); i++) {
					if (PATH.getFirst().get(j) != null && PATH.getSecond().get(j) != null
							&& PATH.getFirst().get(j).equals(predecessorCS.get(k).getName())
							&& PATH.getSecond().get(j).equals(successorCS.get(i).getName())) {
						// hasta este punto encontro elementos del camino que
						// los conectaba, ahora busca darle un nombre
						// asegurandose de que este no fue usado
						Boolean exist = false;
						if (externalCoupling.equals("INPUT")) {
							for (int q = 0; q < currentComponent.getInputPorts().size(); q++) {
								if (currentComponent.getInputPorts().get(q) != null && currentComponent.getInputPorts()
										.get(q).equals("peip" + PATH.getPort().get(j))) {
									exist = true;
								}
							}
							if (!exist) {
								result.add(String.valueOf(PATH.getPort().get(j)));
								result.add(String.valueOf(getIndexToExternalCoupling(xmlTree, PATH.getSecond().get(j),
										currentComponent, currentNode, externalCoupling)));
								return result;
							}
						} else {
							for (int q = 0; q < currentComponent.getOutputPorts().size(); q++) {
								if (currentComponent.getOutputPorts().get(q) != null && currentComponent
										.getOutputPorts().get(q).equals("seop" + PATH.getPort().get(j))) {
									exist = true;
								}
							}
							if (!exist) {
								result.add(String.valueOf(PATH.getPort().get(j)));
								result.add(String.valueOf(getIndexToExternalCoupling(xmlTree, PATH.getFirst().get(j),
										currentComponent, currentNode, externalCoupling)));
								return result;
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static int getIndexToExternalCoupling(JTree xmlTree, String name, Component currentComponent,
			DefaultMutableTreeNode currentNode, String externalCoupling) {
		boolean haveParent = true;

		// DefaultMutableTreeNode auxCurrentNode = currentNode;

		DefaultMutableTreeNode firstNode = getDefaultMutableTreeNode(xmlTree, name);
		// Component nameComponent = (Transformer.Domain.Node)
		// firstNode.getUserObject();
		Transformer.Domain.Node auxChild = (Transformer.Domain.Node) firstNode.getUserObject();

		while (haveParent) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) firstNode.getParent();
			if (parentData != null) {
				Transformer.Domain.Node parent = (Transformer.Domain.Node) parentData.getUserObject();

				if (externalCoupling.equals("INPUT")) {
					for (int q = 0; q < currentComponent.getExternalInputCoupling().size(); q++) {
						if (currentComponent.getExternalInputCoupling().get(q).equals(auxChild.getName())) {
							return q;
						}
					}
				} else {
					for (int q = 0; q < currentComponent.getExternalOutputCoupling().size(); q++) {
						if (currentComponent.getExternalOutputCoupling().get(q).equals(auxChild.getName())) {
							return q;
						}
					}
				}

				auxChild = parent;
				firstNode = parentData;

			} else {
				haveParent = false;
			}

		}

		return -1;
	}

	/*
	 * public static int getIndexToExternalOutputCoupling(String name, Component
	 * currentComponent) { for (int q = 0; q <
	 * currentComponent.getExternalOutputCoupling().size(); q++) { if
	 * (currentComponent.getExternalOutputCoupling().get(q).equals(name)) {
	 * return q; } } return -1; }
	 */

	/**
	 * Gets all responsibilities that are inside a component
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @param xmlTree
	 * @param internalResponsibilities
	 */
	private static void getInternalResponsibilities(DefaultMutableTreeNode currentNode, JTree xmlTree,
			ArrayList<Node> internalResponsibilities) {

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
				Node nodeDataComponent = (Node) nodeData;
				internalResponsibilities.add(nodeDataComponent);
			}
		}
	}

}