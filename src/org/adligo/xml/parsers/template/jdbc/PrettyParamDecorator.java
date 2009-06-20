package org.adligo.xml.parsers.template.jdbc;

import java.util.Date;

import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.models.params.client.ParamDecorator;

/**
 * this class is to provide the developer
 * with a parsed log ready version of the query
 * so that the user can copy it out 
 * of a log and attempt to run
 * it with out ANY modifications
 * 
 * @author scott
 *
 */
public class PrettyParamDecorator extends ParamDecorator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -712990651675949276L;
	private I_PrettyQuery prettyQuery = new DefaultPrettyQuery();
	
	public PrettyParamDecorator(I_TemplateParams delegate) {
		super(delegate);
	}
	
	@Override
	public Object[] getValues() {
		Object [] values = super.getDelegate().getValues();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof String) {
					values[i] = "'" + prettyQuery.prepairString((String) value) + "'";
				} else if (value instanceof Date) {
					values[i] = prettyQuery.prepairDate((Date) value);
				} else if (value instanceof Boolean) {
					values[i] = prettyQuery.prepairBoolean((Boolean) value);
				}
			}
		}
		return values;
	}

	@Override
	public I_TemplateParams getNestedParams() {
		// TODO Auto-generated method stub
		return new PrettyParamDecorator(super.getNestedParams());
	}

	public I_PrettyQuery getPrettyQuery() {
		return prettyQuery;
	}

	public void setPrettyQuery(I_PrettyQuery prettyQuery) {
		this.prettyQuery = prettyQuery;
	}

}
