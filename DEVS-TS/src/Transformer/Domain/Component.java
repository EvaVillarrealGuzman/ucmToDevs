package Transformer.Domain;

import java.util.ArrayList;
import java.util.HashSet;

public class Component extends Node {
	private ArrayList<String> externalOutputCoupling;
	private ArrayList<String> externalInputCoupling;
	private ArrayList<String> internalCouplingFirts;
	private ArrayList<String> internalCouplingSecond;
	private ArrayList<String> internalCouplingName;
	private ArrayList<Relation> inputRelations;
	private ArrayList<Relation> outputRelations;
	private ArrayList<String> internalComponents;

	public Component(int id, String name) {
		super(id, name);
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

	public ArrayList<Relation> getInputRelations() {
		if (inputRelations == null) {
			inputRelations = new ArrayList<Relation>();
		}
		return inputRelations;
	}

	public void setInputRelations(ArrayList<Relation> inputRelations) {
		this.inputRelations = inputRelations;
	}

	public ArrayList<Relation> getOutputRelations() {
		if (outputRelations == null) {
			outputRelations = new ArrayList<Relation>();
		}
		return outputRelations;
	}

	public void setOutputRelations(ArrayList<Relation> outputRelations) {
		this.outputRelations = outputRelations;
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

	public void createInternalComponent() {
		internalComponents = new ArrayList<String>();

		internalComponents.addAll(this.getExternalInputCoupling());
		internalComponents.addAll(this.getExternalOutputCoupling());
		internalComponents.addAll(this.getInternalCouplingFirts());
		internalComponents.addAll(this.getInternalCouplingSecond());

		HashSet<String> hashSet = new HashSet<String>(internalComponents);
		internalComponents.clear();
		internalComponents.addAll(hashSet);

	}

}
