package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:
 *                This class is (and all classes under the org.adligo namespace)
 *                are open-source software which is protected by the GNU GENERAL PUBLIC LICENSE.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.0
 */
import java.util.Vector;
import org.adligo.xml.Parser;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class ParamTagElement extends TemplateElement {
    static Log log = LogFactory.getLog(ParamTagElement.class);
    String sName = "";
    String sPre = "";
    String sPost = "";
    String sDelimiter = "";
    String sSeparator = "";
    String sNestedSeparator = "";
    Vector vElements  = new Vector(); // A vector of String elements
                                    // and ParamTag (nestedTemplateobject) elements

    void setName(String s) { sName = s; }
    void setPre(String s) { sPre = s; }
    void setPost(String s) { sPost = s; }
    void setDelimiter(String s) { sDelimiter = s; }
    void setSeparator(String s) { sSeparator = s; }
    void setNestedSeparator(String s) { sNestedSeparator = s; }
    void parseTagHeader(String sTagHeader) {
      setName(Parser.getAttributeValue(sTagHeader, Tags.NAME));
      setPre(Parser.getAttributeValue(sTagHeader, Tags.PRE));
      setPost( Parser.getAttributeValue(sTagHeader, Tags.POST));
      setDelimiter(Parser.getAttributeValue(sTagHeader, Tags.DELIMITER));
      setSeparator(Parser.getAttributeValue(sTagHeader, Tags.SEPARATOR));
      setNestedSeparator(Parser.getAttributeValue(sTagHeader, Tags.NESTED_SEPARATOR));
    }
      /**
       * this adds the contense of a string that does not have a param tag
       * inside of it
       * @todo start with this
       * then finish the parseTag code
       * then rewrite the TemplateParserEngine
       */
    private void addNonParamString(String s) {
      int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
      if (iTagIndexes[0] == -1) {
          addNonParamNonOptString(s);
      } else {

      }

    }
    /**
     * this adds the contese of a string with only a value tag
     * and no other tags
     */
    private void addNonParamNonOptString(String s) {
      if (log.isDebugEnabled()) {
        log.debug("addNonParamNonOptString(" + s + ")");
      }
      int iTagIndex = s.indexOf(Tags.VALUE);
      if (iTagIndex == -1) {
          vElements.add(s);
      } else {
         // add the string before the value tag and the value tag and then
         // recurse into this method (there could be more than one value tag)
         vElements.add(TemplateElement.NewTemplateElement(s.substring(0, iTagIndex)));
         vElements.add(TemplateElement.NewTemplateElement(ElementTypes.VALUE_TAG));
         addNonParamNonOptString(s.substring(iTagIndex + Tags.VALUE.length(), s.length()));
      }
    }

    void parseTag(String s) {
      int iEndParamAfterParse = 0;

      int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
      int iEndOfHeader = iTagIndexes[1];
      if (iTagIndexes [0] == -1) {
        // no tag simply a string template with out param tags
        vElements.add(TemplateElement.NewTemplateElement(s));
      } else {
        //the string has a tag
        // add the stuff before the tag
        vElements.add(TemplateElement.NewTemplateElement(s.substring(0, iTagIndexes[0])));

        ParamTagElement pte = new ParamTagElement();
        pte.parseTagHeader(s.substring(iTagIndexes[0], iTagIndexes[1]));

        //set the iTagIndexes variable to the boundrys of the whole param tag
        iTagIndexes = Parser.getTagIndexs(s, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
        if (iTagIndexes[0] == -1) {
          log.warn("This error is usually caused by misplaceing the " +
                   " slashes inside the param ender tag" +
                   "\n ie. the param ender tag should be </param> and not <param/>");
        }
        String sAfterTag = s.substring(iTagIndexes[1], s.length());
        // get the tag without the header and ender tags
        String sParamTag = s.substring(iEndOfHeader, iTagIndexes[1]-Tags.PARAM_ENDER.length());
        pte.parseTag(sParamTag);

        vElements.add(pte);
        // recurse for anything left after the tag
        parseTag(s.substring(iTagIndexes[1], s.length()));
      }
    }

    public TemplateElement getElement(int i) {
        return (TemplateElement) vElements.get(i);
    }
    public int getType() { return ElementTypes.PARAM_TAG; }
    public String getName() { return sName; }
    public String getPre() { return sPre; }
    public String getPost() { return sPost; }
    public String getDelimiter() { return sDelimiter; }
    public String getSeparator() { return sSeparator; }
    public String getNestedSeparator() { return sNestedSeparator; }
}