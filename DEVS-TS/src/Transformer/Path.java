package Transformer;

import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class Path {

	private ArrayList<String> first;
	private ArrayList<String> port;
	private ArrayList<String> second;
	
	
	public Path() {
		super();
		first = new ArrayList<String>();
		port = new ArrayList<String>();
		second = new ArrayList<String>();
	}

	//getters and setters
	public ArrayList<String> getFirst() {
		return first;
	}


	public void setFirst(ArrayList<String> first) {
		this.first = first;
	}


	public ArrayList<String> getPort() {
		return port;
	}


	public void setPort(ArrayList<String> port) {
		this.port = port;
	}


	public ArrayList<String> getSecond() {
		return second;
	}


	public void setSecond(ArrayList<String> second) {
		this.second = second;
	}

	public void createPath(JTree xmlTree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) xmlTree.getModel().getRoot();
		java.util.Enumeration e = root.breadthFirstEnumeration();

		int count = 1;

		while (e.hasMoreElements()) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) e.nextElement();
			// Only leaves of the tree are responsibilities
			if (currentNode.isLeaf()) {
				Transformer.Domain.Node domainNode = (Transformer.Domain.Node) currentNode.getUserObject();
				if (domainNode instanceof Transformer.Domain.Responsibility) {
					// Create path, assigning each relation a name
					Transformer.Domain.Responsibility domainNodeResponsibility = (Transformer.Domain.Responsibility) domainNode;
					for (int j = 0; j < domainNodeResponsibility.getSuccessor().size(); j++) {
						this.getFirst().add(domainNode.getName());
						this.getSecond().add(domainNodeResponsibility.getSuccessor().get(j)); // 0
						this.getPort().add(String.valueOf(count));
						count++;
					}
					if (domainNodeResponsibility.getPredecessor().get(0) == null) {
						this.getFirst().add(null);
						this.getSecond().add(domainNode.getName());
						this.getPort().add(String.valueOf(count));
						count++;
					}
				} else if (domainNode instanceof Transformer.Domain.Element) {
					Transformer.Domain.Element domainNodeElement = (Transformer.Domain.Element) domainNode;
					// Create path, assigning each relation a name
					for (int j = 0; j < domainNodeElement.getSuccessor().size(); j++) {
						this.getFirst().add(domainNode.getName());
						this.getSecond().add(domainNodeElement.getSuccessor().get(j));
						this.getPort().add(String.valueOf(count));
						count++;
					}
					if (domainNodeElement.getPredecessor().get(0) == null) {
						this.getFirst().add(null);
						this.getSecond().add(domainNode.getName());
						this.getPort().add(String.valueOf(count));
						count++;
					}
				}
			}
		}
	}
}
