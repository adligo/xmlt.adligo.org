package org.adligo.xml.parsers.template;

/**
 * Title:
 * Description:  This class is used to hold the con-tence of a xml template
 *                file so that it is indexed and stored in RAM.
 *                For instance if you had a xml file with the following text
 *
 *                <?xml version="1.0"?>
 *                <template name="persons">
 *                SELECT  comment
 *                FROM persons p
 *                <param name="where" nested_separator=" OR "> WHERE
 *                  <param name="oid" separator=","> oid IN (<value>) </param>
 *                </param>
 *                </template>
 *
 *                <template name="persons_display">
 *                <html>
 *                <body>foo</body>
 *                </html>
 *                </template>
 *
 *                this class would allow you to load your xml file and retrive
 *                templates with the getTemplate(String sName) method.
 *                ie.. getTemplate("persons_display") would return the string
 *                <html>
 *                <body>foo</body>
 *                </html>
 *                This class is (and all classes under the org.adligo namespace)
 *                are open-source software which is protected by the GNU GENERAL PUBLIC LICENSE.
 * Copyright:    GPL http://www.adligo.com/gpl.html
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.HashMap;
import org.adligo.xml.Parser;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class Templates {
  Log log = LogFactory.getLog(Templates.class);
  String name = new String(""); //used to manage several of these objects
  private HashMap templates = new HashMap();

  /**
   * Default Constructor
   */
  public Templates() {}
  /**
   * Same as Default Constructor but also calls parse File
   * @param String s = the file to be parsed where s is the full path and file name
   */
  public Templates(String sFileName) {
    this();
    parseFile(sFileName);
  }

  /**
   * This opens a xml template file and calls parseContent(String content);
   * passing in the text from the xml file.
   * Note: You must supply the complete relative path in the sFileName argument!
   */
  public void parseFile(String sFileName) {
    if (log.isDebugEnabled()) {
      log.debug(" parseingFile " + sFileName);
    }
      String content;
      try {
        File inputFile = new File(sFileName);
        FileReader in = new FileReader(inputFile);
        char c[] = new char[(char)inputFile.length()];
        in.read(c);
        content = new String(c);
        in.close();
        parseContent(content);
      }catch(java.io.IOException e){
        log.error("Cannot access file " + sFileName, e);
      }
      name = sFileName.substring(sFileName.lastIndexOf(
              System.getProperty("file.separator")) + 1, sFileName.length());
  }


  /**
   * This opens a xml template file and calls parseContent(String content);
   * passing in the text from the xml file.
   * Note:
   * This is basically the same as parse File
   * but this will look in the class path for the xml file
   * for instance
   * /com/adligo/ui/treefinder/Treefinder_HTML.xml
   *
   */
  public void parseResource(String sFileName) {
    if (log.isDebugEnabled()) {
      log.debug(" parseingResource " + sFileName);
    }
      try {
        Class c = this.getClass();
        URL r = c.getResource(sFileName);
        InputStream is = r.openStream();
        StringBuffer str = new StringBuffer();
        byte b[] = new byte[1];

        while ( is.read(b) != -1 ) {
            str.append(new String(b));
        }
        is.close();
        parseContent(new String(str));
      } catch (Exception e) {
          log.error(" could not find resource " + sFileName, e);
      }
      name = sFileName.substring(sFileName.lastIndexOf("/") + 1, sFileName.length());
  }
  /**
   * This parses the content String into string arrays
   * which are placed in the vTemplates Vector.
   * The string arrays are formated so that,
   * [0] is the template name
   * [1] is the content of the template
   */
  private void parseContent(String content) {
    int headerStart,headerEnd,closerStart,closerEnd;

    headerStart = content.indexOf(Tags.TEMPLATE_HEADER);
    
    headerEnd = content.indexOf(">", headerStart);
    String sName = Parser.getAttributeValue(content.substring(headerStart,headerEnd),
            Tags.NAME);

    closerStart = content.indexOf(Tags.TEMPLATE_ENDER);
    closerEnd = closerStart + Tags.TEMPLATE_ENDER.length();
    Template template = new Template(content.substring(headerEnd + 1, closerStart ));
    //delete the template that was parsed from the content string
    content = content.substring(closerEnd);
    templates.put(sName, template);
    if (content.indexOf(Tags.TEMPLATE_HEADER) != -1) {
      parseContent(content); // recursion
    }
  }

  /**
   * This returns the first template string for a template who's
   * name was pased in.
   */
  public Template getTemplate(String sName) {
    return (Template) templates.get(sName);
  }

  public String getTemplateAsString(String sName) {
  	Template t = (Template) templates.get(sName);
  	if (t == null) {
      return "";
  	} else {
  	  return t.getStringValue();
  	}
  }
  /**
   * This adds a template to the object.
   */
  public void addTemplate(String sName, Template template) {
    templates.put(sName, template);
  }

  /**
   * This removes a template to the object.
   */
  public void removeTemplate(Template template) {
    templates.remove(template);
  }

  public void setName(String s) { name = s;}
  public String getName() { return name;}
}