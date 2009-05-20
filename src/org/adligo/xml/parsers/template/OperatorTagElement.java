package org.adligo.xml.parsers.template;

public class OperatorTagElement extends TemplateElement {
	private int id = 0;

	public synchronized int getId() {
		return id;
	}

	public synchronized void setId(int id) {
		this.id = id;
	}

	@Override
	public int getType() {
		return ElementTypes.OPEARTOR_TAG;
	}
	
}
