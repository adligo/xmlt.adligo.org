package org.adligo.xml.parsers.template.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.i.util.client.ArrayCollection;
import org.adligo.models.params.client.ValueTypes;

public class JdbcParamValueAggregator {
	private static final Log log = LogFactory.getLog(JdbcParamValueAggregator.class);
	
	
	private ArrayCollection allValues = new ArrayCollection();
	private ArrayCollection allValueTypes = new ArrayCollection();
	
	public void addValue(short type, Object o) {
		allValueTypes.add(type);
		allValues.add(o);
	}
	
	public Object getValue(int i) {
		return allValues.get(i);
	}
	
	public short getType(int i) {
		return (Short) allValueTypes.get(i);
	}
	
	public int size() {
		return allValues.size();
	}
	
	public void setJdbcQuestionMarks(PreparedStatement stmt) throws SQLException {
		for (int i = 1; i <= allValues.size(); i++) {
			Object value = allValues.get(i -1 );
			short type = (Short) allValueTypes.get(i -1);
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
					case ValueTypes.BOOLEAN:
						stmt.setBoolean(i, (Boolean) value); 
						break;
					default:
						throw new SQLException("Unknown type " + type +
								" for paramter " + i + " value = " + value);
				}
			} catch (SQLException ex) {
				log.error("SQLException setting paramter " + i + " a " +
						type + " with content " + value);
				throw ex;
			}
		}
	}
}
