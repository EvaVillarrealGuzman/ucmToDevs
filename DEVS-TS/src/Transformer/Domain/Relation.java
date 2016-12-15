package Transformer.Domain;

public class Relation {

	private String element;
	private String port;

	public Relation(String element, String port) {
		super();
		this.element = element;
		this.port = port;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

}