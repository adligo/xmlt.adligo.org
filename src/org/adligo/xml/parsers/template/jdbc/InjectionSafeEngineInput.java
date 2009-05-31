package org.adligo.xml.parsers.template.jdbc;

import java.util.Set;

import org.adligo.models.params.client.I_Operators;
import org.adligo.xml.parsers.template.EngineInput;

/**
 * factored out this class for use in hibernate
 * @author scott
 *
 */
public class InjectionSafeEngineInput extends EngineInput {
	private Set<I_Operators> allowedOperators;
	
	public Set<I_Operators> getAllowedOperators() {
		return allowedOperators;
	}
	/**
	 * it is recomended you pass in a unmodifieable Set
	 * Collections.unmodifiableSet
	 * @param p
	 */
	public  void setAllowedOperators(Set<I_Operators> p) {
		allowedOperators = p;
	}
	
	protected void validate(Class<?> clz) {
		super.validate(clz);
		if (allowedOperators == null) {
			throw new NullPointerException(clz.getName() + " needs " +
					"a set of allowed operators");
		}
	}
}
