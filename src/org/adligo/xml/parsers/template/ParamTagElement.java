package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:  A Immutable class (for usage among multiple threads)
 * 		that represents a param tag from the xml template
 * 
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.0
 */
import java.util.List;
import java.util.ArrayList;

import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.models.params.shared.Parser;

public class ParamTagElement extends TemplateElement {
    private static Log log = LogFactory.getLog(ParamTagElement.class);
    private String sName = "";
    private String sPre = "";
    private String sPost = "";
    private String sDelimiter = "";
    private String sSeparator = "";
    private String sNestedSeparator = "";
    private List<Object> elements  = new ArrayList<Object>(); // A list of String elements
                                    // and ParamTag (nestedTemplateobject) elements
    /**
     * This method should take a complete param tag like
     * <param name="maxrows"> TOP <value/> </param>
     *
     * or one with nested stuff like
     * <param name="where" nested_separator=" AND "> WHERE
     *     <param name="oid" delimiter="," > oid IN (<value/>) </param>
     * </param>
     *
     * and build the object out of it
     */
    public ParamTagElement(String s) {
      if (log.isDebugEnabled()) {
        log.debug("entering parseTag '" + s + "'");
      }
      int [] iTagHeaderIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, ">");
      this.parseTagHeader(s.substring(0, iTagHeaderIndexes[1]));
      this.parseInternal(s.substring(iTagHeaderIndexes[1], s.length() - Tags.PARAM_ENDER.length()));

    }
    private void setName(String s) { sName = nonNull(s); }
    private void setPre(String s) { sPre = nonNull(s); }
    private void setPost(String s) { sPost = nonNull(s); }
    private void setDelimiter(String s) {  sDelimiter = nonNull(s); }
    private void setSeparator(String s) { sSeparator = nonNull(s); }
    private void setNestedSeparator(String s) { sNestedSeparator = nonNull(s); }
    private String nonNull(String s) {
      if (s == null) {
        return "";
      }
      return s;
    }
    private void parseTagHeader(String sTagHeader) {
      if (log.isDebugEnabled()) {
        log.debug("entering parseTagHeader '" + sTagHeader + "'");
      }
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
       * then finish the parseTag code
       * then rewrite the TemplateParserEngine
       */
    private void addNonParamString(String s) {
      if (s.length() == 0) {
        return;
      }
      int [] nextOperatorTagIndexes= Parser.getTagIndexs(s, Tags.OPERATOR_HEADER, "/>");
      int nextValueTagIndex = s.indexOf(Tags.VALUE);
      if (nextOperatorTagIndexes[0] == -1 && nextValueTagIndex == -1) {
          // just a string
          elements.add(TemplateElement.NewTemplateElement(s));
          return;
      } 

      // add the string before the first tag and the first tag and then recurse until
      // s.length = 0
      while (nextOperatorTagIndexes[0] != -1 || nextValueTagIndex != -1) {
    	  	
    	  //figure out whats next, either a 
    	  //String, Value node or Operator node
    	  int nextIndex = nextOperatorTagIndexes[0];
    	  if (nextIndex == -1) {
    		  nextIndex = nextValueTagIndex;
    	  } else if (nextValueTagIndex < nextIndex && nextValueTagIndex != -1) {
    		  nextIndex = nextValueTagIndex;
    	  }
    	  if (nextIndex == 0) {
    		  if (nextValueTagIndex == 0) {
    			  // its a value tag
    			  elements.add(TemplateElement.NewTemplateElement(ElementTypes.VALUE_TAG, Tags.VALUE));
    			  s = s.substring(Tags.VALUE.length(), s.length());
    		  } else if (nextOperatorTagIndexes[0] == 0) {
    			  // its a operator tag
    			  
    			  String id = Parser.getAttribute(nextOperatorTagIndexes, s, Tags.ID_ATTRIBUTE);
    			  if (id == null) {
    				String operatorValue = s.substring(nextOperatorTagIndexes[0], nextOperatorTagIndexes[1]);
      			  	OperatorTagElement operator = new OperatorTagElement(operatorValue);
      			  	elements.add(operator);
    		  	  } else {
    		  		String operatorValue = s.substring(nextOperatorTagIndexes[0], nextOperatorTagIndexes[1]);
    			  	OperatorTagElement operator = new OperatorTagElement(operatorValue, new Integer(id));
    			  	elements.add(operator);  
    		  	  }
    			  s = s.substring(nextOperatorTagIndexes[1], s.length());
    		  } 
    	  } else {
    		//its text and then something
			  String text = s.substring(0, nextIndex);
			  s = s.substring(nextIndex, s.length());
			  elements.add(TemplateElement.NewTemplateElement(text));
    	  }
    	  
    	  nextOperatorTagIndexes= Parser.getTagIndexs(s, Tags.OPERATOR_HEADER, "/>");
          nextValueTagIndex = s.indexOf(Tags.VALUE);
      }
      if (s.length() > 0) {
    	  //traling string
    	  elements.add(TemplateElement.NewTemplateElement(s));
      }
    }
   



    public int getElementCount() { return elements.size(); }
    /** this is the recursive method that finds internal param tags and deals with them
     *
     */
    private void parseInternal(String s) {
      if (log.isDebugEnabled()) {
        log.debug("entering parseInternal '" + s + "'");
      }
      
      int [] iTagIndexes= Parser.getTagIndexs(s, Tags.PARAM_HEADER, Tags.PARAM_ENDER);
      if (iTagIndexes [0] == -1) {
        // no tag simply a string template with out param tags
        // however this may have <value/> and <opt> tags so we must strip those out
        addNonParamString(s);
      } else {
        //the string has a tag
        // add the stuff before the tag
    	String preTag = s.substring(0, iTagIndexes[0]);
      	addNonParamString(preTag);
        //vElements.add(TemplateElement.NewTemplateElement(s.substring(0, iTagIndexes[0])));
        // add the first param tag
      	String tag = s.substring(iTagIndexes[0], iTagIndexes[1]);
        elements.add(new ParamTagElement(tag));
        // recurse for anything left after the tag
        parseInternal(s.substring(iTagIndexes[1], s.length()));
      }
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(Tags.PARAM_HEADER);
      if (getName().length() > 0) {
        sb.append(" ");
        sb.append(Tags.NAME);
        sb.append("=\"");
        sb.append(getName());
        sb.append("\"");
      }
      if (sPre.length() > 0) {
        sb.append(" ");
        sb.append(Tags.PRE);
        sb.append("=\"");
        sb.append(sPre);
        sb.append("\"");
      }
      if (sPost.length() > 0) {
        sb.append(" ");
        sb.append(Tags.POST);
        sb.append("=\"");
        sb.append(sPost);
        sb.append("\"");
      }
      if (sDelimiter.length() > 0) {
        sb.append(" ");
        sb.append(Tags.DELIMITER);
        sb.append("=\"");
        sb.append(sDelimiter);
        sb.append("\"");
      }
      if (sSeparator.length() > 0) {
        sb.append(" ");
        sb.append(Tags.SEPARATOR);
        sb.append("=\"");
        sb.append(sSeparator);
        sb.append("\"");
      }
      if (sNestedSeparator.length() > 0) {
        sb.append(" ");
        sb.append(Tags.NESTED_SEPARATOR);
        sb.append("=\"");
        sb.append(sNestedSeparator);
        sb.append("\"");
      }
      sb.append(">");
      for (int i = 0; i < elements.size(); i++) {
        sb.append(elements.get(i).toString());
      }
      sb.append(Tags.PARAM_ENDER);
      return sb.toString();
    }
    public TemplateElement getElement(int i) {
        return (TemplateElement) elements.get(i);
    }
    public int getType() { return ElementTypes.PARAM_TAG; }
    public String getName() { return sName; }
    public String getPre() { return sPre; }
    public String getPost() { return sPost; }
    public String getDelimiter() { return sDelimiter; }
    public String getSeparator() { return sSeparator; }
    public String getNestedSeparator() { return sNestedSeparator; }
}