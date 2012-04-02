package org.adligo.xml.parsers.template.jdbc;

import java.security.InvalidParameterException;
import java.util.Set;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.models.params.client.I_Operators;
import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.models.params.client.ParamDecorator;

/**
 * this simply replaces all the dynamic parameter values with 
 * question marks which will be set later when 
 *  setJdbcQuestionMarks(PreparedStatement stmt) is called
 *  
 *  It also checks all the operator values against 
 *  the list of valid operators
 *  
 *  This was done mostly to block sql injection attacks, 
 *  however it also increases performance by compiling the sql
 *  which the JDBC Driver also caches.
 *  
 *  This can be used for sql as well as mdx (olap4j)
 *  
 * @author scott
 *
 */
public class JdbcParamsDecorator extends ParamDecorator implements I_TemplateParams {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(JdbcParamsDecorator.class);
	
	private QueryParameterAggregator aggregator;
	private Set<I_Operators> operators;
	
	public JdbcParamsDecorator(I_TemplateParams in, Set<I_Operators> allowedOperators,
			QueryParameterAggregator  p_aggregator) {
		super(in);
		operators = allowedOperators;
		aggregator = p_aggregator;
	}

	@Override
	public Object[] getValues() {
		Object [] vals =  super.getValues();
		short [] types = super.getValueTypes();
		Object [] toRet = new Object[vals.length];
		for (int i = 0; i < toRet.length; i++) {
			aggregator.addValue(types[i], vals[i]);
			// put in a question mark to be set later by 
			// the setJdbcQustionMarks method
			toRet[i] = "?";
		}
		
		return toRet;
	}

	
	

	@Override
	public I_Operators getOperators() {
		I_Operators paramOperators = super.getOperators();
		if (log.isDebugEnabled()) {
			log.debug("checking operators " + paramOperators);
		}
		if (paramOperators != null) {
			if ( !operators.contains(paramOperators)) {
				StringBuilder message = new StringBuilder();
				message.append("Invalid operator, you must add the operator ");
				String [] ops = paramOperators.getValues();
				if (ops != null) {
					for (int i = 0; i < ops.length; i++) {
						if (i != 0) {
							message.append(",");
						}
						message.append("'");
						message.append(ops[i]);
						message.append("'");
					}
				}
				message.append(" or you could be in the middle of" +
						" a query injection attack!");
				throw new InvalidParameterException(message.toString());
			}
		}
		return paramOperators;
	}

	@Override
	public I_TemplateParams getNestedParams() {
		return new JdbcParamsDecorator(super.getNestedParams(), operators,
				aggregator);
	}
	
}
