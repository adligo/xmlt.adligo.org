package org.adligo.xml.parsers.template;

/**
 * immutable class (for use among multiple threads)
 * representing a operator tag
 *
 * @author scott
 *
 */
public class OperatorTagElement extends TemplateElement {
	private int id = 0;

	public OperatorTagElement(String superValue) {
		super(superValue);
	}
	
	public OperatorTagElement(String superValue, int p_id) {
		super(superValue);
		id = p_id;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public int getType() {
		return ElementTypes.OPEARTOR_TAG;
	}
	
}
