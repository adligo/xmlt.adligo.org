package org.adligo.xml.parsers.template.jdbc;

import java.util.Date;

/**
 * this class is to provide the developer
 * a way to make the PrettyParamDecorator work
 * so that the developer can have a 
 * runnable version of the query with out needing
 * to replace all the values
 * 
 * This class IS NOT USED for the actual queries 
 * which use PrepairedStatements,
 * just for creating a nice log message
 * 
 * @author scott
 *
 */
public interface I_PrettyQuery {
	/**
	 * this should do the sql escapes for
	 * single quotes (database specific I think)
	 * 
	 * The JdbcPrettyParamDecorator will
	 *  add the first and last quote for you
	 *  as all databases use this
	 * @param p
	 * @return
	 */
	public String prepairString(String p);
	
	/**
	 * this should create a string representaion 
	 * of the date which is ready for sql
	 * in oracle this may be something like
	 * TO_DATE('10/10/2008')
	 * 
	 * Postgres may simply be
	 * '10/10/2008'
	 * 
	 * exc 
	 * 
	 * @param p
	 * @return
	 */
	public String prepairDate(Date p);
	/**
	 * don't know exactly what this should do, 
	 * since I don't store many booleans
	 * 
	 * @param p
	 * @return
	 */
	public String prepairBoolean(Boolean p);
}
