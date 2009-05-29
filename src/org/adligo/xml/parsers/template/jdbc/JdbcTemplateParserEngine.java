package org.adligo.xml.parsers.template.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.TemplateParserEngine;

public class JdbcTemplateParserEngine {

	static public ResultSet query(JdbcTemplateParserValues values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  JdbcParamValueAggregator aggregator = new JdbcParamValueAggregator();
		  JdbcParamDecorator jdbcParams =	new JdbcParamDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  PreparedStatement stmt = values.getConnection().prepareStatement(sqlWithQuestionMarks);
		  aggregator.setJdbcQuestionMarks(stmt);
		  ResultSet result = stmt.executeQuery();
		  return result;
	}
	
	static public boolean execute(JdbcTemplateParserValues values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  JdbcParamValueAggregator aggregator = new JdbcParamValueAggregator();
		  JdbcParamDecorator jdbcParams =	new JdbcParamDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  PreparedStatement stmt = values.getConnection().prepareStatement(sqlWithQuestionMarks);
		  aggregator.setJdbcQuestionMarks(stmt);
		  return stmt.execute();
	}
}
