package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description: <p>This is a generic and reuseable implementation of the
 *              I_TemplateParams interface.  It relies on the TemplateParam
 *              class for storing the name, values and nested I_TemplateParams.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

import java.util.Vector;
import org.adligo.xml.*;
import java.lang.reflect.*;
import org.adligo.xml.parsers.Parser;
import org.adligo.i.persistence.I_TemplateParams;
import org.adligo.i.persistence.I_MultipleParamsObject;
import org.adligo.i.persistence.I_XML_Serilizable;

public class Params implements  I_MultipleParamsObject {
  public static final String CLASS_VERSION = new String("1.4");
  public Vector vParams = new Vector();// holds TemplateParam objects
  public int iParam = 0; // the current starting point for searching through vParams
  public int iTimesForThisParam = 0;
  public I_TemplateParams param; // the current param that was selected by
                       //getNextParam(String s)

  /** Constructors */
  public Params() {}
  /**
   * This creates a Param object using the parameters and adds it to
   * the Vector of Param objects.
   */
  public void addParam(String name, String[] values, I_TemplateParams params) {
    addParam(new Param(name,values, params));
  }
  /**
   * This creates a Param object using the parameters and adds it to
   * the Vector of Param objects.
   */
  public void addParam(String name, String[] values, I_TemplateParams params, int [] pOptions) {
    addParam(new Param(name,values, params, pOptions));
  }
  /**
   * Adds a I_TemplateParams to the vector of params
   */
  public void addParam(I_TemplateParams p) {
    vParams.add(p);
    try {
      ((Param) p).setParent((I_TemplateParams) this);
    } catch (ClassCastException x) {}
  }
  /**
   * This removes the TemplateParam parameter to
   * the Vector of TemplateParam objects.
   */
  public void removeParam(I_TemplateParams p) { vParams.remove(p); }

  /**
   *  Implementation of I_TemplateParams see the interfaces documentation.
   */
  public void First() { iParam = 0; }
  /**
   *  Implementation of I_TemplateParams see the interfaces documentation.
   */
  public String [] getValues() {
    if (param != null) {
      return param.getValues();
    }
    return null;
  }
  /**
   *  Implementation of I_TemplateParams see the interfaces documentation.
   */
  public int [] getOptions() {
    if (param != null) {
      return param.getOptions();
    }
    return null;
  }

  public String getName() {
    String r = new String("");
    if (param != null) {
      r = param.getName();
    }
    return r;
  }
  /**
   *  Implementation of I_TemplateParams see the interfaces documentation.
   */
  public I_TemplateParams getNestedParams() {
    if (param != null ) {
      return param.getNestedParams();
    }
    return null;
  }
  /**
   *  Implementation of I_TemplateParams see the interfaces documentation.
   */
  public boolean getNextParam(String s) {
    if (s == null) {
      return false;
    }
    String sName = new String();
    //System.out.println("getNextParamFool =" + s);
    //System.out.println("starting at index =" + iParam);
    int iSize = vParams.size();
    for (int i = iParam; iSize > i; i++) {
        sName = ((I_TemplateParams) vParams.elementAt(i)).getName();
        //System.out.println(sName);
        if (sName == null) {
          return false;
        }
        if ( sName.equals(s) ) {
          param = (I_TemplateParams) vParams.elementAt(i);
          try {
            I_MultipleParamsObject i_mpo = (I_MultipleParamsObject) param;
            param.getNextParam(s);
          } catch (ClassCastException e) {}
          //System.out.println("returned " + i);
          iParam = i + 1;
          return true;
        }
    }
    //System.out.println("getNextParamFool =" + s);
    return false;
  }

  public String toString() {
    String s = "Params to String \n";
    for (int i = 0; i < vParams.size(); i++) {
      s = s + vParams.get(i).toString();
    }
    return s;
  }
  /*************************************** I_XML_Serilizable ***************************************************/
  public String writeXML() { return writeXML(null); }

  public String writeXML(String s) {
    String sTheXML = new String(
                OBJECT_HEADER + " " + CLASS + "=\"" + this.getClass().getName() + "\" " +
                VERSION + "=\"" +  CLASS_VERSION + "\"  " );
    if (s != null) {
      sTheXML = sTheXML + NAME + "=\"" + s + "\" ";
    }
    sTheXML = sTheXML + ">\n";

    sTheXML = sTheXML + "   " + OBJECT_HEADER+ " " + NAME + "=\"vParams\" " + ">\n";
    for (int i = 0; i < vParams.size(); i++) {
       sTheXML = sTheXML + Parser.tab( ((I_XML_Serilizable) vParams.elementAt(i)).writeXML(),"      ");
       //System.out.println(sTheXML);
    }
    sTheXML = sTheXML + "   " + OBJECT_ENDER + "\n";
    //System.out.println("returning\n" + sTheXML + "</object>\n");
    return sTheXML + OBJECT_ENDER + "\n";
  }

  public void readXML(String s) {
    //System.out.println("Reading XML in Params\n" + s);
    int [] iaVectorTags = Parser.getTagIndexs(s,OBJECT_HEADER, OBJECT_ENDER ); // get vector element
    s = s.substring(iaVectorTags[0], iaVectorTags[1]);
    int [] iaVectorHeader = Parser.getTagIndexs(s,OBJECT_HEADER, ">" ); // get vector header
    String sVectorHeader = s.substring(iaVectorHeader[0], iaVectorHeader[1]);
    //Make sure this is the vParams Vector object
    if (Parser.getAttributeValue(sVectorHeader, NAME).equals("vParams") ) {
      int [] iaObject = Parser.getTagIndexs(s, OBJECT_HEADER, ">" );
      s = s.substring(iaObject[1] + 1, s.length()); // remove object header name=vParmas
      String sVectorObject = s.substring(iaObject[0], iaObject[1]);//get first object in vParams vector
      iaObject = Parser.getTagIndexs(s, OBJECT_HEADER, OBJECT_ENDER );

      while (iaObject[1] > 10 && iaObject[0] >= 0) {
        sVectorObject = s.substring(iaObject[0], iaObject[1]);
        this.addParam((I_TemplateParams) XMLObject.readXML(sVectorObject));
        s = s.substring(iaObject[1] + 1, s.length());
        iaObject = Parser.getTagIndexs(s, OBJECT_HEADER, OBJECT_ENDER );
      }
    }
  }

  public String getClassVersion() { return CLASS_VERSION; }
  /*************************************** END I_XML_Serilizable ***************************************************/
}