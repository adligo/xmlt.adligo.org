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
 * Company:      Adligo
 * @author       scott@adligo.com
 * @version 1.3
 */
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.models.params.client.Parser;
import org.adligo.models.params.client.XMLBuilder;

public class Templates {
  public static final String CANNOT_ACCESS_FILE = "Cannot access file ";
  public static final String THERE_WAS_A_PROBLEM_PARSING_OR_COULD_NOT_FIND_RESOURCE = "There was a problem parsing or could not find resource ";
  public static final String THERE_IS_A_BUG_IN_THE_TEMPLATE_PARSER_PLEASE_SEND_YOUR_FILE_TO_SCOTT_ADLIGO_COM = "There is a bug in the template parser, please send your file to scott@adligo.com";
  private static final Log log = LogFactory.getLog(Templates.class);
  
  String name = new String(""); //used to manage several of these objects
  private List<String> templateNames = new ArrayList<String>();
  private HashMap<String,Template> templates = new HashMap<String,Template>();

  /**
   * Default Constructor
   */
  public Templates() {}
  /**
   * 
   * @param sFileName
   * @param isResource
   */
  public Templates(String sFileName, boolean isResource) {
	    this();
	    if (isResource) {
	    	parseResource(sFileName);
	    } else {
	    	parseFile(sFileName);
	    }
  }

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
  public synchronized void parseFile(String sFileName) {
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
    	  IllegalArgumentException toThrow = new IllegalArgumentException(CANNOT_ACCESS_FILE + sFileName);
    	  toThrow.initCause(e);
    	  throw toThrow;
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
  public synchronized void parseResource(String sFileName) {
	  parseResourcePrivate(sFileName, XMLBuilder.UNIX_LINE_FEED);
  }
  
  /**
   * see method with same name and no line feed,
   * which was added to fix issues with cvs/tests and textRunner
   * @param sFileName
   * @param lineFeed something from XMLBuilder 
   *   for instance XMLBuilder.DOS_LINE_FEED
   *   so you can prety print your template
   */
  public synchronized void parseResource(String sFileName, String lineFeed) {
	  parseResourcePrivate(sFileName, lineFeed);
  }
  
  private void parseResourcePrivate(String sFileName, String lineFeed) {
    if (log.isDebugEnabled()) {
      log.debug(" parseingResource " + sFileName);
    }
      try {
        Class<?> c = this.getClass();
        URL r = c.getResource(sFileName);
        if (r == null) {
        	 IllegalArgumentException toThrow = new IllegalArgumentException(
       			  THERE_WAS_A_PROBLEM_PARSING_OR_COULD_NOT_FIND_RESOURCE + sFileName);
        	 throw toThrow;
        }
        InputStream is = r.openStream();
        StringBuffer str = new StringBuffer();
        byte b[] = new byte[1];

        while ( is.read(b) != -1 ) {
        	while ( is.read(b) != -1 ) {
        		//strip line feeds 
            	if (b[0] == '\n') {
            		str.append(lineFeed);
            	} else if (b[0] != '\r') {
            		str.append(new String(b));
            	}
            }
        }
        is.close();
        parseContent(new String(str));
      } catch (StringIndexOutOfBoundsException e) {
    	  RuntimeException toThrow = new RuntimeException(
    			  THERE_IS_A_BUG_IN_THE_TEMPLATE_PARSER_PLEASE_SEND_YOUR_FILE_TO_SCOTT_ADLIGO_COM);
    	  toThrow.initCause(e);
    	  throw toThrow;
      } catch (IOException e) {
    	  IllegalArgumentException toThrow = new IllegalArgumentException(
    			  THERE_WAS_A_PROBLEM_PARSING_OR_COULD_NOT_FIND_RESOURCE + sFileName);
    	  toThrow.initCause(e);
    	  throw toThrow;
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
    templateNames.add(sName);
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
  
  public synchronized void addTemplate(String sName, Template template) {
	templateNames.add(sName);
    templates.put(sName, template);
  }

  /**
   * This removes a template to the object.
   */
  public synchronized void removeTemplate(Template template) {
    templates.remove(template);
  }

  public Iterator<String> getTemplateNames() {
	  return templateNames.iterator();
  }
  
  public void setName(String s) { name = s;}
  public String getName() { return name;}
}