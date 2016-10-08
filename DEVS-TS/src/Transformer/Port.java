package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import Transformer.Domain.Component;

/**
 * Esta clase tiene la responsabilidad de crear los puertos
 * 
 * @author Usuario-Pc
 *
 */
public class Port {
	// TODO ver de colocar bien desde donde se llama

	/**
	 * Se encarga de crear los puertos de todos los elementos posibles
	 * 
	 * @param xmlTree
	 */
	public static void createPort(JTree xmlTree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		Component root = (Component) rootNode.getUserObject();

		createPortRoot(xmlTree, root);
		createPortComponent(xmlTree, rootNode, root);
		createPortResponsability(xmlTree);
		createPortElementOrAnd(xmlTree);
	}

	/**
	 * Crea los puertos de entrada y salida de la vista
	 * 
	 * @param xmlTree
	 * @param root
	 */
	private static void createPortRoot(JTree xmlTree, Component root) {
		root.getInputPorts().add("erip");
		root.getOutputPorts().add("esop");
	}

	/**
	 * Crea los puertos de dentrada y salida de los componentes
	 * 
	 * @param xmlTree
	 * @param currentNode
	 * @param currentComponent
	 */
	private static void createPortComponent(JTree xmlTree, DefaultMutableTreeNode currentNode,
			Component currentComponent) {
		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node domainNode = (Transformer.Domain.Node) node.getUserObject();

			if (domainNode instanceof Component) {
				Component component = (Component) domainNode;
				for (int j = 0; j < getInputPort(component, node).size(); j++) {
					component.getInputPorts().add("peip" + getInputPort(component, node).get(j));
				}
				for (int j = 0; j < getOutputPort(component, node).size(); j++) {
					component.getOutputPorts().add("seop" + getOutputPort(component, node).get(j));
				}
				createPortComponent(xmlTree, node, component);
			}
		}
	}

	/**
	 * Crea los puertos de dentrada y salida de las responsabilidades
	 * 
	 * @param xmlTree
	 */
	private static void createPortResponsability(JTree xmlTree) {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Responsibility) {
					domainNode.getInputPorts().add("prip");
					domainNode.getOutputPorts().add("srop");
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
	 * 
	 * @param currentComponent
	 * @param currentNode
	 * @return
	 */
	private static ArrayList<String> getInputPort(Component currentComponent, DefaultMutableTreeNode currentNode) {
		ArrayList<String> inputPorts = new ArrayList<String>();
		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = currentComponent;

		while (inputPorts.size() < 1) {
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
				// Se llega a este punto, cuando es el primer o último elemento
				// del camino
				inputPorts.add("0");
			}

		}
		return inputPorts;
	}

	private static ArrayList<String> getOutputPort(Component child, DefaultMutableTreeNode currentNode) {
		ArrayList<String> outputPorts = new ArrayList<String>();
		DefaultMutableTreeNode auxCurrentNode = currentNode;
		Component auxChild = child;

		while (outputPorts.size() < 1) {
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
				outputPorts.add("0");
			}

		}
		return outputPorts;
	}

}
