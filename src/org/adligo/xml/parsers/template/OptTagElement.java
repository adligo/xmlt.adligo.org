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

public class OptTagElement extends TemplateElement{
  String sStringValue = "";

  public OptTagElement() {}

  protected void setStringValue(String s) {
    sStringValue = s;
  }

  public int getType() { return ElementTypes.OPT_TAG; }
  public String getStringValue() { return sStringValue; }


  public static TemplateElement NewOptTagElement(int i, String s) {
    OptTagElement te = new OptTagElement();
    te.setType(i);
    return te;
  }
}