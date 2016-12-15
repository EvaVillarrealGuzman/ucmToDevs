package Transformer;

///modifique en 3 lugares
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import Transformer.Domain.Component;
import Transformer.Domain.Element;
import Transformer.Domain.Relation2;
import Transformer.Domain.Responsibility;
import view.modeling.ViewableDigraph;

public class DynamicClass {

	public void createSaViewDEVS(JTree xmlTree, JCodeModel codeModel) throws JClassAlreadyExistsException {
		// Crea los puertos de todos los componentes y responsabilidades
		Port port = new Port();
		port.createPort(xmlTree);

		// Busca la raíz del árbol que va ser la clase SaViewDEVS
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();

		String className = "SimEnvironment.SAModel.SystemTemp.SAViewDEVS";

		// JDefinedClass will let you create a class in a specified package.
		JDefinedClass sAViewDEVS = codeModel._class(className);
		sAViewDEVS._extends(ViewableDigraph.class);

		// this attribute only use to import
		JClass arrayListClass = codeModel.ref(ArrayList.class);
		JClass arrayListOfDoubleClass = arrayListClass.narrow(Double.class);
		sAViewDEVS.field(JMod.PRIVATE, arrayListOfDoubleClass, "arrayListDouble");

		// Crea el constructor sin parámetros
		Transformer.Domain.Component root = (Component) rootNode.getUserObject();
		JMethod constructorWithoutParameter = sAViewDEVS.constructor(JMod.PUBLIC);
		constructorWithoutParameter.body().directStatement("super(\"" + root.getName() + " - SA View\");");
		constructorWithoutParameter.body().directStatement("saViewConstruct();");

		// Crea el constructor sin parámetros
		JMethod constructorWithParameter = sAViewDEVS.constructor(JMod.PUBLIC);
		constructorWithParameter.param(String.class, "name");
		constructorWithParameter.body().directStatement("super(name);");
		constructorWithParameter.body().directStatement("saViewConstruct();");

		// Create saViewConstruct methods
		JMethod saViewConstruct = sAViewDEVS.method(JMod.PUBLIC, codeModel.VOID, "saViewConstruct");
		// Create ports
		saViewConstruct.body().directStatement("addInport(\"erip\");");
		saViewConstruct.body().directStatement("addOutport(\"esop\");");
		saViewConstruct.body().directStatement("addOutport(\"rtaop\");");
		saViewConstruct.body().directStatement("addOutport(\"rdtop\");");
		saViewConstruct.body().directStatement("addOutport(\"rrtop\");");
		saViewConstruct.body().directStatement("addOutport(\"rfailop\");");

		// Crea la definición para cada elemento que contiene
		for (int i = 0; i < xmlTree.getModel().getChildCount(rootNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(rootNode, i));
			Transformer.Domain.Node nodeData = (Transformer.Domain.Node) node.getUserObject();

			if (nodeData instanceof Transformer.Domain.Component) {
				Transformer.Domain.Component nodeDataComponent = (Component) nodeData;
				createComponent(xmlTree, codeModel, node, nodeDataComponent, saViewConstruct, port.getRelation());
			} else if (nodeData instanceof Transformer.Domain.Element
					|| nodeData instanceof Transformer.Domain.Responsibility) {
				createResponsability(node, nodeData, saViewConstruct);
			}
		}

		saViewConstruct.body().directStatement("initialize();");
		Coupling.createCoupling(xmlTree, root, rootNode, saViewConstruct, port.getRelation());
	}

	private void createComponent(JTree xmlTree, JCodeModel codeModel, DefaultMutableTreeNode currentNode,
			Transformer.Domain.Component currentComponent, JMethod parentMethod, Relation2 relations) throws JClassAlreadyExistsException {

		String temporalName = currentComponent.getName().replace(" ", "");
		// Crea el nombre del objeto
		String objectName = (temporalName.substring(0, 1)).toLowerCase()
				+ temporalName.substring(1, temporalName.length());
		// Crea el nombre de la clase
		String className = (temporalName.substring(0, 1)).toUpperCase()
				+ temporalName.substring(1, temporalName.length());

		// Create method
		parentMethod.body().directStatement("ViewableDigraph " + objectName + " = new " + className + "();");
		parentMethod.body().directStatement("add(" + objectName + ");");

		// Create class
		JDefinedClass newClass = codeModel._class("SimEnvironment.SAModel.SystemTemp." + className);
		newClass._extends(ViewableDigraph.class);

		// this attribute only use to import
		JClass arrayListClass = codeModel.ref(ArrayList.class);
		JClass arrayListOfDoubleClass = arrayListClass.narrow(Double.class);
		newClass.field(JMod.PRIVATE, arrayListOfDoubleClass, "arrayListDouble");

		// Crea el constructor sin parámetros
		JMethod constructorWithoutParameter = newClass.constructor(JMod.PUBLIC);
		constructorWithoutParameter.body().directStatement("super(\"" + currentComponent.getName() + "\");");
		constructorWithoutParameter.body().directStatement("construct();");

		// Crea el constructor con parámetros
		JMethod constructorWithParameter = newClass.constructor(JMod.PUBLIC);
		constructorWithParameter.param(String.class, "name");
		constructorWithParameter.body().directStatement("super(name);");
		constructorWithParameter.body().directStatement("construct();");

		// Create saViewConstruct methods
		JMethod construct = newClass.method(JMod.PUBLIC, codeModel.VOID, "construct");

		// Create ports
		for (int i = 0; i < currentComponent.getExternalInputCoupling().size(); i++) {
			construct.body().directStatement("addInport(\"" + currentComponent.getInputPorts().get(i) + "\");");
		}
		for (int i = 0; i < currentComponent.getExternalOutputCoupling().size(); i++) {
			construct.body().directStatement("addOutport(\"" + currentComponent.getOutputPorts().get(i) + "\");");
		}
		construct.body().directStatement("addOutport(\"taop\");");
		construct.body().directStatement("addOutport(\"dtop\");");
		construct.body().directStatement("addOutport(\"rtop\");");
		construct.body().directStatement("addOutport(\"failop\");");

		// Crea la definición para cada elemento que contiene
		for (int i = 0; i < xmlTree.getModel().getChildCount(currentNode); i++) {
			DefaultMutableTreeNode node = ((DefaultMutableTreeNode) xmlTree.getModel().getChild(currentNode, i));
			Transformer.Domain.Node nodeData = (Transformer.Domain.Node) node.getUserObject();

			if (nodeData instanceof Transformer.Domain.Component) {
				Transformer.Domain.Component nodeDataComponent = (Component) nodeData;
				createComponent(xmlTree, codeModel, node, nodeDataComponent, construct, relations);
			} else if (nodeData instanceof Transformer.Domain.Element
					|| nodeData instanceof Transformer.Domain.Responsibility) {
				createResponsability(node, nodeData, construct);
			}
		}

		construct.body().directStatement("initialize();");
		Coupling.createCoupling(xmlTree, currentComponent, currentNode, construct, relations);
	}

