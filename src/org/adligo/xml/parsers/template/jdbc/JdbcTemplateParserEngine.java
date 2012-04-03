package org.adligo.xml.parsers.template.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.adligo.xml.parsers.template.TemplateParserEngine;

public class JdbcTemplateParserEngine {

	static public JdbcQueryResult query(JdbcEngineInput values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  QueryParameterAggregator aggregator = new QueryParameterAggregator();
		  JdbcParamsDecorator jdbcParams =	new JdbcParamsDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  jdbcParams.clear();
		  Connection conn = values.getConnection();
		  values.clear();
		  
		  PreparedStatement stmt = conn.prepareStatement(sqlWithQuestionMarks);
		  try {
			  JdbcPopulator.setJdbcQuestionMarks(aggregator, stmt);
			  ResultSet result = stmt.executeQuery();
			  return new JdbcQueryResult(stmt, result);
		  } finally {
			  aggregator.clear();
		  }
	}

	static public boolean execute(JdbcEngineInput values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  QueryParameterAggregator aggregator = new QueryParameterAggregator();
		  JdbcParamsDecorator jdbcParams =	new JdbcParamsDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  jdbcParams.clear();
		  Connection conn = values.getConnection();
		  values.clear();
		  PreparedStatement stmt = null;
		  try {
			  stmt = conn.prepareStatement(sqlWithQuestionMarks);
			  
			  JdbcPopulator.setJdbcQuestionMarks(aggregator, stmt);
			  
			  boolean toRet = stmt.execute();
			  return toRet;
		  } finally {
			  if (stmt != null) {
				  stmt.close();
			  }
			  aggregator.clear();
		  }
	}

	static public int executeUpdate(JdbcEngineInput values) throws SQLException  {
		  //does a null check for connection, params, and template
		  // allowed operators is a internally managed set (never null)
		  values.validate();
		  QueryParameterAggregator aggregator = new QueryParameterAggregator();
		  JdbcParamsDecorator jdbcParams =	new JdbcParamsDecorator(values.getParams(), 
				  values.getAllowedOperators(), aggregator);
		  String sqlWithQuestionMarks = TemplateParserEngine.parse(values.getTemplate(), jdbcParams);
		  
		  jdbcParams.clear();
		  Connection conn = values.getConnection();
		  values.clear();
		  
		  PreparedStatement stmt = null;
		  try {
		  	  stmt = conn.prepareStatement(sqlWithQuestionMarks);
			  JdbcPopulator.setJdbcQuestionMarks(aggregator, stmt);
			  int toRet = stmt.executeUpdate();
			  return toRet;
		  } finally {
			  if (stmt != null) {
				  stmt.close();
			  }
			  aggregator.clear();
		  }
	}
	
	
}
