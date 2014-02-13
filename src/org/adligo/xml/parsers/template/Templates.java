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
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.i.util.shared.I_Iterator;
import org.adligo.i.util.shared.StringUtils;
import org.adligo.models.params.shared.I_XMLBuilder;
import org.adligo.models.params.shared.Parser;
import org.adligo.models.params.shared.TagAttribute;
import org.adligo.models.params.shared.TagInfo;

public class Templates {
  public static final String CANNOT_ACCESS_FILE = "Cannot access file ";
  public static final String THERE_WAS_A_PROBLEM_PARSING_OR_COULD_NOT_FIND_RESOURCE = "There was a problem parsing or could not find resource ";
  public static final String THERE_IS_A_BUG_IN_THE_TEMPLATE_PARSER_PLEASE_SEND_YOUR_FILE_TO_SCOTT_ADLIGO_COM = "There is a bug in the template parser, please send your file to scott@adligo.com";
  private static final Log log = LogFactory.getLog(Templates.class);
  
  String name = new String(""); //used to manage several of these objects
  private List<String> templateNames = new ArrayList<String>();
  private HashMap<String,Template> templates = new HashMap<String,Template>();
  private boolean parsed = false; 

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
	    parsed = true;
  }

  /**
   * Same as Default Constructor but also calls parse File
   * @param String s = the file to be parsed where s is the full path and file name
   */
  public Templates(String sFileName) {
    this();
    parseFile(sFileName);
    parsed = true;
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
        char c[] = new char[(int)inputFile.length()];
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
      parsed = true;
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
	  parseResourcePrivate(sFileName, I_XMLBuilder.UNIX_LINE_FEED);
	  parsed = true;
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
	  parsed = true;
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
        byte b[] = new byte[1];
        int counter = 0;
        while (is.read(b) != -1) {
        	counter ++;
        }
        is.close();
        
        is = r.openStream();
        InputStreamReader isr = new InputStreamReader(is);
        
        if (log.isDebugEnabled()) {
        	String encoding = isr.getEncoding();
        	log.debug("reading content with encoding " + encoding);
        }
        CharBuffer cb = CharBuffer.allocate(counter);
        isr.read(cb);
        String result = new String(cb.array()).trim();
        result = new String(result.getBytes(),"UTF-8");
        isr.close();
        is.close();
        if (log.isDebugEnabled()) {
        	log.debug("read in " + result.length() + " characters characters");
        	if (log.isTraceEnabled()) {
        		log.trace(result);
        	}
        }
        parseContent(result);
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
	content = Parser.stripComments(content);
	if (log.isDebugEnabled()) {
		String debugSearchString = "<>";
		if (content.contains(debugSearchString)) {
			log.debug("found string " + debugSearchString + " for break point");
		}
	}
	TagInfo info = Parser.getNextTagInfo(content, 0);
	String templatesTagName = info.getTagName();
	if (Tags.XML_TAG_NAME.equals(templatesTagName)) {
		info = Parser.getNextTagInfo(content, info.getHeaderEnd());
		
	}
	if (info == null) {
		throw new IllegalArgumentException("Was Not able to identify the templates tag ");
	}
	templatesTagName = info.getTagName();
	
	if (!Tags.TEMPLATES_TAG_NAME.equals(templatesTagName)) {
		throw new IllegalArgumentException("Was Not able to identify the templates tag ");
	}
	info = Parser.getNextTagInfo(content, info.getHeaderEnd());
	while (info != null) {
		if (log.isDebugEnabled()) {
			String header = content.substring(info.getHeaderStart(), info.getHeaderEnd());
			log.debug("parsing tag " + info + " with header " + header);
		}
		String tagName = info.getTagName();
		if (Tags.TEMPLATE_TAG_NAME.equals(tagName)) {
			if (info.hasEnder()) {
				String templateText = content.substring(info.getHeaderEnd() + 1, info.getEnderStart());
				Template template = new Template(templateText);
			    //delete the template that was parsed from the content string
				String name = "";
				I_Iterator it = Parser.getAttributes(info, content);
				while (it.hasNext()) {
					TagAttribute ta = (TagAttribute) it.next();
					String sName = ta.getName();
					if (Tags.NAME.equals(sName)) {
						name = ta.getValue();
					}
				}
				if (!StringUtils.isEmpty(name)) {
					templateNames.add(name);
					templates.put(name, template);
				}
			}
		} 
		if (info.hasEnder()) {
			Integer end = info.getEnderEnd();
			if (end == null) {
				info = null;
			} else { 
				info = Parser.getNextTagInfo(content, end);
			}
		} else {
			info = Parser.getNextTagInfo(content, info.getHeaderEnd());
		}
	}
  }

  /**
   * This returns the first template string for a template who's
   * name was pased in.
   */
  public Template getTemplate(String sName) {
	  Template toRet = templates.get(sName);
	  if (toRet == null) {
		  throw new IllegalArgumentException("No template found with name '" + sName + "'");
	  }
	  return toRet;
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
  
	public boolean isParsed() {
		return parsed;
	}
}