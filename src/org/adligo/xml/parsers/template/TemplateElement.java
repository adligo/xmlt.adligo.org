package org.adligo.xml.parsers.template;

/**
 * a immutable class that represents freform text inside of the xml
 * 
 * Title:
 * Description:
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

public class TemplateElement {
  private String sStringValue = "";
  private int iType = ElementTypes.STRING;

  public TemplateElement() {}
  public TemplateElement(String value) {
	  sStringValue = value;
  }
  
  private void setStringValue(String s) {
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
  private void setType(int i) { iType = i; }

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