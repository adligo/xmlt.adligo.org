package org.adligo.xml.parsers.template.jdbc;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.xml.parsers.template.Template;

public class JdbcTemplateParserValues {
	Connection connection;
	Template template;
	I_TemplateParams params;
	Set<String[]> allowedOperators = new HashSet<String[]>();
	

	public boolean validate() {
		if (connection == null) {
			throw new NullPointerException("JdbcTemplateParserValues needs " +
					"a connection instance");
		}
		if (template == null) {
			throw new NullPointerException("JdbcTemplateParserValues needs " +
					"a template instance");
		}
		if (params == null) {
			throw new NullPointerException("JdbcTemplateParserValues needs " +
					"a params instance");
		}
		return true;
	}
	
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
	public Set<String[]> getAllowedOperators() {
		return allowedOperators;
	}
	public  void setAllowedOperators(Set<String[]> p) {
		allowedOperators.clear();
		allowedOperators.addAll(p);
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}
