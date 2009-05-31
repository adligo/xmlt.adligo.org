package org.adligo.xml.parsers.template.jdbc;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.i.util.client.ArrayCollection;

public class JdbcAggregator {
	private static final Log log = LogFactory.getLog(JdbcAggregator.class);
	
	
	private ArrayCollection allValues = new ArrayCollection();
	private ArrayCollection allValueTypes = new ArrayCollection();
	
	public void addValue(short type, Object o) {
		allValueTypes.add(type);
		allValues.add(o);
	}
	
	public Object getValue(int i) {
		return allValues.get(i);
	}
	
	public short getType(int i) {
		return (Short) allValueTypes.get(i);
	}
	
	public int size() {
		return allValues.size();
	}
	
	
}
