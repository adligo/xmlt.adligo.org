package org.adligo.xml.parsers.template.jdbc;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.i.util.client.ArrayCollection;
import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.models.params.client.ParamDecorator;
import org.adligo.models.params.client.ValueTypes;

public class JdbcParamDecorator extends ParamDecorator implements I_TemplateParams {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(JdbcParamDecorator.class);
	
	private ArrayCollection allValues = new ArrayCollection();
	private ArrayCollection allValueTypes = new ArrayCollection();
	private Set<String[]> operators = new HashSet<String[]>();
	
	public JdbcParamDecorator(I_TemplateParams in, Set<String[]> allowedOperators) {
		super(in);
		operators.addAll(allowedOperators);
	}

	@Override
	public Object[] getValues() {
		Object [] vals =  super.getValues();
		Object [] toRet = new Object[vals.length];
		for (int i = 0; i < toRet.length; i++) {
			allValues.add(vals[i]);
			// put in a question mark to be set later by 
			// the setJdbcQustionMarks method
			toRet[i] = "?";
		}
		short [] types = super.getValueTypes();
		for (int i = 0; i < toRet.length; i++) {
			allValueTypes.add(types[i]);
		}
		return toRet;
	}

	
	public void setJdbcQuestionMarks(PreparedStatement stmt) throws SQLException {
		for (int i = 0; i < allValues.size(); i++) {
			Object value = allValues.get(i);
			short type = (Short) allValueTypes.get(i);
			try {
				
				switch (type) {
					case ValueTypes.STRING:
						stmt.setString(i, (String) value); 
						break;
					case ValueTypes.INTEGER:
						stmt.setInt(i, (Integer) value); 
						break;
					case ValueTypes.DOUBLE:
						stmt.setDouble(i, (Double) value); 
						break;
					case ValueTypes.LONG:
						stmt.setLong(i, (Long) value); 
						break;
					case ValueTypes.SHORT:
						stmt.setShort(i, (Short) value); 
						break;
					case ValueTypes.FLOAT:
						stmt.setFloat(i, (Float) value); 
						break;
					case ValueTypes.DATE:
						stmt.setDate(i, new java.sql.Date(((Date) value).getTime())); 
						break;
					default:
						throw new SQLException("Unknown type " + type +
								" for paramter " + i + " value = " + value);
				}
			} catch (SQLException ex) {
				log.error("SQLException setting paramter " + i + " a " +
						type + " with content " + value);
			}
		}
	}

	@Override
	public String[] getOperators() {
		String [] paramOperators = super.getOperators();
		if ( !operators.contains(paramOperators)) {
			StringBuilder message = new StringBuilder();
			message.append("Invalid operator, you must add the operator ");
			for (int i = 0; i < paramOperators.length; i++) {
				if (i != 0) {
					message.append(",");
				}
				message.append("'");
				message.append(paramOperators[i]);
				message.append("'");
			}
			message.append(" or you could be in the middle of" +
					" a sql injection attack!");
			throw new InvalidParameterException(message.toString());
		}
		return paramOperators;
	}
	
}
