package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import Transformer.Domain.Component;
import Transformer.Domain.Relation2;
import Transformer.Domain.Responsibility;

/**
 * Esta clase tiene la responsabilidad de crear los puertos
 * 
 * @author Usuario-Pc
 *
 */
public class Port {
	// TODO ver de colocar bien desde donde se llama
	boolean band = true;

	Relation2 relation;

	/**
	 * Se encarga de crear los puertos de todos los elementos posibles
	 * 
	 * @param xmlTree
	 */
	public void createPort(JTree xmlTree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		Component root = (Component) rootNode.getUserObject();

		createPortRoot(xmlTree, root);
		createPortResponsability(xmlTree);
		createPortComponent(xmlTree, rootNode, root);
		createPortElementOrAnd(xmlTree);
	}

	/**
	 * Crea los puertos de entrada y salida de la vista
	 * 
	 * @param xmlTree
	 * @param root
	 */
	private void createPortRoot(JTree xmlTree, Component root) {
		root.getInputPorts().add("erip");
		root.getOutputPorts().add("esop");
	}

	/**
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private static ArrayList<String> getInputPort(Component currentComponent, DefaultMutableTreeNode currentNode) {
		boolean band = true;
		
		ArrayList<String> inputPorts = new ArrayList<String>();
		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;



		while (inputPorts.size() < 1 && band) {
			DefaultMutableTreeNode parentData = (DefaultMutableTreeNode) auxCurrentNode.getParent();
			if (parentData != null) {
				Component parent = (Component) parentData.getUserObject();
				for (int j = 0; j < parent.getInternalCouplingSecond().size(); j++) {
					if (parent.getInternalCouplingSecond().get(j).equals(auxChild.getName())) {
						//Verifica si es el primer elemento del camino
						
						if (parent.getInternalCouplingSecond().get(j).equals("ucm.map:StartPoint")) {
							inputPorts.add(parent.getInternalCouplingName().get(j - 1));
						} else {
							inputPorts.add(parent.getInternalCouplingName().get(j));
						}
					}
				}
				auxChild = parent;
				auxCurrentNode = parentData;
			
			} else {
				band = false;
			}

		}
		
		
		if (currentComponent.isStart()){
			inputPorts.add("0");
		}
		return inputPorts;
	}

	/**
	 * Crea los puertos de dentrada y salida de los componentes
	 * 
	 * @param xmlTree
	 * @param currentNode
	 * @param currentComponent
	 */
	private void createPortComponent(JTree xmlTree, DefaultMutableTreeNode currentNode, Component currentComponent) {

		System.out.println(currentComponent.getName());

		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node domainNode = (Transformer.Domain.Node) node.getUserObject();

			if (domainNode instanceof Component) {
				Component component = (Component) domainNode;
				for (int j = 0; j < currentComponent.getInternalCouplingFirts().size(); j++) {
					if (!(currentComponent.getInternalCouplingFirts().get(j)).equals("ucm.map:StartPoint")
							&& !(currentComponent.getInternalCouplingSecond().get(j).equals("ucm.map:EndPoint"))) {
						DefaultMutableTreeNode firstNode = getDefaultMutableTreeNode(xmlTree,
								currentComponent.getInternalCouplingFirts().get(j));
						currentComponent.getInternalCouplingName().add(this.setICN(currentComponent, currentNode,
								xmlTree, firstNode, currentComponent.getInternalCouplingSecond().get(j)));
					}
				}
				for (int j = 0; j < getInputPort(component, node).size(); j++) {
					component.getInputPorts().add("peip" + getInputPort(component, node).get(j));
				}
				for (int j = 0; j < getOutputPort(component, node).size(); j++) {
					component.getOutputPorts().add("seop" + getOutputPort(component, node).get(j));
				}
				createPortComponent(xmlTree, node, component);
			}
		}

