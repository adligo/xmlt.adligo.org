package org.adligo.xml.parsers.template.jdbc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.adligo.models.params.client.I_Operators;
import org.adligo.models.params.client.SqlOperators;

public class BaseSqlOperators {
	public static final Set<I_Operators> OPERATORS = getAllOperators();
	
	private static Set<I_Operators> getAllOperators() {
		Set<I_Operators> toRet = new HashSet<I_Operators>();
		toRet.add(SqlOperators.EQUALS);
		toRet.add(SqlOperators.LIKE);
		toRet.add(SqlOperators.IN);
		toRet.add(SqlOperators.NOT_IN);
		toRet.add(SqlOperators.GREATER_THAN);
		toRet.add(SqlOperators.GREATER_THAN_EQUALS);
		toRet.add(SqlOperators.LESS_THAN);
		toRet.add(SqlOperators.LESS_THAN_EQUALS);
		toRet.add(SqlOperators.NOT_EQUALS);
		toRet.add(SqlOperators.IS_NULL);
		toRet.add(SqlOperators.IS_NOT_NULL);
		toRet.add(SqlOperators.ASCENDING);
		toRet.add(SqlOperators.DESCENDING);
		return Collections.unmodifiableSet(toRet);
	}
}
