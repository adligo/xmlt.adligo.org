package org.adligo.xml.parsers.template.jdbc;

import java.io.IOException;
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
	private Connection connection;
	private Templates templates;
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Templates getTemplates() {
		return templates;
	}
	public void setTemplates(Templates templates) {
		this.templates = templates;
	}
	
	public void run() throws SQLException {
		assert connection != null;
		assert templates != null;
		assert templates.isParsed();
		
		
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