		/*
		 * if (band &&
		 * currentComponent.getName().equals("GUISoftwareArchitectureEvaluation"
		 * )) { currentComponent.getInputPorts().add("peip" + 0);
		 * currentComponent.getOutputPorts().add("seop" + 0); band = false; }
		 */
	}

	/**
	 * Crea los puertos de dentrada y salida de las responsabilidades
	 * 
	 * @param xmlTree
	 */
	private void createPortResponsability(JTree xmlTree) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		int count = 1;

		relation = new Relation2();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Responsibility) {
					domainNode.getInputPorts().add("prip");
					domainNode.getOutputPorts().add("srop");
					relation.getFirst().add(domainNode.getName());
					// TODO ampliar no solo a 0
					relation.getSecond().add(((Transformer.Domain.Responsibility) domainNode).getSuccessor().get(0));
					relation.getPort().add(String.valueOf(count));
					count++;
				}
			}
		}
	}

	/**
	 * Crea los puertos de dentrada y salida de los elementos and y or del
	 * camino
	 * 
	 * @param xmlTree
	 */
	private void createPortElementOrAnd(JTree xmlTree) {

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
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private String setICN(Component currentComponent, DefaultMutableTreeNode currentNode, JTree xmlTree,
			DefaultMutableTreeNode firstNode, String SecondNode) {
		ArrayList<String> inputPorts = new ArrayList<String>();
		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;

		ArrayList<Responsibility> externalResponsibilities = new ArrayList<Responsibility>();

		Component firstComponent = (Component) firstNode.getUserObject();
		getResponsibilities(firstComponent, firstNode, xmlTree, externalResponsibilities);

		for (int i = 0; i < externalResponsibilities.size(); i++) {
			boolean band = true;
			Responsibility responsibility = externalResponsibilities.get(i);
			// TODO join and
			DefaultMutableTreeNode node = getDefaultMutableTreeNode(xmlTree, responsibility.getSuccessor().get(0));
			if (node != null) {
				while (band) {
					if (node.getParent() != null) {
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
						Transformer.Domain.Node parentNode = (Transformer.Domain.Node) parent.getUserObject();
						if (parentNode.getName().equals(SecondNode)) {
							for (int j = 0; j < this.getRelation().getFirst().size(); j++) {
								// TODO join and
								// System.out.println(this.getRelation().getFirst().get(j));
								// System.out.println(this.getRelation().getSecond().get(j));
								// System.out.println(responsibility.getName()
								// );
								if (this.getRelation().getFirst().get(j).equals(responsibility.getName())
										&& this.getRelation().getSecond().get(j)
												.equals(responsibility.getSuccessor().get(0))) {
									boolean esDiferente = true;
									for (int k = 0; k < currentComponent.getInternalCouplingName().size(); k++) {
										if (this.getRelation().getPort().get(j)
												.equals(currentComponent.getInternalCouplingName().get(k))) {
											esDiferente = false;
										}
									}
									if (esDiferente) {
										return this.getRelation().getPort().get(j);
									}
								}
							}
						}
						node = parent;
					} else {
						band = false;
					}

				}
			}
		}
		return null;
	}

	// Obtiene todas las responsabilidades que están dentro de un componente
	private void getResponsibilities(Component currentComponent, DefaultMutableTreeNode currentNode, JTree xmlTree,
			ArrayList<Responsibility> externalResponsibilities) {

		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node nodeData = (Transformer.Domain.Node) node.getUserObject();

			if (nodeData instanceof Transformer.Domain.Component) {
				Transformer.Domain.Component nodeDataComponent = (Component) nodeData;
				getResponsibilities(nodeDataComponent, node, xmlTree, externalResponsibilities);
			} else if (nodeData instanceof Transformer.Domain.Element
					|| nodeData instanceof Transformer.Domain.Responsibility) {
				Responsibility nodeDataComponent = (Responsibility) nodeData;
				externalResponsibilities.add(nodeDataComponent);
			}
		}
	}

	private ArrayList<String> getOutputPort(Component child, DefaultMutableTreeNode currentNode) {
		boolean band = true;
		
		
		ArrayList<String> outputPorts = new ArrayList<String>();
		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = child;
		

		while (outputPorts.size() < 1 && band) {
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
				band=false;
			}

		}
		
		
		if (child.isEnd()){
			outputPorts.add("0");
		}

		
		return outputPorts;
	}

	/*
	 * private void getRelations(JTree xmlTree, DefaultMutableTreeNode
	 * currentNode, Component currentComponent) {
	 * 
	 * ArrayList<String> portsName = ();
	 * 
	 * for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
	 * DefaultMutableTreeNode node = ((DefaultMutableTreeNode)
	 * xmlTree.getModel().getChild(currentNode, i)); Transformer.Domain.Node
	 * domainNode = (Transformer.Domain.Node) node.getUserObject();
	 * 
	 * DefaultMutableTreeNode parentData = (DefaultMutableTreeNode)
	 * currentNode.getParent(); if (parentData != null) { Component parent =
	 * (Component) parentData.getUserObject();
	 * 
	 * currentComponent.getExternalCouplingResponsability().add(domainNode.
	 * getName()); currentComponent.getExternalCouplingResponsability().add();
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * for (int j = 0; j < getInputPort(responsibility, node).size(); j++) {
	 * responsibility.getInputPorts().add("peip" + getInputPort(responsibility,
	 * node).get(j)); } for (int j = 0; j < getOutputPort(responsibility,
	 * node).size(); j++) { responsibility.getOutputPorts().add("seop" +
	 * getOutputPort(responsibility, node).get(j)); }
	 * createPortComponent(xmlTree, node, responsibility); } } }
	 * 
	 * 
	 * public void getPortsName( Component parent, String childName){
	 * ArrayList<String> portsName = new ArrayList<String>();
	 * 
	 * 
	 * for (int i = 0; i < parent.getExternalCouplingSecondPort().size(); i++) {
	 * 
	 * if (parent.getExternalCouplingSecondPort().get(i).equals(childName)){
	 * portsName.add(parent.getInternalCouplingName().get(i)); } } }
	 */

	/**
	 * obtiene la primera responsabilidad del camino
	 * 
	 * @param parent
	 * @param childName
	 */
	public void getFirstChild(Component root) {

	}

	public Relation2 getRelation() {
		return relation;
	}

	public void setRelation(Relation2 relation) {
		this.relation = relation;
	}

	/***
	 * Devuelve el nodo del árbol cuyo nombre se corresponde con name
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

}
