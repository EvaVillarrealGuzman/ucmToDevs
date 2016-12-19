package Transformer;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Transformer.Domain.Component;
import Transformer.Domain.Responsibility;
import Transformer.Domain.StartPoint;

/**
 * This class converts a UCM to a java tree objectF
 * 
 * @author: María Eva Villarreal Guzmán. E-mail: villarrealguzman@gmail.com
 *
 */
public class ConvertUCM2JTree extends JFrame implements TreeSelectionListener {
	JTree tree;
	Document doc;
	DefaultTreeModel model;
	Object[] result;
	StartPoint startPoint;
	int index;

	public Object[] convertToTree(String path) {
		try {
			// lee el archivo xml que se convertirá en árbol
			File fXmlFile = new File(path);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			// busca una lista de los elementos con el tag correspondiente
			NodeList contRefsList = doc.getElementsByTagName("contRefs");
			Transformer.Domain.Component rootNode = null;

			// busca el padre del árbol que se creará
			for (int i = 0; i < contRefsList.getLength(); i++) {
				Node contRef = contRefsList.item(i);
				if (contRef.getNodeType() == Node.ELEMENT_NODE) {
					Element eContRef = (Element) contRef;
					String parent = eContRef.getAttribute("parent");
					if (parent.equals("")) {
						String id = eContRef.getAttribute("id");
						rootNode = new Transformer.Domain.Component(Integer.parseInt(id),
								this.translateNameComponent(id));
					}
				}
			}

			DefaultMutableTreeNode parentN = new DefaultMutableTreeNode(rootNode);

			// Definimos el modelo donde se agregaran los nodos
			model = new DefaultTreeModel(parentN);

			// agregamos el modelo al arbol, donde previamente establecimos la
			// raiz
			tree = new JTree(model);

			// definimos los eventos
			tree.getSelectionModel().addTreeSelectionListener(this);

			// Cada Padre crea a su hijo, recursivamente
			creationComponent(parentN);

			createPredecessorSuccessor();

			creationStartPoint();

			result = new Object[2];

			result[0] = tree;
			result[1] = startPoint;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * Devuelve el nombre correspondiente de un elemento componente con un
	 * determinado id (value)
	 * 
	 * @param id
	 * @return
	 */
	private String translateNameComponent(String id) {
		NodeList componentsList = doc.getElementsByTagName("components");

		for (int i = 0; i < componentsList.getLength(); i++) {

			Node component = componentsList.item(i);

			if (component.getNodeType() == Node.ELEMENT_NODE) {
				Element eComponent = (Element) component;
				if (eComponent.getAttribute("contRefs").equals(id)) {
					return eComponent.getAttribute("name");
				}
			}
		}
		return "";
	}

	/**
	 * Devuelve el nombre correspondiente de un elemento responsabilidad con un
	 * determinado id (value)
	 * 
	 * @param value
	 * @return
	 */
	private String translateNameSimpleElement(String value) {
		NodeList responsibilitiesNode = doc.getElementsByTagName("responsibilities");

		for (int i = 0; i < responsibilitiesNode.getLength(); i++) {

			Node responsibility = responsibilitiesNode.item(i);

			if (responsibility.getNodeType() == Node.ELEMENT_NODE) {
				Element eResponsibility = (Element) responsibility;
				if (eResponsibility.getAttribute("respRefs").equals(value)) {
					return eResponsibility.getAttribute("name");
				}
			}
		}

		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {

			Node nNode = nodesList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String typeNode = eElement.getAttribute("xsi:type");
				if (eElement.getAttribute("id").equals(value) && !typeNode.equals("ucm.map:EndPoint")
						&& !typeNode.equals("ucm.map:StartPoint")) {
					return eElement.getAttribute("name");
				}
			}
		}

		return null;
	}

	private void creationComponent(DefaultMutableTreeNode parentN) {
		NodeList contRefsList = doc.getElementsByTagName("contRefs");
		int index = 0;

		Transformer.Domain.Component dataParent = (Component) parentN.getUserObject();

		for (int j = 0; j < contRefsList.getLength(); j++) {

			Node contRef = contRefsList.item(j);

			if (contRef.getNodeType() == Node.ELEMENT_NODE) {
				Element eContRef = (Element) contRef;

				// busca el elemento del archivo xml que representa al padre del
				// subárbol actual en el árbol
				if (this.translateNameComponent(eContRef.getAttribute("id")).equals(dataParent.getName())) {
					String children = eContRef.getAttribute("children");
					if (!children.equals("")) {
						// crea un vector con los hijos del padre
						String[] childArray = children.split(" ");
						for (int i = 0; i < childArray.length; i++) {

							Element eChild = getNodeOfComponent(childArray[i]);

							String id = eChild.getAttribute("id");
							Transformer.Domain.Component child = new Transformer.Domain.Component(Integer.parseInt(id),
									this.translateNameComponent(id));

							DefaultMutableTreeNode hijoN = new DefaultMutableTreeNode(child);
							model.insertNodeInto(hijoN, parentN, index);
							index++;
							// llama recursivamente, para que si este nodo tiene
							// hijos, los cree
							creationComponent(hijoN);
						}
					}

					// si se llega a este punto, es porque el nodo no tiene
					// hijos
					// por lo cual se supone que es un componente simple
					// entonces, se buscan sus responsabilidades
					String nodes = eContRef.getAttribute("nodes");
					if (!nodes.equals("")) {
						String[] node = nodes.split(" ");
						for (int k = 0; k < node.length; k++) {
							// Por cada responsabilidad
							this.creationSimpleElement(node[k], parentN, index);
						}
					}
				}
			}
		}
	}

	private int creationSimpleElement(String idNode, DefaultMutableTreeNode parentNode, int index) {
		NodeList responsibilitiesList = doc.getElementsByTagName("responsibilities");
		NodeList nodesList = doc.getElementsByTagName("nodes");

		Element eNode = this.getNodeOfResponsability(idNode, doc);

		for (int i = 0; i < responsibilitiesList.getLength(); i++) {
			Node respNode = responsibilitiesList.item(i);

			if (respNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eResponsability = (Element) respNode;

				if (eResponsability.getAttribute("respRefs").equals(idNode)) {

					Responsibility child = new Responsibility(Integer.parseInt(idNode),
							this.translateNameSimpleElement(idNode));

					NodeList metadatasList = eResponsability.getElementsByTagName("metadata");

					// Busca los metadatos
					for (int k = 0; k < metadatasList.getLength(); k++) {
						Node node = metadatasList.item(k);
						Element metadata = (Element) node;
						String metadataName = metadata.getAttribute("name");

						if (metadataName.equals("MeanExecutionTime")) {
							child.setMeanExecuteTime(Double.parseDouble(metadata.getAttribute("value")));
						} else if (metadataName.equals("MeanDowntime")) {
							child.setMeanDownTime(Double.parseDouble(metadata.getAttribute("value")));
						} else if (metadataName.equals("MeanRecoveryTime")) {
							child.setMeanRecoveryTime(Double.parseDouble(metadata.getAttribute("value")));
						} else if (metadataName.equals("MeanTimeBFail")) {
							child.setMeanTimeBFail(Double.parseDouble(metadata.getAttribute("value")));
						}

					}

					child.setPredecessor(this.getPredecessor(idNode));
					child.setSuccessor(this.getSuccessor(idNode));

					DefaultMutableTreeNode hijoN = new DefaultMutableTreeNode(child);

					model.insertNodeInto(hijoN, parentNode, index);
					index++;

					return 0;

				}
			}
		}

		for (int i = 0; i < nodesList.getLength(); i++) {
			Node node = nodesList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eCurrentNode = (Element) node;
				String nodeType = eCurrentNode.getAttribute("xsi:type");
				if (eCurrentNode.getAttribute("id").equals(idNode) && !nodeType.equals("ucm.map:EndPoint")
						&& !nodeType.equals("ucm.map:StartPoint")) {

					Transformer.Domain.Element child = new Transformer.Domain.Element(Integer.parseInt(idNode),
							this.translateNameSimpleElement(idNode));

					child.setType(nodeType);

					child.setPredecessor(this.getPredecessor(idNode));
					child.setSuccessor(this.getSuccessor(idNode));

					if (nodeType.equals("ucm.map:OrFork")) {
						ArrayList<Double> pathProbabilities = new ArrayList<Double>();

						NodeList metadatasList = eCurrentNode.getElementsByTagName("metadata");

						// Busca los metadatos
						for (int k = 0; k < metadatasList.getLength(); k++) {
							Node metadataNode = metadatasList.item(k);
							Element metadata = (Element) metadataNode;

							pathProbabilities.add(Double.parseDouble(metadata.getAttribute("value")));
						}
						child.setPathProbabilities(pathProbabilities);

					}

					DefaultMutableTreeNode hijoN = new DefaultMutableTreeNode(child);

					model.insertNodeInto(hijoN, parentNode, index);
					index++;

				}
			}

		}

		return 0;

	}

