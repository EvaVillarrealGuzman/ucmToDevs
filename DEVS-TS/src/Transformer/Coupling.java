package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.codemodel.JMethod;

import Transformer.Domain.Component;
import Transformer.Domain.Element;
import Transformer.Domain.Node;
import Transformer.Domain.Relation;

/**
 * Esta clase tiene la responsabilidad de crear los acoplamientos
 * 
 * @author Usuario-Pc
 *
 */
public class Coupling {

	/**
	 * Crea el acoplamiento dentro de una clase
	 * 
	 * @param currentComponent
	 * @param method
	 */
	public static void createCoupling(JTree xmlTree, Component currentComponent, DefaultMutableTreeNode currentNode,
			JMethod method) {

		couplingInputExternal(xmlTree, currentComponent, currentNode, method);
		couplingOuputExternal(xmlTree, currentComponent, currentNode, method);
		couplingInternal(xmlTree, currentComponent, method);
		experimentalFrameCoupling(xmlTree, currentComponent, method);

	}

	/**
	 * crea el acoplamiento interno
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param method
	 */
	private static void couplingInternal(JTree xmlTree, Component currentComponent, JMethod method) {

		ArrayList<String> icf = currentComponent.getInternalCouplingFirts();
		ArrayList<String> icn = currentComponent.getInternalCouplingName();
		ArrayList<String> ics = currentComponent.getInternalCouplingSecond();

		int indexORAND = 1;

		for (int j = 0; j < icf.size(); j++) {

			DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(xmlTree, ics.get(j));
			Transformer.Domain.Node dataNextNode = (Node) nextNode.getUserObject();

			String temporalName = dataNextNode.getName();
			temporalName = temporalName.replace(" ", "");
			String nextObjectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			DefaultMutableTreeNode beforeNode = getDefaultMutableTreeNode(xmlTree, icf.get(j));
			Transformer.Domain.Node dataBeforeNode = (Node) beforeNode.getUserObject();

			temporalName = dataBeforeNode.getName();
			temporalName = temporalName.replace(" ", "");
			String beforeOjectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			String beforePort = "";
			String nextPort = "";

			if (dataBeforeNode instanceof Transformer.Domain.Element) {
				Transformer.Domain.Element dataBeforeNodeElement = (Element) dataBeforeNode;
				if (dataBeforeNodeElement.getType().equals("ucm.map:AndFork")) {
					beforePort = " pathout" + indexORAND;
					indexORAND++;
				} else if (dataBeforeNodeElement.getType().equals("ucm.map:AndJoin")) {
					beforePort = " pathout";
				} else if (dataBeforeNodeElement.getType().equals("ucm.map:OrJoin")) {
					beforePort = " pathout";
				} else if (dataBeforeNodeElement.getType().equals("ucm.map:OrFork")) {
					beforePort = " pathout" + indexORAND;
					indexORAND++;
				}
			} else if (dataBeforeNode instanceof Transformer.Domain.Responsibility) {
				beforePort = "\"srop\"";
			} else if (dataBeforeNode instanceof Component) {
				beforePort = "\"seop" + icn.get(j) + "\"";
			}

			if (dataNextNode instanceof Transformer.Domain.Element) {
				Transformer.Domain.Element dataNextNodeElement = (Element) dataNextNode;
				if (dataNextNodeElement.getType().equals("ucm.map:AndFork")) {
					nextPort = " pathin";
				} else if (dataNextNodeElement.getType().equals("ucm.map:AndJoin")) {
					nextPort = " pathin" + indexORAND;
					indexORAND++;
				} else if (dataNextNodeElement.getType().equals("ucm.map:OrJoin")) {
					nextPort = " pathin" + indexORAND;
					indexORAND++;
				} else if (dataNextNodeElement.getType().equals("ucm.map:OrFork")) {
					nextPort = " pathin";
				}
			} else if (dataNextNode instanceof Transformer.Domain.Responsibility) {
				nextPort = "\"prip\"";
			} else if (dataNextNode instanceof Component) {
				nextPort = "\"peip" + icn.get(j) + "\"";
			}

			method.body().directStatement(
					"addCoupling(" + beforeOjectName + "," + beforePort + "," + nextObjectName + "," + nextPort + ");");

		}
	}

