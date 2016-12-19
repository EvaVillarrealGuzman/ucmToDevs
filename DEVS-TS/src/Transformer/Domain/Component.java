package Transformer.Domain;

import java.util.ArrayList;
import java.util.HashSet;

public class Component extends Node {

	// Indica si el nodo es el primero en el camino
	private boolean isStart;
	// Indica si el nodo es el último en el camino
	private boolean isEnd;

	// Indica cuales nodos forman parte del acoplamiento externo
	private ArrayList<String> externalOutputCoupling;
	private ArrayList<String> externalInputCoupling;

	// Indica la relación existente entre dos elementos del camino que se
	// encuentran conectados. Al mismo se le asigna un nombre único (un número).
	private ArrayList<String> internalCouplingFirts;
	private ArrayList<String> internalCouplingSecond;
	private ArrayList<String> internalCouplingName;

	// Indica los componentes internos del componente
	private ArrayList<String> internalComponents;

	public Component(int id, String name) {
		super(id, name);
		isStart = false;
		isEnd = false;
	}

	public ArrayList<String> getExternalOutputCoupling() {
		if (externalOutputCoupling == null) {
			externalOutputCoupling = new ArrayList<String>();
		}
		return externalOutputCoupling;
	}

	public void setExternalOutputCoupling(ArrayList<String> externalOutputCoupling) {
		this.externalOutputCoupling = externalOutputCoupling;
	}

	public ArrayList<String> getExternalInputCoupling() {
		if (externalInputCoupling == null) {
			externalInputCoupling = new ArrayList<String>();
		}
		return externalInputCoupling;
	}

	public void setExternalInputCoupling(ArrayList<String> externalInputCoupling) {
		this.externalInputCoupling = externalInputCoupling;
	}

	public ArrayList<String> getInternalCouplingFirts() {
		if (internalCouplingFirts == null) {
			internalCouplingFirts = new ArrayList<String>();
		}
		return internalCouplingFirts;
	}

	public void setInternalCouplingFirts(ArrayList<String> internalCouplingFirts) {
		this.internalCouplingFirts = internalCouplingFirts;
	}

	public ArrayList<String> getInternalCouplingSecond() {
		if (internalCouplingSecond == null) {
			internalCouplingSecond = new ArrayList<String>();
		}
		return internalCouplingSecond;
	}

	public void setInternalCouplingSecond(ArrayList<String> internalCouplingSecond) {
		this.internalCouplingSecond = internalCouplingSecond;
	}

	public ArrayList<String> getInternalComponents() {
		if (internalComponents == null) {
			internalComponents = new ArrayList<String>();
		}
		return internalComponents;
	}

	public void setInternalComponents(ArrayList<String> internalComponents) {
		this.internalComponents = internalComponents;
	}

	public ArrayList<String> getInternalCouplingName() {
		if (internalCouplingName == null) {
			internalCouplingName = new ArrayList<String>();
		}
		return internalCouplingName;
	}

	public void setInternalCouplingName(ArrayList<String> internalCouplingName) {
		this.internalCouplingName = internalCouplingName;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public void createInternalComponent() {
		// TODO se puede optimizar trabajando directamente con HashSet
		internalComponents = new ArrayList<String>();

		internalComponents.addAll(this.getExternalInputCoupling());
		internalComponents.addAll(this.getExternalOutputCoupling());
		internalComponents.addAll(this.getInternalCouplingFirts());
		internalComponents.addAll(this.getInternalCouplingSecond());

		// Al hacer esto se elimina componentes repetidos
		HashSet<String> hashSet = new HashSet<String>(internalComponents);
		internalComponents.clear();
		internalComponents.addAll(hashSet);

	}

}
