package org.adligo.xml.parsers.template.jdbc;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.i.util.client.ArrayCollection;

/**
 * this class simply flattens out all the parameters from the 
 * tree structure of the Params class so 
 * sequential processing can occure during 
 * ? replcement for Jdbc prepaired statements 
 * (or :1, :2 replacement for JPQL)
 * 
 * @author scott
 *
 */
public class QueryParameterAggregator {
	private static final Log log = LogFactory.getLog(QueryParameterAggregator.class);
	
	
	private ArrayCollection allValues = new ArrayCollection();
	private ArrayCollection allValueTypes = new ArrayCollection();
	
	public void addValue(short type, Object o) {
		if (log.isDebugEnabled()) {
			log.debug("adding value " + type + "," + o);
		}
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