	private void creationStartPoint() {

		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {
			Node node = nodesList.item(i);

			Element eNode = (Element) node;

			if (eNode.getAttribute("xsi:type").equals("ucm.map:StartPoint")) {
				NodeList metadatasList = eNode.getElementsByTagName("metadata");

				Node currentNode = metadatasList.item(0);
				Element metadata = (Element) currentNode;

				startPoint = new StartPoint(Integer.parseInt(eNode.getAttribute("id")), "start point",
						Double.parseDouble(metadata.getAttribute("value")));

			}

		}

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
	}

	/**
	 * Obtiene los elementos predecesores de un elemento particular
	 * 
	 * @param idSource
	 * @return
	 */
	private ArrayList<String> getSuccessor(String idSource) {
		ArrayList<String> successor = new ArrayList<String>();
		NodeList listConnections = doc.getElementsByTagName("connections");

		for (int i = 0; i < listConnections.getLength(); i++) {

			Node connection = listConnections.item(i);
			Element eConnection = (Element) connection;

			if (idSource.equals(eConnection.getAttribute("source"))) {
				successor.add(translateNameSimpleElement(eConnection.getAttribute("target")));
			}

		}

		return successor;
	}

	/**
	 * Obtiene los elementos sucesores de un elemento particular
	 * 
	 * @param idTarget
	 * @return
	 */
	private ArrayList<String> getPredecessor(String idTarget) {
		ArrayList<String> predecessor = new ArrayList<String>();
		NodeList listConnections = doc.getElementsByTagName("connections");

		for (int i = 0; i < listConnections.getLength(); i++) {

			Node node = listConnections.item(i);
			Element eNode = (Element) node;

			if (idTarget.equals(eNode.getAttribute("target"))) {
				predecessor.add(translateNameSimpleElement(eNode.getAttribute("source")));
			}
		}

		return predecessor;
	}

