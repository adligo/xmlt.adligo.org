package org.adligo.xml.parsers.template;

/**
 * Description:  This class is simply used to hold information in a logical
 *              fashion.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import java.util.Vector;
import org.adligo.xml.XMLObject;
import org.adligo.xml.parsers.Parser;
import org.adligo.i.provider.I_TemplateParams;

public class Param implements I_TemplateParams {
  public static final String CLASS_VERSION = new String("1.4");
  private String name;
  private String [] values;
  private I_TemplateParams params;
  private boolean bAlreadyGotMe = false;
  private int [] iaOptions = null;
  private I_TemplateParams parent;
  /**
  * Constructors
  */

  public Param() {}

  public Param(String pName, String [] pValues, I_TemplateParams pParams) {
    name = pName;
    values = pValues;
    params = pParams;
  }
  public Param(String pName, String [] pValues, I_TemplateParams pParams, int [] pOptions) {
    name = pName;
    values = pValues;
    params = pParams;
    iaOptions = pOptions;
  }

  public void setParent(I_TemplateParams p) { parent = p ; }
  public I_TemplateParams getParent() { return parent; }
  public void setName(String s) { name = s; }
  public void setValues(String [] s) { values = s;}
  public void setParams(I_TemplateParams pParams) { params = pParams; }
  public I_TemplateParams getNestedParams() { return params; }
  public String getName() { return name; }
  public String [] getValues() {
    if (values == null) {
      return new String[] {""};
    }
    return values;
  }
  //do nothing for these
  public void First() { bAlreadyGotMe = false;}
  public boolean getNextParam(String s) {
    if (s.equals(name) && bAlreadyGotMe == false ){
      bAlreadyGotMe = true;
      return true;
    }
    return false;
  }

  public String toString() {
    String s = name + " , [" + values + "] ";
    if (params != null) {
    s = s + params.toString();
    }
    return s;
  }
  public void setOptions(int [] p) { iaOptions = p; }
  public int [] getOptions() { return iaOptions; }
  public boolean hasOption( int i) {
    if (iaOptions == null ) {
      return false;
    } else {
      for (int iC = 0; iC < iaOptions.length; iC++) {
        if (iaOptions[iC] == i) {
          return true;
        }
      }
    }
    return false;
  }

  /*************************************** I_XML_Serilizable ***************************************************/
  public String writeXML() { return writeXML(null); }

  public String writeXML(String s) {
    String sTheXML = new String(
                OBJECT_HEADER + " " + CLASS + "=\"" + this.getClass().getName() + "\" " +
                VERSION + "=\"" +  CLASS_VERSION + "\"  ");
    if (s != null) {
      sTheXML = sTheXML + NAME + "=\"" + s + "\"";
    }

    sTheXML = sTheXML + ">\n";
    sTheXML = sTheXML + "   " + OBJECT_HEADER + " " + NAME + "=\"name\">" +
                    this.getName() + OBJECT_ENDER + "\n";
    if (iaOptions != null) {
      sTheXML = sTheXML + "   " + OBJECT_HEADER + " " + NAME + "=\"iaOptions\">\n";
      for (int i = 0; i < iaOptions.length; i++) {
          sTheXML = sTheXML + "      " + ELEMENT_HEADER + ">" + iaOptions[i] + ELEMENT_ENDER +"\n";
      }
      sTheXML = sTheXML + "   " + OBJECT_ENDER + "\n";
    }
    if (values != null) {
      sTheXML = sTheXML + "    " + OBJECT_HEADER + " " + NAME + "=\"values\" " + ">\n";
      for (int i = 0; i < values.length; i++) {
          sTheXML = sTheXML + "      " + ELEMENT_HEADER + ">" + (String) values[i] + ELEMENT_ENDER +"\n";
      }
      sTheXML = sTheXML + "   " + OBJECT_ENDER + "\n";
    }
    if (params != null) {
      sTheXML = sTheXML + Parser.tab(params.writeXML("params"),"   ");
    }
    //System.out.println("returning\n" + sTheXML + "</object>\n");
    return sTheXML + OBJECT_ENDER + "\n";
  }

  public void readXML(String s) {
    //System.out.println("Param.readXML\n" + s);
    int [] iaObject = Parser.getTagIndexs(s,OBJECT_HEADER, OBJECT_ENDER); // get first object tag
    int [] iaHeader = Parser.getTagIndexs(s,OBJECT_HEADER, ">" ); // get first header tag
    for (int i = 0; i < 4; i++) {
      if (iaHeader[1] > 10 && iaHeader[0] > 0 && iaHeader[1] > 0) {
        String sName = Parser.getAttributeValue(s.substring(iaHeader[0], iaHeader[1]), NAME);
        parseObject(sName, s.substring(iaObject[0], iaObject[1]));
        s = s.substring(iaObject[1], s.length());
        iaObject = Parser.getTagIndexs(s,OBJECT_HEADER, OBJECT_ENDER); // get next object tag
        iaHeader = Parser.getTagIndexs(s,OBJECT_HEADER, ">" ); // get next header tag
      }
    }
  }

  private void parseObject(String sName,String sObjectXML) {
    if(sName.equals("name") ) {
      parseName(sObjectXML);
    } else if (sName.equals("values")) {
      parseValues(sObjectXML);
    } else if (sName.equals("iaOptions")) {
      parseOptions(sObjectXML);
    } else if (sName.equals("params")) {
      parseParams(sObjectXML);
    }
  }

  private void parseName(String s) {
    //System.out.println("parseName:\n" + s + "\n End parseName");
    s = s.substring(Parser.getTagIndexs(s, OBJECT_HEADER, ">")[1], s.length());
    int iLastChar = s.indexOf(OBJECT_ENDER);
    name = s.substring(0, iLastChar);
    //System.out.println("Setting Name:" + name);
  }

  private void parseValues(String s) {
    //System.out.println("parseValues:\n" + s + "\nEnd parseValues:");
    int iCount = Parser.countTags(s, ELEMENT_HEADER);
    values = new String[iCount];
    int [] iHeader = Parser.getTagIndexs(s, ELEMENT_HEADER, ">");
    int iElementEnder = s.indexOf(ELEMENT_ENDER);

    for (int i = 0; i < iCount; i++) {
      //System.out.println("Param.parseValues" + s);
      values[i] = s.substring(iHeader[1], iElementEnder);
      //System.out.println("added value\n" + values[i]);
      s = s.substring(iElementEnder + ELEMENT_ENDER.length(), s.length());
      iElementEnder = s.indexOf(ELEMENT_ENDER);
      iHeader =  Parser.getTagIndexs(s, ELEMENT_HEADER,">");
    }
    //System.out.println("values:" + values);
  }

  private void parseOptions(String s) {
    //System.out.println("parseValues:\n" + s + "\nEnd parseValues:");
    int iCount = Parser.countTags(s, ELEMENT_HEADER);
    values = new String[iCount];
    int [] iHeader = Parser.getTagIndexs(s, ELEMENT_HEADER, ">");
    int iElementEnder = s.indexOf(ELEMENT_ENDER);
    iaOptions = new int[iCount];

    for (int i = 0; i < iCount; i++) {
      //System.out.println("Param.parseValues" + s);
      iaOptions[i] = Integer.parseInt(s.substring(iHeader[1], iElementEnder));
      //System.out.println("added value\n" + values[i]);
      s = s.substring(iElementEnder + ELEMENT_ENDER.length(), s.length());
      iElementEnder = s.indexOf(ELEMENT_ENDER);
      iHeader =  Parser.getTagIndexs(s, ELEMENT_HEADER,">");
    }
    //System.out.println("values:" + values);
  }

  private void parseParams(String s) {
    //System.out.println("parseParams:\n" + s + "\nEnd parseParams");
    int [] iaObject = Parser.getTagIndexs(s, OBJECT_HEADER, OBJECT_ENDER);
    params = (I_TemplateParams) XMLObject.readXML(s.substring(iaObject[0],iaObject[1]));
  }

  public String getClassVersion() { return CLASS_VERSION; }
  /*************************************** END I_XML_Serilizable ***************************************************/
}