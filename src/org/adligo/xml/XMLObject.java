package org.adligo.xml;

/**
 * Description:  A generic object that can write itself to a xml file
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import java.lang.reflect.*;
import java.util.Vector;
import org.adligo.xml.parsers.Parser;
import org.adligo.i.persistence.I_XML_Serilizable;

import org.apache.commons.logging.*;

public class XMLObject {
  private static final Log log = LogFactory.getLog(XMLObject.class);
  public static final String XML_OBJECT_VERSION = new String("1.0");

  /**
   * this method will create a object that implements the I_XML_Serilizable interface
   * from the serilized xml
   */
  public static Object readXML(String s){
    if (s == null) {
        log.fatal(" XMLObject.readXML(String s) was passed a null string");
        return null;
    }
    if (s.trim().length() == 0 ) {
        log.fatal(" XMLObject.readXML(String s) was passed a empty string");
        return null;
    }
    int [] iaObjectHeader = Parser.getTagIndexs(s, I_XML_Serilizable.OBJECT_HEADER, ">");
    String sClass = Parser.getAttributeValue(s.substring(iaObjectHeader[0], iaObjectHeader[1]), I_XML_Serilizable.CLASS);
    String sVersion = Parser.getAttributeValue(s.substring(iaObjectHeader[0], iaObjectHeader[1]), I_XML_Serilizable.VERSION);
    Object o = null;

    iaObjectHeader = Parser.getTagIndexs(s,I_XML_Serilizable.OBJECT_HEADER, ">" );
    s = s.substring(iaObjectHeader[1], s.length()); //strip off object header tag
    try {
      //System.out.println("classis:");
      //System.out.println(sClass);
      Class c = Class.forName(sClass);
      Constructor ct = c.getConstructor(null);
      o = ct.newInstance(null);
      if (! ((I_XML_Serilizable) o ).getClassVersion().equals(sVersion)) {
        log.fatal("The version of the class in your JVM is different than \n the version saved " +
                        "in your xml!  The object can't be instantiated!");
        return null;
      } else {
        ((I_XML_Serilizable) o ).readXML(s);
      }
    } catch (ClassNotFoundException f) {
      System.out.print(f.toString());
      f.printStackTrace();
    } catch (NoSuchMethodException n) {
      System.out.print(n.toString());
      n.printStackTrace();
    } catch (java.lang.reflect.InvocationTargetException i) {
      System.out.print(i.toString());
      i.printStackTrace();
    } catch (IllegalAccessException a) {
      System.out.print(a.toString());
      a.printStackTrace();
    }  catch (InstantiationException ie) {
      System.out.print(ie.toString());
      ie.printStackTrace();
    }
    return o;
  }
}