	private Element getNodeOfResponsability(String idNode, Document doc) {
		NodeList nodesList = doc.getElementsByTagName("nodes");

		for (int i = 0; i < nodesList.getLength(); i++) {

			Node node = nodesList.item(i);
			Element eNode = (Element) node;

			String id = eNode.getAttribute("id");
			if (id.equals(idNode)) {
				return eNode;
			}

		}
		return null;
	}

	private Element getNodeOfComponent(String id) {
		NodeList contRefsList = doc.getElementsByTagName("contRefs");

		for (int i = 0; i < contRefsList.getLength(); i++) {

			Node contRef = contRefsList.item(i);
			Element eContRef = (Element) contRef;

			if (id.equals(eContRef.getAttribute("id"))) {
				return eContRef;
			}

		}

		return null;
	}

	/**
	 * Crea para los nodos que son componentes del árbol, los sucesores y
	 * predecesores del mismo
	 */

	private void createPredecessorSuccessor() {

		Boolean terminationCondition = true;

		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		Transformer.Domain.Component rootComponent = (Transformer.Domain.Component) rootNode.getUserObject();

		int currentDepth = rootNode.getDepth() - 1;

		while (terminationCondition) {
			java.util.Enumeration e = rootNode.breadthFirstEnumeration();

			while (e.hasMoreElements()) {
				// nodo a evaluar
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

				if (currentDepth == node.getLevel()) {

					if (rootNode == node) {
						terminationCondition = false;
					}

					Transformer.Domain.Node nodeData = (Transformer.Domain.Node) node.getUserObject();

					if (nodeData instanceof Transformer.Domain.Component) {

						Transformer.Domain.Component nodeDataComponent = (Component) nodeData;

						for (int j = 0; j < node.getChildCount(); j++) {

							DefaultMutableTreeNode childNode = ((DefaultMutableTreeNode) tree.getModel().getChild(node,
									j));
							Transformer.Domain.Node childData = (Transformer.Domain.Node) childNode.getUserObject();

							if (childData instanceof Transformer.Domain.Element) {
								Transformer.Domain.Element nodeDataElement = (Transformer.Domain.Element) childData;

								ArrayList<String> predecessor = (ArrayList<String>) nodeDataElement.getPredecessor();
								ArrayList<String> successor = (ArrayList<String>) nodeDataElement.getSuccessor();

								searchPredecessor(predecessor, node, nodeDataComponent, childData);
								searchSuccessor(successor, node, nodeDataComponent, childData, childNode);

							} else if (childData instanceof Responsibility) {
								Responsibility nodeDataResponsibility = (Responsibility) childData;

								ArrayList<String> predecessor = (ArrayList<String>) nodeDataResponsibility
										.getPredecessor();
								ArrayList<String> successor = (ArrayList<String>) nodeDataResponsibility.getSuccessor();

								searchPredecessor(predecessor, node, nodeDataComponent, childData);
								searchSuccessor(successor, node, nodeDataComponent, childData, childNode);

							} else if (childData instanceof Component) {
								ArrayList<DefaultMutableTreeNode> leafList = this.getLeafs(childNode);

								for (int k = 0; k < leafList.size(); k++) {
									DefaultMutableTreeNode leaf = leafList.get(k);
									Transformer.Domain.Node leafData = (Transformer.Domain.Node) leaf.getUserObject();

									if (leafData instanceof Transformer.Domain.Element) {
										Transformer.Domain.Element nodeDataElement = (Transformer.Domain.Element) leafData;

										ArrayList<String> predecessor = (ArrayList<String>) nodeDataElement
												.getPredecessor();
										ArrayList<String> successor = (ArrayList<String>) nodeDataElement
												.getSuccessor();

										searchPredecessor(predecessor, node, nodeDataComponent, childData);
										searchSuccessor(successor, node, nodeDataComponent, childData, childNode);

									} else if (leafData instanceof Responsibility) {
										Responsibility nodeDataResponsibility = (Responsibility) leafData;

										ArrayList<String> predecessor = (ArrayList<String>) nodeDataResponsibility
												.getPredecessor();
										ArrayList<String> successor = (ArrayList<String>) nodeDataResponsibility
												.getSuccessor();

										searchPredecessor(predecessor, node, nodeDataComponent, childData);
										searchSuccessor(successor, node, nodeDataComponent, childData, childNode);

									}

								}

							}
						}
						((Component) nodeData).createInternalComponent();
					}
				}
			}
			currentDepth--;
		}
	}