	private void createResponsability(DefaultMutableTreeNode currentNode, Transformer.Domain.Node currentDomainNode,
			JMethod parentMethod) {

		String temporalName = currentDomainNode.getName().replace(" ", "");
		// Crea el nombre del objeto
		String objectName = (temporalName.substring(0, 1)).toLowerCase()
				+ temporalName.substring(1, temporalName.length());

		if (currentDomainNode instanceof Transformer.Domain.Responsibility) {
			Transformer.Domain.Responsibility currentResponsibility = (Responsibility) currentDomainNode;
			parentMethod.body().directStatement("SimEnvironment.SAModel.Library.CPXResponsibility " + objectName
					+ " = new SimEnvironment.SAModel.Library.CPXResponsibility(" + currentResponsibility.getId()
					+ ", \"" + currentResponsibility.getName() + "\", " + currentResponsibility.getMeanExecuteTime()
					+ ", " + currentResponsibility.getMeanRecoveryTime() + ", "
					+ currentResponsibility.getMeanTimeBFail() + ", " + currentResponsibility.getMeanDownTime() + ");");
			parentMethod.body().directStatement("add(" + objectName + ");");
		} else if (currentDomainNode instanceof Transformer.Domain.Element) {
			Transformer.Domain.Element currentElement = (Element) currentDomainNode;
			if (currentElement.getType().equals("ucm.map:AndFork")) {
				ArrayList<String> successor = currentElement.getSuccessor();
				parentMethod.body()
						.directStatement("model.modeling.atomic  " + objectName
								+ " = new SimEnvironment.SAModel.Library.ANDFork(" + currentElement.getId()
								+ ",\"andFork\", " + successor.size() + ");");
				parentMethod.body().directStatement("add(" + objectName + ");");
			} else if (currentElement.getType().equals("ucm.map:AndJoin")) {
				ArrayList<String> predecessor = currentElement.getPredecessor();
				parentMethod.body()
						.directStatement("model.modeling.atomic  " + objectName
								+ " = new SimEnvironment.SAModel.Library.ANDJoin(" + currentElement.getId()
								+ ",\"andFork\", " + predecessor.size() + ");");
				parentMethod.body().directStatement("add(" + objectName + ");");
			} else if (currentElement.getType().equals("ucm.map:OrFork")) {
				ArrayList<String> successor = currentElement.getSuccessor();
				String nameArrayList = currentElement.getName();
				parentMethod.body()
						.directStatement("ArrayList<Double> " + nameArrayList + "AL = new ArrayList<Double>();");
				for (int j = 0; j < currentElement.getPathProbabilities().size(); j++) {
					parentMethod.body().directStatement(
							nameArrayList + "AL.add(" + currentElement.getPathProbabilities().get(j) + ");");
				}
				parentMethod.body()
						.directStatement("model.modeling.atomic  " + objectName
								+ " = new SimEnvironment.SAModel.Library.ORFork(" + currentElement.getId()
								+ ",\"orFork\", " + successor.size() + ", " + nameArrayList + "AL);");
				parentMethod.body().directStatement("add(" + objectName + ");");
			} else if (currentElement.getType().equals("ucm.map:OrJoin")) {
				ArrayList<String> predecessor = currentElement.getPredecessor();
				parentMethod.body().directStatement(
						"model.modeling.atomic  " + objectName + " = new SimEnvironment.SAModel.Library.ORJoin("
								+ currentElement.getId() + ",\"orJoin\", " + predecessor.size() + ");");
				parentMethod.body().directStatement("add(" + objectName + ");");
			}
		}

	}

}
