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

public class TemplateElement {
  String sStringValue = "";
  int iType = ElementTypes.STRING;

  public TemplateElement() {}

  protected void setStringValue(String s) {
    sStringValue = s;
  }

  public int getType() { return iType; }
  public String getStringValue() { return sStringValue; }
  void setType(int i) { iType = i; }

  public static TemplateElement NewTemplateElement(String s) {
    TemplateElement te = new TemplateElement();
    te.setStringValue(s);
    return te;
  }

  public static TemplateElement NewTemplateElement(int i) {
    TemplateElement te = new TemplateElement();
    te.setType(i);
    return te;
  }
}