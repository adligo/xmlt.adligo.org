package org.adligo.xml.parsers.template;

/**
 * Description:  This holds the tags and attributes for use in the other classes.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

public class Tags {
  //Tags
  static public String TEMPLATE_HEADER = new String("<template ");
  static public String TEMPLATE_ENDER = new String("</template>");
  static public String PARAM_HEADER = new String("<param ");
  static public String PARAM_ENDER = new String("</param>");
  static public String OPT_HEADER = new String("<opt ");
  static public String OPT_ENDER = new String("</opt>");
  static public String VALUE = new String("<value/>");

  // Param Attributes
  static public String NAME = new String("name");
  /**
   * The Pre tag sepcifys text before the replacements for all text replaceing the param tag
   * See Documentation for the SEPERATOR tag for a illistruation
   */
  static public String PRE = new String("pre");
  /**
   * The Post tag sepcifys text after the replacements for all text replaceing the param tag
   * See Documentation for the SEPERATOR tag for a illistruation
   */
  static public String POST = new String("post");
  /**
   * The Delimiter tag sepcifys text between the <value/> tag
   * For instance if the tag was <param name="1" delimiter=","> Foo IN (<value/>) </param>
   * the result for a I_TemplateParams object with name="1" and values [] = {"1","2","3"}would be
   * Foo IN (1,2,3)
   */
  static public String DELIMITER = new String("delimiter");
  /**
   * The seperator tag specifys text between replacements
   * of the current param;
   * For instance if there was a tag <param name"1" pre="(" post=")" separator=" SP ">foo</param>
   * and two I_TemplateParams with the name 1 in the I_Template Params Object the result would be;
   * (foo SP foo)
   */
  static public String SEPARATOR = new String("separator");
  /**
   * The NESTED_SEPERATOR tag sepcifys text between the nested param tags
   * For instance if the tags were;
   * <param name="1" nested_seperator=" XNOR ">
   *  <param name="2">PARAM_2</param>
   *  <param name="3">PARAM_3</param>
   * </param>
   * and you had a I_TemplatParams object with a param name=1 and two nested I_TemplatParams
   * object with names 2 and 3 the result would be
   * PARAM_2 XNOR PARAM_3
   */
  static public String NESTED_SEPARATOR = new String("nested_separator");
}