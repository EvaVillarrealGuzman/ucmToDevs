package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.codemodel.JMethod;

import Transformer.Domain.Component;
import Transformer.Domain.Element;
import Transformer.Domain.Node;

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

			if (dataBeforeNode instanceof Transformer.Domain.Responsibility) {
				beforePort = "\"srop\"";
			} else {
				beforePort = "\"seop" + icn.get(j) + "\"";
			}

			if (dataNextNode instanceof Transformer.Domain.Responsibility) {
				nextPort = "\"prip\"";
			} else {
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

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		for (int j = 0; j < eoc.size(); j++) {
			DefaultMutableTreeNode beforeNode = getDefaultMutableTreeNode(xmlTree, eoc.get(j));
			Transformer.Domain.Node dataBeforeNode = (Node) beforeNode.getUserObject();

			String temporalName = dataBeforeNode.getName();
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			method.body()
					.directStatement("addCoupling(" + objectName + ",\""
							+ dataBeforeNode.getOutputPorts()
									.get(searchIndexOutput(currentComponent, dataBeforeNode,
											currentComponent.getOutputPorts().get(j)))
							+ "\" ,this,\"" + currentComponent.getOutputPorts().get(j) + "\");");

		}
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

		for (int j = 0; j < eic.size(); j++) {
			DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(xmlTree, eic.get(j));
			Transformer.Domain.Node dataNextNode = (Node) nextNode.getUserObject();

			temporalName = dataNextNode.getName();
			temporalName = temporalName.replace(" ", "");
			String objectName = (temporalName.substring(0, 1)).toLowerCase()
					+ temporalName.substring(1, temporalName.length());

			String namePort = dataNextNode.getInputPorts()
					.get(searchIndexInput(currentComponent, dataNextNode, currentComponent.getInputPorts().get(j)));
			method.body().directStatement("addCoupling(this,\"" + currentComponent.getInputPorts().get(j) + "\","
					+ objectName + ", \"" + namePort + "\");");
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
					// TODO va acoplamiento para elementos?
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
	 * Returns the index within the input array that corresponds to name
	 * 
	 * @param currentComponent
	 * @return
	 */
	private static int searchIndexInput(Component currentComponent, Transformer.Domain.Node dataNextNode,
			String portNamecurrentComponent) {
		if (dataNextNode instanceof Component) {
			for (int j = 0; j < dataNextNode.getInputPorts().size(); j++) {
				boolean isEqual = false;
				for (int i = 0; i < currentComponent.getInternalCouplingName().size(); i++) {
					if (dataNextNode.getInputPorts().get(j)
							.equals("peip" + currentComponent.getInternalCouplingName().get(i))) {
						isEqual = true;
					}
				}
				if (!isEqual) {
					return j;
				}
			}
		} else if (dataNextNode instanceof Element) {
			int indexCC = -1; // indica desde donde se corta la cadena para
								// current
			// component
			if (portNamecurrentComponent.indexOf("peip") != -1) {
				indexCC = 4;
			}
			for (int i = 0; i < dataNextNode.getInputPorts().size(); i++) {
				int indexNN = -1; // indica desde donde se corta la cadena para
									// next node
				String namePortNextNode = dataNextNode.getInputPorts().get(i);
				if (namePortNextNode.indexOf("peip") != -1) {
					indexNN = 4;
				}
				if (indexNN != -1 && indexCC != -1) {
					String numperPortNextNode = namePortNextNode.substring(indexNN, namePortNextNode.length());
					String numperComponent = portNamecurrentComponent.substring(indexCC, namePortNextNode.length());
					if (numperPortNextNode.equals(numperComponent)) {
						return i;
					}
				}
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
	private static int searchIndexOutput(Component currentComponent, Transformer.Domain.Node dataBeforeNode,
			String portNamecurrentComponent) {

		if (dataBeforeNode instanceof Component) {
			for (int j = 0; j < dataBeforeNode.getOutputPorts().size(); j++) {
				boolean isEqual = false;
				for (int i = 0; i < currentComponent.getInternalCouplingName().size(); i++) {
					if (dataBeforeNode.getOutputPorts().get(j)
							.equals("seop" + currentComponent.getInternalCouplingName().get(i))) {
						isEqual = true;
					}
				}
				if (!isEqual) {
					return j;
				}
			}
		} else if (dataBeforeNode instanceof Element) {
			int indexCC = -1; // indica desde donde se corta la cadena para
			// current
			// component
			if (portNamecurrentComponent.indexOf("seop") != -1) {
				indexCC = 4;
			}
			for (int i = 0; i < dataBeforeNode.getOutputPorts().size(); i++) {
				int indexNN = -1; // indica desde donde se corta la cadena para
				// next node
				String namePortBeforeNode = dataBeforeNode.getOutputPorts().get(i);
				if (namePortBeforeNode.indexOf("seop") != -1) {
					indexNN = 4;
				}
				if (indexNN != -1 && indexCC != -1) {
					String numperPortBeforeNode = namePortBeforeNode.substring(indexNN, namePortBeforeNode.length());
					String numperComponent = portNamecurrentComponent.substring(indexCC, namePortBeforeNode.length());
					if (numperPortBeforeNode.equals(numperComponent)) {
						return i;
					}
				}
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
