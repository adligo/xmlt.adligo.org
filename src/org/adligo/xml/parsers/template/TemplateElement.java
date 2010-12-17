package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

public class TemplateElement {
  String sStringValue = "";
  int iType = ElementTypes.STRING;

  public TemplateElement() {}

  protected void setStringValue(String s) {
    sStringValue = new String(s);
  }

  public int getType() { return iType; }
  public String getStringValue() {
    if (iType == ElementTypes.VALUE_TAG) {
      return Tags.VALUE;
    }
    return sStringValue;
  }
  public String toString() { return getStringValue(); }
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
  
  public static TemplateElement NewTemplateElement(int i, String s) {
	    TemplateElement te = new TemplateElement();
	    te.setType(i);
	    te.setStringValue(s);
	    return te;
   }
}