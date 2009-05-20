package org.adligo.xml.parsers.template;

/**
 * Description:  This holds the tags and attributes for use in the other classes.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */

public class Tags {
  private Tags() {}
  //Tags
  public static final String TEMPLATE_HEADER = new String("<template ");
  public static final String TEMPLATE_ENDER = new String("</template>");
  public static final String PARAM_HEADER = new String("<param ");
  public static final String PARAM_ENDER = new String("</param>");
  public static final String OPERATOR_HEADER = new String("<operator");
  public static final String ID_ATTRIBUTE = new String("id");
  public static final String VALUE = new String("<value/>");

  // Param Attributes
  public static final String NAME = new String("name");
  /**
   * The Pre tag sepcifys text before the replacements for all text replaceing the param tag
   * See Documentation for the SEPERATOR tag for a illistruation
   */
  public static final String PRE = new String("pre");
  /**
   * The Post tag sepcifys text after the replacements for all text replaceing the param tag
   * See Documentation for the SEPERATOR tag for a illistruation
   */
  public static final String POST = new String("post");
  /**
   * The Delimiter tag sepcifys text between the <value/> tag
   * For instance if the tag was <param name="1" delimiter=","> Foo IN (<value/>) </param>
   * the result for a I_TemplateParams object with name="1" and values [] = {"1","2","3"}would be
   * Foo IN (1,2,3)
   */
  public static final String DELIMITER = new String("delimiter");
  /**
   * The seperator tag specifys text between replacements
   * of the current param;
   * For instance if there was a tag <param name"1" pre="(" post=")" separator=" SP ">foo</param>
   * and two I_TemplateParams with the name 1 in the I_Template Params Object the result would be;
   * (foo SP foo)
   */
  public static final String SEPARATOR = new String("separator");
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
  public static final String NESTED_SEPARATOR = new String("nested_separator");
}