	/**
	 * crea el acoplamiento externo de salida
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param currentNode
	 * @param method
	 */
	private static void couplingOuputExternal(JTree xmlTree, Component currentComponent,
			DefaultMutableTreeNode currentNode, JMethod method) {

		ArrayList<String> eoc = currentComponent.getExternalOutputCoupling();

		int indexORAND = 1;

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		for (int j = 0; j < eoc.size(); j++) {
			DefaultMutableTreeNode beforeNode = getDefaultMutableTreeNode(xmlTree, eoc.get(j));
			Transformer.Domain.Node dataBeforeNode = (Node) beforeNode.getUserObject();

			String temporalName = dataBeforeNode.getName();
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			if (dataBeforeNode instanceof Transformer.Domain.Element) {
				Transformer.Domain.Element dataNextNodeElement = (Element) dataBeforeNode;
				if (dataNextNodeElement.getType().equals("ucm.map:AndFork")
						|| dataNextNodeElement.getType().equals("ucm.map:OrFork")) {
					method.body().directStatement("addCoupling(" + objectName + ",\"pathout" + indexORAND + "\",this,\""
							+ currentComponent.getOutputPorts().get(j) + "\");");
					indexORAND++;
				} else if (dataNextNodeElement.getType().equals("ucm.map:AndJoin")
						|| dataNextNodeElement.getType().equals("ucm.map:OrJoin")) {
					method.body().directStatement("addCoupling(" + objectName + ",\"pathout\",this,\""
							+ currentComponent.getOutputPorts().get(j) + "\");");
				}
			} else if (dataBeforeNode instanceof Transformer.Domain.Responsibility) {
				method.body().directStatement("addCoupling(" + objectName + ",\"srop\",this,\""
						+ currentComponent.getOutputPorts().get(j) + "\");");
			} else if (dataBeforeNode instanceof Component) {
				method.body()
						.directStatement("addCoupling(" + objectName + ",\""
								+ currentComponent.getOutputRelations()
										.get(searchIndexOutput(currentComponent, dataBeforeNode.getName())).getPort()
								+ "\" ,this,\"" + currentComponent.getOutputPorts().get(j) + "\");");
			}

			// le dice a su padre cuales son sus puertos
			if (currentNode != root) {
				Relation relation = new Relation(currentComponent.getName(), currentComponent.getOutputPorts().get(j));
				getParent(currentNode).getOutputRelations().add(relation);
			}

		}
	}

	/**
	 * crea el acoplamiento externo de entrada
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param currentNode
	 * @param method
	 */
	private static void couplingInputExternal(JTree xmlTree, Component currentComponent,
			DefaultMutableTreeNode currentNode, JMethod method) {
		ArrayList<String> eic = currentComponent.getExternalInputCoupling();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		String temporalName;
		int indexORAND = 1;

		for (int j = 0; j < eic.size(); j++) {
			DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(xmlTree, eic.get(j));
			Transformer.Domain.Node dataNextNode = (Node) nextNode.getUserObject();

			temporalName = dataNextNode.getName();
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			if (dataNextNode instanceof Transformer.Domain.Element) {
				Transformer.Domain.Element dataNextNodeElement = (Element) dataNextNode;
				if (dataNextNodeElement.getType().equals("ucm.map:AndFork")
						|| dataNextNodeElement.getType().equals("ucm.map:OrFork")) {
					method.body().directStatement("addCoupling(this,\"" + currentComponent.getInputPorts().get(j)
							+ "\"," + objectName + ",\"pathin\");");
				} else if (dataNextNodeElement.getType().equals("ucm.map:AndJoin")
						|| dataNextNodeElement.getType().equals("ucm.map:OrJoin")) {
					method.body().directStatement("addCoupling(this,\"" + currentComponent.getInputPorts().get(j)
							+ "\"," + objectName + ",\"pathin" + indexORAND + "\");");
					indexORAND++;
				}
			} else if (dataNextNode instanceof Transformer.Domain.Responsibility) {
				method.body().directStatement("addCoupling(this,\"" + currentComponent.getInputPorts().get(j) + "\","
						+ objectName + ",\"prip\");");
			} else if (dataNextNode instanceof Component) {
				method.body().directStatement("addCoupling(this,\""
						+ currentComponent.getInputPorts().get(j) + "\"," + objectName + ", \"" + currentComponent
								.getInputRelations().get(searchIndexInput(currentComponent, objectName)).getPort()
						+ "\");");
			}

			// le dice a su padre cuales son sus puertos
			if (currentNode != root) {
				Relation relation = new Relation(currentComponent.getName(), currentComponent.getInputPorts().get(j));
				getParent(currentNode).getInputRelations().add(relation);
			}

		}
	}

