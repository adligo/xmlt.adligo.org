package org.adligo.xml.parsers;

/**
 * Description:  This is a utility class that holds static methods used for
 *                parseing xml.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

public class Parser {
  static boolean bDebug = true;
  /**
  * This method returns the attribute value for the give attribute
  * from inside a xml tag header.  For instance if the parameters passed in
  * were "<foo id=\"123\" >","id" respectively this method would
  * return "123".
  */
  static public String getAttributeValue(String tagHeader, String attrName) {
    if (attrName.length() != 0 && tagHeader.length() != 0 &&
    tagHeader.indexOf(attrName) != -1) {
      int iAttrStart, iAttrEnd, iQuoteStart, iQuoteEnd, iEqualsSign;

      iAttrStart = tagHeader.indexOf(attrName);
      iAttrEnd = iAttrStart + attrName.length() - 1;

      //check for a equals sign
      iEqualsSign = tagHeader.indexOf("=", iAttrEnd);
      iQuoteStart = tagHeader.indexOf("\"", iAttrEnd);
      if (iEqualsSign > iAttrEnd  && iEqualsSign < iQuoteStart) {
        iQuoteEnd = tagHeader.indexOf("\"", iQuoteStart + 1);
        return new String(tagHeader.substring(iQuoteStart + 1, iQuoteEnd));
      }
    }
    return null;
  }

  /**
  * This returns a int array with the indexes of the header and ender Strings
  * respectively.
  * [0] = the starting index of the first instance of the header tag
  * [1] = the starting index of the respective ending tag
  * For instance passing in "<a> <a></a> <b></b> </a> <c></c> <a></a> <b></b>"
  * ,"<a","</a>" would return a int array x with
  * x[0] = 0, x[1] = 19
  * note the second parameter should not pass any header information
  * after the tag name!
  *
  *  Note that the header tags should have a space for the last character to prevent confulsion
  *  between "<s" and "<sa"
  */
  static public int[] getTagIndexs(String stuff, String header, String ender) {
    //say("Stuff = " + stuff + "\n" + header + "\n" + ender);
    int iStart, iEnd;

    iStart = stuff.indexOf(header);
    iEnd = stuff.indexOf(ender, iStart);
    if ( iStart != -1 && iEnd != -1) {
      while ( !couple(iStart, iEnd, stuff, header, ender)) {
        iEnd = stuff.indexOf(ender, iEnd + 1);
      }
    } else if ( iStart == -1 && iEnd == -1) {
      return new int [] {-1, -1};
    }
    //say("param returned = " + stuff.substring(iStart, iEnd + ender.length()));
    return new int [] {iStart, iEnd + ender.length()};
  }
  /**
   * this method checks to see if the substring of the string parameter defined by s.substring(p,p1)
   * determines a xml tag
   * @param p the possible start of the xml tag
   * @param p2 the possible end of the xml tag
   * @param s the string with the data in question
   */
  static public boolean couple(int p, int p2, String s, String header, String ender) {
    int iNestedHeaderTags = 0;
    int iNestedEnderTags = 0;
    int iLastHeader = 0;
    int iLastEnder = 0;

    String xmlTag = s.substring(p, p2 + ender.length());
    //say("trying = " + xmlTag);

    while ( xmlTag.indexOf(header, iLastHeader) != -1) {
      iNestedHeaderTags++;
      //say("iNestedHeaderTags = " + iNestedHeaderTags);
      iLastHeader = xmlTag.indexOf(header, iLastHeader);
      iLastHeader++;
    }
    while ( xmlTag.indexOf(ender, iLastEnder) != -1) {
      iNestedEnderTags++;
      //say("iNestedEnderTags = " + iNestedEnderTags);
      iLastEnder = xmlTag.indexOf(ender, iLastEnder);
      iLastEnder++;
    }
    boolean b = false;
    if (iNestedEnderTags == iNestedHeaderTags) {
      b = true;
    }
    return b;
  }

  /**
   * @param p the stuff with phrases that need to be replaced
   * @param p2 the replacement stuff
   * @param p3 the key used for finding where to insert the replacement stuff
   */
  static public String replace(String p, String p2, String p3) {
    //say("replace input \n" + p  + " \n " + p2 + " \n " + p3 );
    String r = new String(p);
    int i = r.indexOf(p3);
    while (i != -1 ) {
      r = r.substring(0,i) + p2 + r.substring(i+p3.length(), r.length());
      i = r.indexOf(p3);
    }
    //say("replace returns \n" + r);
    return r;
  }

  static private void say(String s) {
    if (bDebug == true) {
      System.out.println(s);
    }
   }

  /**
   * @param s the string to apply line insertions to
   * @param space the string to apply to each line
   */
  public static String tab(String s, String space) {
    //System.out.println("tabing\n" + s);
    s = space + s;
    int i = s.indexOf("\n");
    while (i != -1 && i <= s.length() - 2) {
      s = s.substring(0,i + 1) + space + s.substring(i + 1, s.length());
      //System.out.println(i);
      //System.out.println(s);
      i = s.indexOf("\n", i + 1 + space.length());
    }

    //ystem.out.println("end tabing\n");
    return s;
  }

  /**
   * this counts the number of tags in the string
   * the tag should just have its header
   * ie;
   *
   * <object
   * or
   * <template
   *
   * @param sText to count tags from
   * @param sTagHeader the tag to count
   */
  public static int countTags(String sText, String sTagHeader) {
    //System.out.println("countTags text\n" + sText + "\n tag\n" + sTagHeader);
    int i = 0;
    int [] iIndex = getTagIndexs(sText, sTagHeader,">" );
    String sTemp = new String(sText);

    while (iIndex[1] > 1 && ( !(iIndex[0] < 0) && !(iIndex[1] < 0) )) {
      //System.out.println("in Loop" + iIndex[0] + "," + iIndex[1]);
      sTemp = sTemp.substring(iIndex[1],  sTemp.length());
      iIndex =  getTagIndexs(sTemp, sTagHeader,">" );
      i++;
    }
    return i;
  }

  public static String removeAllTags(String s, String sHeader, String sEnder) {
    int [] iIndex = getTagIndexs(s, sHeader,sEnder);

    while (iIndex[1] > 1 && ( !(iIndex[0] < 0) && !(iIndex[1] < 0) )) {
      s = s.substring(0,iIndex[0]) + s.substring(iIndex[1], s.length());
      iIndex = getTagIndexs(s, sHeader,sEnder);
    }
    return s;
  }
}