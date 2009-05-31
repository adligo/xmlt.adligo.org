package org.adligo.xml.parsers.template.jdbc;

import java.sql.Connection;

public class JdbcEngineInput extends InjectionSafeEngineInput {
	Connection connection;
	

	public void validate(Class<?> clz) {
		if (connection == null) {
			throw new NullPointerException(clz.getName() + " needs " +
					"a connection instance");
		}	
	}
	public boolean validate() {
		validate(getClass());
		return true;
	}
	
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
