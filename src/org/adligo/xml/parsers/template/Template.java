package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:
 *                This class is (and all classes under the org.adligo namespace)
 *                are open-source software which is protected by the GNU GENERAL PUBLIC LICENSE.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.0
 */
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import org.adligo.xml.parsers.Parser;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class Template {
  static Log log = LogFactory.getLog(Template.class);
  Vector vElements  = new Vector(); // A vector of String elements
                                    // and ParamTag (nested Template object) elements
  TemplateElement te;

  /**
   * @param String sXML = the xml to be parsed into the object
   */
  public Template(String sXML) {
    parseInternal(sXML);
  }

  private void parseInternal(String s) {
    int iEndParamAfterParse = 0;

    if (s == null) {
      log.warn("The string argument passed to Template.parseInternal was null");
    }

    int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
    int iEndOfHeader = iTagIndexes[1];
    if (iTagIndexes [0] == -1) {
      // no tag simply a string template with out param tags
      vElements.add(TemplateElement.NewTemplateElement(s));
    } else {
      //the string has a tag
      // add the stuff before the tag
      vElements.add(TemplateElement.NewTemplateElement(s.substring(0, iTagIndexes[0])));

      ParamTagElement pte = new ParamTagElement();
      pte.parseTagHeader(s.substring(iTagIndexes[0], iTagIndexes[1]));

      //set the iTagIndexes variable to the boundrys of the whole param tag
      iTagIndexes = Parser.getTagIndexs(s, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
      if (iTagIndexes[0] == -1) {
        log.warn("This error is usually caused by misplaceing the " +
                 " slashes inside the param ender tag" +
                 "\n ie. the param ender tag should be </param> and not <param/>");
      }
      String sAfterTag = s.substring(iTagIndexes[1], s.length());
      // get the tag without the header and ender tags
      String sParamTag = s.substring(iEndOfHeader, iTagIndexes[1]-Tags.PARAM_ENDER.length());
      pte.parseTag(sParamTag);

      vElements.add(pte);
      // recurse for anything left after the tag
      parseInternal(s.substring(iTagIndexes[1], s.length()));
    }
  }

  /**
   * @return a boolean true if the element i is a String element
   * false if the element i is a ParamTag (nested Template object) element
   */
  public TemplateElement getElement(int i) {
      te = (TemplateElement) vElements.get(i);
      return te;
  }

  public String getStringValue() { return te.getStringValue(); }
}

