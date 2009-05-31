package org.adligo.xml.parsers.template.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.adligo.xml.parsers.template.TemplateParserEngine;

public class JdbcTemplateParserEngine {

	static public ResultSet query(JdbcEngineInput values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  JdbcAggregator aggregator = new JdbcAggregator();
		  JdbcParamsDecorator jdbcParams =	new JdbcParamsDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  PreparedStatement stmt = values.getConnection().prepareStatement(sqlWithQuestionMarks);
		  JdbcPopulator.setJdbcQuestionMarks(aggregator, stmt);
		  ResultSet result = stmt.executeQuery();
		  return result;
	}
	
	static public boolean execute(JdbcEngineInput values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  JdbcAggregator aggregator = new JdbcAggregator();
		  JdbcParamsDecorator jdbcParams =	new JdbcParamsDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  PreparedStatement stmt = values.getConnection().prepareStatement(sqlWithQuestionMarks);
		  JdbcPopulator.setJdbcQuestionMarks(aggregator, stmt);
		  return stmt.execute();
	}
}
