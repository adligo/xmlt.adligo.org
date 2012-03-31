package org.adligo.xml.parsers.template;

import java.util.HashMap;
import java.util.Map;

import org.adligo.models.params.client.I_TemplateParams;

/**
 * this manages a single templates files class lookups
 * 
 * @author scott
 *
 */
public class TemplatesClassManager {
	private Map<String, I_ParamClassLookup> lookups = new HashMap<String, I_ParamClassLookup>();

	public synchronized void addLookup(String template, I_ParamClassLookup lookup) {
		lookups.put(template, lookup);
	}
	
	/**
	 * if this throws a npe you didn't set it up correctly!
	 * 
	 * @param template
	 * @param params
	 * @return
	 */
	public Class<?> lookup(String template, I_TemplateParams params) {
		I_ParamClassLookup lookup = lookups.get(template);
		return lookup.lookup(params);
	}
}