	/**
	 * crea el acoplamiento con el marco experimental
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param method
	 */
	private static void experimentalFrameCoupling(JTree xmlTree, Component currentComponent, JMethod method) {

		ArrayList<String> ic = currentComponent.getInternalComponents();

		String taop = "\"taop\");";
		String dtop = "\"dtop\");";
		String rtop = "\"rtop\");";
		String failop = "\"failop\");";

		if (method.name().equals("saViewConstruct")) {
			taop = "\"rtaop\");";
			dtop = "\"rdtop\");";
			rtop = "\"rrtop\");";
			failop = "\"rfailop\");";
		}

		for (int j = 0; j < ic.size(); j++) {
			DefaultMutableTreeNode node = getDefaultMutableTreeNode(xmlTree, ic.get(j));
			Transformer.Domain.Node dataNode = (Node) node.getUserObject();

			String temporalName = currentComponent.getInternalComponents().get(j);
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			if (dataNode instanceof Transformer.Domain.Element) {
				Transformer.Domain.Element dataNodeElement = (Element) dataNode;
				if (!dataNodeElement.getType().equals("ucm.map:AndFork")
						&& !dataNodeElement.getType().equals("ucm.map:AndJoin")
						&& !dataNodeElement.getType().equals("ucm.map:OrFork")
						&& !dataNodeElement.getType().equals("ucm.map:OrJoin")) {

					method.body().directStatement("addCoupling(" + objectName + ",\"taop\",this," + taop);
					method.body().directStatement("addCoupling(" + objectName + ",\"dtop\",this," + dtop);
					method.body().directStatement("addCoupling(" + objectName + ",\"rtop\",this," + rtop);
					method.body().directStatement("addCoupling(" + objectName + ",\"failop\",this," + failop);
				}
			} else {
				method.body().directStatement("addCoupling(" + objectName + ",\"taop\",this," + taop);
				method.body().directStatement("addCoupling(" + objectName + ",\"dtop\",this," + dtop);
				method.body().directStatement("addCoupling(" + objectName + ",\"rtop\",this," + rtop);
				method.body().directStatement("addCoupling(" + objectName + ",\"failop\",this," + failop);
			}
		}
	}

	/**
	 * devuelve el padre de un elemento
	 * 
	 * @param currentNode
	 * @return
	 */
	private static Component getParent(DefaultMutableTreeNode currentNode) {

		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentNode.getParent();
		return (Component) parent.getUserObject();

	}

	/**
	 * Devuelve el índice dentro del array de entrada que corresponde con name
	 * 
	 * @param currentComponent
	 * @param name
	 * @return
	 */
	private static int searchIndexInput(Component currentComponent, String name) {
		for (int j = 0; j < currentComponent.getInputRelations().size(); j++) {
			if (currentComponent.getInputRelations().get(j).getElement().equals(name)) {
				return j;
			}
		}
		return 0;
	}

	/**
	 * Devuelve el índice dentro del array de salida que corresponde con name
	 * 
	 * @param currentComponent
	 * @param name
	 * @return
	 */
	private static int searchIndexOutput(Component currentComponent, String name) {
		for (int j = 0; j < currentComponent.getOutputRelations().size(); j++) {
			if (currentComponent.getOutputRelations().get(j).getElement().equals(name)) {
				return j;
			}
		}

		return 0;
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
