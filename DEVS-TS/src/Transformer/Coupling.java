package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.codemodel.JMethod;

import Transformer.Domain.Component;
import Transformer.Domain.Element;
import Transformer.Domain.Node;
import Transformer.Domain.Path;

/**
 * This class has the responsibility of creating couplings
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class Coupling {

	/**
	 * Create the couplings inside a class
	 * 
	 * @param currentComponent
	 * @param method
	 */
	public static void createCoupling(JTree xmlTree, Component currentComponent, DefaultMutableTreeNode currentNode,
			JMethod method, Path relation) {

		couplingInputExternal(xmlTree, currentComponent, method, relation);
		couplingOuputExternal(xmlTree, currentComponent, method, relation);
		couplingInternal(xmlTree, currentComponent, method);
		experimentalFrameCoupling(xmlTree, currentComponent, method);

	}

	/**
	 * create internal coupling
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param method
	 */
	private static void couplingInternal(JTree xmlTree, Component currentComponent, JMethod method) {

		ArrayList<String> icf = currentComponent.getInternalCouplingFirts();
		ArrayList<String> icn = currentComponent.getInternalCouplingName();
		ArrayList<String> ics = currentComponent.getInternalCouplingSecond();

		// TODO
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
				// TODO
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
	 * create output external coupling
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param method
	 */
	private static void couplingOuputExternal(JTree xmlTree, Component currentComponent, JMethod method, Path path) {

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

			// TODO ver
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
				method.body().directStatement("addCoupling(" + objectName + ",\"srop\",this,\"seop"
						+ getNamePortFirst(temporalName, path) + "\");");
			} else if (dataBeforeNode instanceof Component) {
				method.body()
						.directStatement("addCoupling(" + objectName + ",\""
								+ dataBeforeNode.getOutputPorts()
										.get(searchIndexOutput(currentComponent, dataBeforeNode))
								+ "\" ,this,\"" + currentComponent.getOutputPorts().get(j) + "\");");
			}

		}
	}

	/**
	 * Gets the port name of the corresponding responsibility
	 * 
	 * @param responsibility
	 * @param path
	 * @return
	 */
	private static String getNamePortFirst(String responsibility, Path path) {
		for (int j = 0; j < path.getFirst().size(); j++) {
			if ((path.getSecond().get(j) != null) && path.getFirst().get(j).equals(responsibility)) {
				return path.getPort().get(j);
			}
		}
		return "0";
	}

	/**
	 * Gets the port name of the corresponding responsibility
	 * 
	 * @param responsibility
	 * @param path
	 * @return
	 */
	private static String getNamePortSecond(String responsibility, Path path) {
		for (int j = 0; j < path.getFirst().size(); j++) {
			if ((path.getSecond().get(j) != null) && path.getSecond().get(j).equals(responsibility)) {
				return path.getPort().get(j);
			}
		}
		return "0";
	}

	/**
	 * Creates the input external coupling
	 * 
	 * @param xmlTree
	 * @param currentComponent
	 * @param method
	 */
	private static void couplingInputExternal(JTree xmlTree, Component currentComponent, JMethod method, Path path) {
		ArrayList<String> eic = currentComponent.getExternalInputCoupling();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		String temporalName;
		// TODO
		int indexORAND = 1;

		for (int j = 0; j < eic.size(); j++) {
			DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(xmlTree, eic.get(j));
			Transformer.Domain.Node dataNextNode = (Node) nextNode.getUserObject();

			temporalName = dataNextNode.getName();
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			if (dataNextNode instanceof Transformer.Domain.Element) {
				// TODO
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
				method.body().directStatement("addCoupling(this,\"peip" + getNamePortSecond(temporalName, path) + "\","
						+ objectName + ",\"prip\");");
			} else if (dataNextNode instanceof Component) {
				method.body().directStatement("addCoupling(this,\"" + currentComponent.getInputPorts().get(j) + "\","
						+ objectName + ", \""
						+ dataNextNode.getInputPorts().get(searchIndexInput(currentComponent, dataNextNode)) + "\");");
			}

		}
	}

	/**
	 * Creates the coupling with the experimental frame
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
	 * Returns element´s parent
	 * 
	 * @param currentNode
	 * @return
	 */
	private static Component getParent(DefaultMutableTreeNode currentNode) {

		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentNode.getParent();
		return (Component) parent.getUserObject();

	}

	/**
	 * Returns the index within the input array that corresponds to name
	 * 
	 * @param currentComponent
	 * @return
	 */
	private static int searchIndexInput(Component currentComponent, Transformer.Domain.Node dataNextNode) {

		for (int j = 0; j < dataNextNode.getInputPorts().size(); j++) {
			boolean isEqual = false;
			for (int i = 0; i < currentComponent.getInternalCouplingName().size(); i++) {
				System.out.println(dataNextNode.getInputPorts().get(j));
				System.out.println("peip" + currentComponent.getInternalCouplingName().get(i));
				if (dataNextNode.getInputPorts().get(j)
						.equals("peip" + currentComponent.getInternalCouplingName().get(i))) {
					isEqual = true;
				}
			}
			if (!isEqual) {
				return j;
			}
		}
		return 0;
	}

	/**
	 * Returns the index within the output array that corresponds to name
	 * 
	 * @param currentComponent
	 * @return
	 */
	private static int searchIndexOutput(Component currentComponent, Transformer.Domain.Node dataBeforeNode) {

		for (int j = 0; j < dataBeforeNode.getOutputPorts().size(); j++) {
			boolean isEqual = false;
			for (int i = 0; i < currentComponent.getInternalCouplingName().size(); i++) {
				System.out.println(dataBeforeNode.getOutputPorts().get(j));
				System.out.println("seop" + currentComponent.getInternalCouplingName().get(i));
				if (dataBeforeNode.getOutputPorts().get(j)
						.equals("seop" + currentComponent.getInternalCouplingName().get(i))) {
					isEqual = true;
				}
			}
			if (!isEqual) {
				return j;
			}
		}

		return 0;
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

}
