package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:  The TemplateParserEngine is used to parse templates and
 *              replace the param tags with information from the
 *              I_TemplateParams interface.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import org.adligo.xml.parsers.Parser;
import java.lang.StringIndexOutOfBoundsException;
import org.adligo.i.persistence.I_TemplateParams;

public class TemplateParserEngine {

  static public String parse(String s, I_TemplateParams params)   {

    String r = parseInternal(s, params, null, false);
    //System.out.println("parse returns \n" + r);
    return r;
  }

   /**
   * This takes a string of a particular template and uses the information
   * in the I_TemplateParams object to replace the param tags with runtime info.
   *
   * For instance if the following was passed in as the sTemplate:
   * SELECT <param name="maxrows"> TOP <value> </param>
   * fname, mname, lname, nickname, birthday, comment
   * FROM persons p
   * <param name="where" nested_separator=" OR "> WHERE
   *   <param name="oid" separator=","> oid IN (<value>) </param>
   *   <param name="fname" separator=","> fname LIKE <value> </param>
   * </param>
   *
   * And the I_TemplateParams argument was null the following would be returned;
   * SELECT fname, mname, lname, nickname, birthday, comment
   * FROM persons p
   *
   * Note: that some line breaks will be omited.
   *
   * If the the following sudo code occurred with the I_TemplateParams params
   * params.getNextParam("where") = true
   * params.getNestedParams() = whereParams
   * whereParams.getNextParam("oid") = true
   * whereParams.getValues() = "1","2","3"
   * whereParams.getNextParam("fname") = true 2 times
   * whereParams.getValues() = "'chris'" (1st time)
   * whereParams.getValues() = "'ulaf'" (2nd time)
   *
   * the following would be returned;
   * SELECT fname, mname, lname, nickname, birthday, comment
   * FROM persons p WHERE oid IN (1 ,2 ,3) OR fname LIKE 'chris' OR
   * fname LIKE 'ulaf'
   *
   */
  static private String parseInternal(String s, I_TemplateParams params, String psNestedSeperator,
  boolean pbNestSep) {
    //say("Called \n" + s + "\n" + psNestedSeperator + " \n" + pbNestSep);
    String sParamTagReplacment = new String("");
    String sReturn = new String("");
    String sDelimiter = new String("");
    int iEndParamAfterParse = 0;

    if (s == null) {
      System.out.println("The string argument passed to TemplateParserEngine.parse was null!");
      new Exception().printStackTrace();
      return new String("");
    }

    int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
    int iEndOfHeader = iTagIndexes[1];
    //say(":" + s);
    if (iTagIndexes [0] != -1) {//the string has a tag

      String sTagHeader = s.substring(iTagIndexes[0], iTagIndexes[1]);
      String sParamName = Parser.getAttributeValue(sTagHeader, Tags.NAME);
      String sPre = Parser.getAttributeValue(sTagHeader, Tags.PRE);
      String sPost = Parser.getAttributeValue(sTagHeader, Tags.POST);
      sDelimiter = Parser.getAttributeValue(sTagHeader, Tags.DELIMITER);
      String sSeparator = Parser.getAttributeValue(sTagHeader, Tags.SEPARATOR);
      String sNestedSeparator = Parser.getAttributeValue(sTagHeader, Tags.NESTED_SEPARATOR);
      //set the iTagIndexes variable to the boundrys of the whole param tag
      //say(":" + s + ":" + iTagIndexes[0] + ":" + iTagIndexes[1] );
      iTagIndexes = Parser.getTagIndexs(s, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
      if (iTagIndexes[0] == -1) {
        say("This error is usually caused by misplaceing the slashes inside the param ender tag" +
                      "\n ie. the param ender tag should be </param> and not <param/>");
      }
      String sPreTag = s.substring(0, iTagIndexes[0]);
      String sPostTag = s.substring(iTagIndexes[1], s.length());
      // get the tag without the header and ender tags
      String sParamTag = s.substring(iEndOfHeader, iTagIndexes[1]-Tags.PARAM_ENDER.length());

      // find out if there are more params nested in the tag
      iTagIndexes= Parser.getTagIndexs(sParamTag, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
      if (params != null ) {
        params.First();
        if ( iTagIndexes [0] != -1 ){ //nested Stuff!

          while (params.getNextParam(sParamName)) {
            if (sParamTagReplacment.length() != 0 && sSeparator != null) {
              sParamTagReplacment = sParamTagReplacment + sSeparator;
            }
            String sTemp = parseInternal(sParamTag, params.getNestedParams(), sNestedSeparator, false);
            sTemp = getReplacementForTag(sTemp, sDelimiter, params.getValues(), params.getOptions());
            sParamTagReplacment = sParamTagReplacment + sTemp;
            //say("nested stuff 2 in loop = \n" + sParamTagReplacment);
          }
        } else {

            // no more tags just <value/> , <opt> and other stuff
          if (params.getNextParam(sParamName)) {
            sParamTagReplacment = sParamTagReplacment + getReplacementForTag(sParamTag, sDelimiter,
                    params.getValues(), params.getOptions());
            while (params.getNextParam(sParamName)) {

              sParamTagReplacment = sParamTagReplacment + sSeparator +
                       getReplacementForTag(sParamTag, sDelimiter, params.getValues(), params.getOptions());
            }
            if (sPre != null) {
              sParamTagReplacment = sPre + sParamTagReplacment;
            }
            if (sPost != null) {
              sParamTagReplacment = sParamTagReplacment + sPost;
            }
          }
        }

      }
      if (psNestedSeperator != null && pbNestSep) {
        if (sParamTagReplacment.length() > 0) {
          sReturn = sPreTag + psNestedSeperator + sParamTagReplacment + sPostTag;
          iEndParamAfterParse = sPreTag.length() + psNestedSeperator.length() + sParamTagReplacment.length();
        } else {
          sReturn = sPreTag  + sPostTag;
          iEndParamAfterParse = sPreTag.length();
        }
      } else {
        sReturn = sPreTag + sParamTagReplacment + sPostTag;
        iEndParamAfterParse = sPreTag.length() + sParamTagReplacment.length();
      }
    } else {// no tag return what was sent in
      sReturn = s;
    }

    iTagIndexes= Parser.getTagIndexs(sReturn, Tags.PARAM_HEADER, ">");
    if ( iTagIndexes [0] != -1 ){ //another tag!
      boolean bSendNestSep = false;
      if ( sParamTagReplacment.length() != 0 || pbNestSep) {
        bSendNestSep = true;
      }
      sReturn = parseInternal(sReturn, params, psNestedSeperator, bSendNestSep);
    }
    return sReturn;
  }

  static public String getReplacementForTag(String tag, String delimiter, String [] values, int [] options) {
    String s = new String("");
    //say("Entering getReplacementForTag \n" + tag);
    if (values != null ) {
      String sValueReplacement = new String("");
      String sDelimiter = new String("");
      if (delimiter != null ) {
        sDelimiter = delimiter;
      }
      if (values.length >= 1) {
        sValueReplacement = values[0];
      }
      for (int i = 1; i < values.length; i++) {
        sValueReplacement = sValueReplacement + sDelimiter + values[i];
      }
      s = Parser.replace(tag, sValueReplacement, Tags.VALUE);
    }
    if (options != null) {
      int [] tags = Parser.getTagIndexs(s, Tags.OPT_HEADER, ">");
      while (tags[0] >= 0 && tags[1] > 0) {
        int iID = Integer.parseInt(Parser.getAttributeValue(s.substring(tags[0], tags[1]),"id"));
        boolean bFoundIt = false;
        for (int i = 0; i < options.length && !bFoundIt; i++) {
          if (options[i] == iID) {
            s = s.substring(0, tags[0]) + s.substring(tags[1], s.indexOf(Tags.OPT_ENDER)) +
                  s.substring(s.indexOf(Tags.OPT_ENDER) + Tags.OPT_ENDER.length(), s.length());
            bFoundIt = true;
          }
        }
        if (!bFoundIt) {
          s = s.substring(0, tags[0]) +
                  s.substring(s.indexOf(Tags.OPT_ENDER) + Tags.OPT_ENDER.length(), s.length());
        }
        tags = Parser.getTagIndexs(s, Tags.OPT_HEADER, ">");
      }
    }
    s = Parser.removeAllTags(s, Tags.OPT_HEADER, Tags.OPT_ENDER);
    //say(" returned \n" + s);
    return s;
  }


  public static void say( String s) {
    System.out.println(s);
  }
}
