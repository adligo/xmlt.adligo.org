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
import org.adligo.xml.Parser;
import java.lang.StringIndexOutOfBoundsException;
import org.adligo.i.persistence.I_TemplateParams;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class TemplateParserEngine {
  static final Log log = LogFactory.getLog(TemplateParserEngine.class);

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

  /**
   * @deprecated use Template I_TemplateParams
   */
  static public String parse(String s, I_TemplateParams params)   {

    String r = parseInternal(new Template(s), params);
    //System.out.println("parse returns \n" + r);
    return r;
  }

  static private String parseInternal(Template template, I_TemplateParams params) {
    StringBuffer sb = new StringBuffer();

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

  static private void addParamStuff(String sNestedSeparator, I_TemplateParams params,
  StringBuffer sb, ParamTagElement pte) {
      if (log.isDebugEnabled()) {
        log.debug("entering addParamStuff NestedSeparator = " + sNestedSeparator);
        log.debug(" param name = " +  pte.getName());
        log.debug(" \n\n\n");
      }

      params.First();
      if ( params.getNextParam( pte.getName())) {
        sb.append(sNestedSeparator);
        sb.append(pte.getPre());
        boolean bFirstOne = true;
        boolean bFirstParam = true;
        do {
          if (bFirstOne == false) {
            sb.append(pte.getSeparator());
          }
          bFirstOne = false;
          for (int pi = 0; pi < pte.getElementCount(); pi++) {
            TemplateElement te_nested = pte.getElement(pi);
            switch (te_nested.getType()){
              case ElementTypes.STRING: sb.append(te_nested.toString()); break;
              case ElementTypes.VALUE_TAG:
                  String [] values = params.getValues();
                  for (int vi = 0; vi < values.length; vi++) {
                    if (vi >= 1) {
                      sb.append(pte.getDelimiter());
                    }
                    sb.append(values[vi]);
                  }
                  break;
              case ElementTypes.OPT_TAG:
                  int [] iaOptions = params.getOptions();
              	  if (iaOptions != null) {
	                  OptTagElement ote = (OptTagElement) te_nested;
	                  for (int oi = 0; oi < iaOptions.length; oi++) {
	                    if (iaOptions[oi] == ote.getId()) {
	                      sb.append(ote.getStringValue());
	                    }
	                  }
              	  }
                  break;
              case ElementTypes.PARAM_TAG:
                  if (bFirstParam == true) {
                    addParamStuff("",  params.getNestedParams(), sb, (ParamTagElement) te_nested);
                    bFirstParam = false;
                  } else {
                    addParamStuff(pte.getNestedSeparator(),  params.getNestedParams(),
                            sb, (ParamTagElement) te_nested);
                  }
                  break;
            }
          }
        } while (params.getNextParam( pte.getName()));
        sb.append(pte.getPost());
      }
  }

}
