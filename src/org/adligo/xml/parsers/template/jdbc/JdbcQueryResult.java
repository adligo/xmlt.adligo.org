package org.adligo.xml.parsers.template.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  a immutable class so that the statement and result set
 *  can be closed correctly
 * @author scott
 *
 */
public class JdbcQueryResult {
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	
	public JdbcQueryResult(PreparedStatement stmt, ResultSet rs) {
		preparedStatement = stmt;
		resultSet = rs;
	}
	
	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}
	public void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
	public ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	public void close() throws SQLException {
		resultSet.close();
		preparedStatement.close();
	}
	
}
