package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:
 *                This class is (and all classes under the org.adligo namespace)
 *                are open-source software which is protected by the GNU GENERAL PUBLIC LICENSE.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import org.adligo.xml.Parser;


public class OptTagElement extends TemplateElement{
  String sStringValue = "";
  String sContent = "";
  int id = -1;

  /**
   * @param s the whole opt tag ie
   * <opt id="2">NOT</opt>
   */
  public OptTagElement(String s) {
  	sStringValue = s;
    int [] tags = Parser.getTagIndexs(s, Tags.OPT_HEADER, ">");
    id = Integer.parseInt(Parser.getAttributeValue(s.substring(tags[0], tags[1]),"id"));
    sContent = s.substring(tags[1], s.length() - Tags.OPT_ENDER.length());
  }
  public int getId() { return id; }
  public int getType() { return ElementTypes.OPT_TAG; }
  public String getStringValue() { return sStringValue; }
  public String getContent() { return sContent; }

}