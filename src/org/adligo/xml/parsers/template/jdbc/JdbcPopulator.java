package org.adligo.xml.parsers.template.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.models.params.shared.ValueType;
import org.adligo.models.params.shared.ValueTypes;

public class JdbcPopulator {
	private static final Log log = LogFactory.getLog(JdbcPopulator.class);
	
	public static void setJdbcQuestionMarks(QueryParameterAggregator agg,PreparedStatement stmt) throws SQLException {
		for (int i = 1; i <= agg.size(); i++) {
			Object value = agg.getValue(i -1 );
			ValueType vt = agg.getType(i - 1);
			short type = vt.getId();
			try {
				switch (type) {
					case ValueTypes.STRING_ID:
						stmt.setString(i, (String) value); 
						break;
					case ValueTypes.INTEGER_ID:
						stmt.setInt(i, (Integer) value); 
						break;
					case ValueTypes.DOUBLE_ID:
						stmt.setDouble(i, (Double) value); 
						break;
					case ValueTypes.LONG_ID:
						stmt.setLong(i, (Long) value); 
						break;
					case ValueTypes.SHORT_ID:
						stmt.setShort(i, (Short) value); 
						break;
					case ValueTypes.FLOAT_ID:
						stmt.setFloat(i, (Float) value); 
						break;
					case ValueTypes.DATE_ID:
						stmt.setDate(i, new java.sql.Date(((Date) value).getTime())); 
						break;
					case ValueTypes.BOOLEAN_ID:
						stmt.setBoolean(i, (Boolean) value); 
						break;
					case ValueTypes.BIG_DECIMAL_ID:
						stmt.setBigDecimal(i, new BigDecimal((String) value)); 
						break;
					case ValueTypes.BIG_INTEGER_ID:
						throw new IllegalArgumentException("jdbc does not support BigIntegers!");
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