	private void searchSuccessor(ArrayList<String> successor, DefaultMutableTreeNode node, Component nodeDataComponent,
			Transformer.Domain.Node childData, DefaultMutableTreeNode childNode) {

		for (int k = 0; k < successor.size(); k++) {
			String nextName = successor.get(k);

			if (nextName != null) {

				DefaultMutableTreeNode nextNode = getDefaultMutableTreeNode(nextName);

				if (!isInTree(node, nextNode)) {

					nodeDataComponent.getExternalOutputCoupling().add(childData.getName());

				} else {
					Transformer.Domain.Node dataNext = (Transformer.Domain.Node) nextNode.getUserObject();

					while (childNode.getLevel() != nextNode.getLevel()) {
						nextNode = (DefaultMutableTreeNode) nextNode.getParent();
						dataNext = (Transformer.Domain.Node) nextNode.getUserObject();
					}

					if (nextNode != childNode) {

						nextName = dataNext.getName();

						nodeDataComponent.getInternalCouplingFirts().add(childData.getName());
						nodeDataComponent.getInternalCouplingSecond().add(nextName);
						//nodeDataComponent.getInternalCouplingName().add(Integer.toString(index));
						index++;
					}
				}

			} else {
				nodeDataComponent.setEnd(true);
				nodeDataComponent.getExternalOutputCoupling().add(childData.getName());
			}

		}
	}

	private void searchPredecessor(ArrayList<String> predecessor, DefaultMutableTreeNode node,
			Component nodeDataComponent, Transformer.Domain.Node childData) {
		for (int k = 0; k < predecessor.size(); k++) {
			String before = predecessor.get(k);
			if (before != null) {
				DefaultMutableTreeNode beforeNode = getDefaultMutableTreeNode(before);
				if (!isInTree(node, beforeNode)) {
					Transformer.Domain.Node childDataDNode = (Transformer.Domain.Node) childData;
					nodeDataComponent.getExternalInputCoupling().add(childDataDNode.getName());
				}
			} else {
				nodeDataComponent.setStart(true);
				nodeDataComponent.getExternalInputCoupling().add(childData.getName());
			}
		}
	}

	/***
	 * Devuelve el nodo del árbol cuyo nombre se corresponde con name
	 * 
	 * @param name
	 * @return
	 */
	private DefaultMutableTreeNode getDefaultMutableTreeNode(String name) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
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

	/**
	 * Devuelve si el memberNode es un miembre del subárbol cuya raíz es
	 * rootNode
	 * 
	 * @param rootNode
	 * @param memberNode
	 * @return
	 */
	public boolean isInTree(DefaultMutableTreeNode rootNode, DefaultMutableTreeNode memberNode) {
		java.util.Enumeration e = rootNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			if (memberNode == currentNode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Obtiene las hojas del subárbol que tiene a node como raíz
	 * 
	 * @param node
	 * @return
	 */
	public ArrayList<DefaultMutableTreeNode> getLeafs(DefaultMutableTreeNode node) {
		Boolean terminationCondition = true;

		ArrayList<DefaultMutableTreeNode> leafList = new ArrayList<DefaultMutableTreeNode>();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			if (currentNode.isLeaf()) {
				DefaultMutableTreeNode parent = currentNode;

				while (terminationCondition) {
					if (parent != root) {
						if (node.getLevel() != parent.getLevel()) {
							parent = (DefaultMutableTreeNode) parent.getParent();
						} else {
							terminationCondition = false;
						}
					} else {
						terminationCondition = false;
					}
				}

				if (node == parent) {
					leafList.add(currentNode);
				}
				terminationCondition = true;
			}
		}
		return leafList;

	}

}
