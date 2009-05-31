package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:  The TemplateParserEngine is used to parse templates and
 *              replace the param tags with information from the
 *              I_TemplateParams interface.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.j2se.util.J2SEPlatform;
import org.adligo.models.params.client.I_Operators;
import org.adligo.models.params.client.I_TemplateParams;


public class TemplateParserEngine {
  static final Log log = LogFactory.getLog(TemplateParserEngine.class);

  static {
	  try {
		  //init the wrappers for params and
		  // the adligo log system, this package is 
		  // server or fat client (2teir only)
	  	J2SEPlatform.init();
	  } catch (Exception x) {
		  x.printStackTrace();
	  }
  }
  /**
   * This takes a string of a particular template and uses the information
   * in the I_TemplateParams object to replace the param tags with runtime info.
   *
   * For instance if the following was passed in as the sTemplate:
   * SELECT <param name="maxrows"> TOP <value> </param>
   * fname, mname, lname, nickname, birthday, comment
   * FROM persons p
   * <param name="where" nested_separator=" OR "> WHERE
   *   <param name="oid" separator=","> oid IN (<value>) </param>
   *   <param name="fname" separator=","> fname LIKE <value> </param>
   * </param>
   *
   * And the I_TemplateParams argument was null the following would be returned;
   * SELECT fname, mname, lname, nickname, birthday, comment
   * FROM persons p
   *
   * Note: that some line breaks will be omited.
   *
   * If the the following sudo code occurred with the I_TemplateParams params
   * params.getNextParam("where") = true
   * params.getNestedParams() = whereParams
   * whereParams.getNextParam("oid") = true
   * whereParams.getValues() = "1","2","3"
   * whereParams.getNextParam("fname") = true 2 times
   * whereParams.getValues() = "'chris'" (1st time)
   * whereParams.getValues() = "'ulaf'" (2nd time)
   *
   * the following would be returned;
   * SELECT fname, mname, lname, nickname, birthday, comment
   * FROM persons p WHERE oid IN (1 ,2 ,3) OR fname LIKE 'chris' OR
   * fname LIKE 'ulaf'
   *
   */
  static public String parse(Template template, I_TemplateParams params)   {
    String r = parseInternal(template, params);
    return r;
  }

  static public String parse(EngineInput in)   {
	  in.validate();
	  return parse(in.getTemplate(), in.getParams());
  }

  static private String parseInternal(Template template, I_TemplateParams params) {
    StringBuilder sb = new StringBuilder();

    if (log.isDebugEnabled()) {
        log.debug(" " + template.getElementCount());
    }

    for (int i = 0; i < template.getElementCount(); i++) {
      TemplateElement te = template.getElement(i);
      if (te.getType() == ElementTypes.STRING) {
         // String content
         sb.append(te.toString());
       } else {
          addParamStuff("" ,params, sb,(ParamTagElement) te);
      }
    }
    return sb.toString().trim();
  }

  static private boolean addParamStuff(String sNestedSeparator, I_TemplateParams params,
  StringBuilder sb, ParamTagElement pte) {
      if (log.isDebugEnabled()) {
        log.debug("entering addParamStuff NestedSeparator = " + sNestedSeparator);
        log.debug(" param name = " +  pte.getName());
        log.debug(" \n\n\n");
      }

      if (params != null ) {
	      params.First();
	      if ( params.getNextParam( pte.getName())) {
	        sb.append(sNestedSeparator);
	        sb.append(pte.getPre());
	        boolean bFirstParam = true;
	        boolean bFirstOne = true;
	        do {
	          bFirstParam = true;
	          if (bFirstOne == false) {
	            sb.append(pte.getSeparator());
	          }
	          bFirstOne = false;
	          for (int pi = 0; pi < pte.getElementCount(); pi++) {
	            TemplateElement te_nested = pte.getElement(pi);
	            switch (te_nested.getType()){
	              case ElementTypes.STRING: 
	              		sb.append(te_nested.toString()); 
	              		break;
	              case ElementTypes.VALUE_TAG:
	                  Object [] values = params.getValues();
	                  if (values != null) {
		                  for (int vi = 0; vi < values.length; vi++) {
		                    if (vi >= 1) {
		                      sb.append(pte.getDelimiter());
		                    }
		                    sb.append(values[vi]);
		                  }
	                  }
	                  break;
	              case ElementTypes.OPEARTOR_TAG:
	                  I_Operators operators = params.getOperators();
	              	  if (operators != null) {
	              		  String [] ops = operators.getValues();
	              		  if (ops != null) {
		              		 OperatorTagElement ote = (OperatorTagElement) te_nested;
		              		 int id = ote.getId();
		              		 if (id >= 0 && id < ops.length) {
		              			 sb.append(ops[id]);
		              		 }
	              		  }
	              	  }
	                  break;
	              case ElementTypes.PARAM_TAG:
	                  if (bFirstParam == true) {
	                    if (addParamStuff("",  params.getNestedParams(), sb, 
	                    	(ParamTagElement) te_nested)) {
	                  
	                    	bFirstParam = false;
	                    }
	                  } else {
	                    addParamStuff(pte.getNestedSeparator(),  params.getNestedParams(),
	                            sb, (ParamTagElement) te_nested);
	                  }
	                  break;
	            }
	          }
	        } while (params.getNextParam( pte.getName()));
	        sb.append(pte.getPost());
	        return true;
	      }
      }
      return false;
  }

}
