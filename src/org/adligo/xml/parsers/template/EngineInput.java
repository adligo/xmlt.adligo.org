package org.adligo.xml.parsers.template;

import org.adligo.models.params.shared.I_TemplateParams;

public class EngineInput {
	private Template template;
	private I_TemplateParams params;
	
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public I_TemplateParams getParams() {
		return params;
	}
	public void setParams(I_TemplateParams params) {
		this.params = params;
	}
	
	protected void validate(Class<?> cls) {
		//params may be null!
		if (template == null) {
			throw new NullPointerException(cls.getName() + " needs " +
					"a template instance");
		}
		if (params == null) {
			throw new NullPointerException(cls.getName() + " needs " +
					"a I_TemplateParams object");
		}
	}
	
	public boolean validate() {
		validate(getClass());
		return true;
	}
	
	public void clear() {
		template = null;
		params = null;
	}
}
