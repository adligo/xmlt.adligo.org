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
import org.adligo.xml.parsers.Parser;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class Templates {
  Log log = LogFactory.getLog(this.getClass());
  String name = new String(""); //used to manage several of these objects
  Vector vTemplates  = new Vector(); // A vector of String []s where
                    //  [0] is the template name
                    //  [1] is the content of the template

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
        System.out.println("Cannot access file " + sFileName);
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
          log.warn(" could not find resource " + sFileName);
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
    //System.out.println("the content is \n" + content);
    String[] template = new String[2];
    int headerStart,headerEnd,closerStart,closerEnd;

    headerStart = content.indexOf(Tags.TEMPLATE_HEADER);
    headerEnd = content.indexOf(">", headerStart);
    template[0] = Parser.getAttributeValue(content.substring(headerStart,headerEnd),
            Tags.NAME);

    closerStart = content.indexOf(Tags.TEMPLATE_ENDER);
    closerEnd = closerStart + Tags.TEMPLATE_ENDER.length();

    template[1] = content.substring(headerEnd + 1, closerStart );
    //delete the template that was parsed from the content string
    content = content.substring(closerEnd);
    vTemplates.add(template);
    if (content.indexOf(Tags.TEMPLATE_HEADER) != -1) {
      parseContent(content); // recursion
    }
  }

  /**
   * This returns the first template string for a template who's
   * name was pased in.
   */
  public String getTemplate(String sName) {
    int iCount = vTemplates.size();

    for (int i = 0; iCount > i; i++) {
      if(((String[]) vTemplates.elementAt(i))[0].equals(sName)) {
        return new String(((String[]) vTemplates.elementAt(i))[1]);
      }
    }
    if (log.isWarnEnabled()) {
      log.warn("\n\n org.adligo.xml.parsers.template.Templates \n " +
            "could not find a template for " + sName);
    }
    return null;
  }

  /**
   * This adds a template to the object.
   */
  public void addTemplate(String sName, String sTemplate) {
    vTemplates.addElement(new String[] {sName, sTemplate});
  }

  /**
   * This removes a template to the object.
   */
  public void removeTemplate(String sTemplate) {
    int iCount = vTemplates.size();

    for (int i = 0; iCount > i; i++) {
      if ( (((String []) vTemplates.elementAt(i))[0]).equals(sTemplate)) {
        vTemplates.remove(i);
      }
    }
  }

  public void setName(String s) { name = s;}
  public String getName() { return name;}
}