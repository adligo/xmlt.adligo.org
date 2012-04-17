package org.adligo.xml.parsers.template.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.adligo.models.params.client.Param;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;

/**
 * this class is a utility
 * to execute a bunch of sql statements from a 
 * templates file in the order of the file (like a script)
 * 
 * @author scott
 *
 */
public class TemplateAsScriptExecutor {
	public static final String TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_TEMPLATES_INSTANCE_WHICH_HAS_BEEN_PARSED = 
		"TemplateAsScriptExecutor requires a Templates instance which has been parsed";
	public static final String TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_TEMPLATES_INSTANCE = 
		"TemplateAsScriptExecutor requires a Templates instance";
	public static final String TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_CONNECTION_INSTANCE = 
		"TemplateAsScriptExecutor requires a Connection instance";
	private Connection connection;
	
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void run(Templates templates) throws SQLException {
		if (connection == null) {
			throw new NullPointerException(TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_CONNECTION_INSTANCE);
		}
		if (templates == null) {
			throw new NullPointerException(TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_TEMPLATES_INSTANCE);
		}
		if (!templates.isParsed()) {
			throw new IllegalStateException(
					TEMPLATE_AS_SCRIPT_EXECUTOR_REQUIRES_A_TEMPLATES_INSTANCE_WHICH_HAS_BEEN_PARSED);
		}
		
		connection.setAutoCommit(true);
		
		Iterator<String> names = templates.getTemplateNames();
		while (names.hasNext()) {
			String templateName = names.next();
			Template temp = templates.getTemplate(templateName);
			
			JdbcEngineInput values = new JdbcEngineInput();
			values.setParams(new Param());
			values.setConnection(connection);
			values.setTemplate(temp);
			JdbcTemplateParserEngine.execute(values);
		}
		
	}
	

}
