package org.adligo.xml.parsers.template.jdbc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * this is a postgres implementation
 * 
 * this is not threadsafe!
 * 
 * @author scott
 *
 */
public class DefaultPrettyQuery implements I_PrettyQuery {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
	
	@Override
	public String prepairBoolean(Boolean p) {
		if (p) {
			return "1";
		} else {
			return "0";
		}
	}

	@Override
	public String prepairDate(Date p) {
		return "'" + sdf.format(p) + "'";
	}

	@Override
	public String prepairString(String p) {
		char [] ca = p.toCharArray();
	    String sR = new String();
	    for (int i = 0; i < ca.length; i++) {
	      if ( ca[i] == '\\') {
	        sR = sR + "\\\\";
	      } else if ( ca[i] == '\'') {
	        sR = sR + "\\'";
	      } else {
	        sR = sR + ca[i];
	      }
	    }
	    return sR;
	}

}
