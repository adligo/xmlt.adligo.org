package org.adligo.xml.parsers.template;

/**
 * Title:
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.List;

import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.models.params.shared.Parser;

/**
 * immutable (so the instance may be shared by multiple threads) 
 * class that represents the xml of the template stored
 * in the computers RAM
 * 
 * @author scott
 *
 */
public class Template {
  private static Log log = LogFactory.getLog(Template.class);
  /*
   *  A list of String elements
   * and ParamTag (nested Template object) elements
   */
  
  private List<Object> elements  = new ArrayList<Object>(); //
 

  /**
   * @param String sXML = the xml to be parsed into the object
   */
  public Template(String sXML) {
    if (log.isDebugEnabled()) {
        log.debug("parseing xml template \n" + sXML);
    }
    parseInternal(sXML);
  }

  private void parseInternal(String s) {
    
    if (s == null) {
      log.warn("The string argument passed to Template.parseInternal was null");
      return;
    }
    if (log.isDebugEnabled()) {
      log.debug("parseInternal \n\n\n" + s + " \n\n\n");
    }
    
    int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
    if (iTagIndexes [0] == -1) {
      // no tag simply a string template with out param tags
    	String text = Parser.unescapeFromXml(s);
      elements.add(TemplateElement.NewTemplateElement(text));
    } else {
      //the string has a param tag
      // add the stuff before the tag
      String teText = s.substring(0, iTagIndexes[0]);
      teText = Parser.unescapeFromXml(teText);
      TemplateElement preHeader = TemplateElement.NewTemplateElement(teText);
      if (log.isDebugEnabled()) {
        log.debug("adding element '" + preHeader.getStringValue() + "'\n");
      }
      elements.add(preHeader);

      //set the iTagIndexes variable to the boundrys of the whole param tag
      iTagIndexes = Parser.getTagIndexs(s, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
      if (iTagIndexes[0] == -1) {
        log.warn("This error is usually caused by misplaceing the " +
                 " slashes inside the param ender tag" +
                 "\n ie. the param ender tag should be </param> and not <param/>");
      }
      // get the tag without the header and ender tags
      String sParamTag = s.substring(iTagIndexes[0], iTagIndexes[1]);
      ParamTagElement pte = new ParamTagElement(sParamTag);

      elements.add(pte);
      // recurse for anything left after the tag
      parseInternal(s.substring(iTagIndexes[1], s.length()));
    }
  }

  /**
   * @return a boolean true if the element i is a String element
   * false if the element i is a ParamTag (nested Template object) element
   */
  public TemplateElement getElement(int i) {
      return (TemplateElement) elements.get(i);
  }
  public int getElementCount() {
    return elements.size();
  }

  public String getStringValue() {
    StringBuffer sb = new StringBuffer();

    for (int i = 0; i < elements.size(); i++) {
      sb.append(elements.get(i).toString());
    }
    return sb.toString();
  }
  public String toString() { return getStringValue(); }
}

