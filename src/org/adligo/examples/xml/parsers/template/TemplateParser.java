package org.adligo.examples.xml.parsers.template;

/**
 * Description:  This is a example of how to use the org.adligo.xml.parsers.template package.
 *                This class is (and all classes under the org.adligo namespace)
 *                are open-source software which is protected by the GNU GENERAL PUBLIC LICENSE.
 *                You can find a copy of the license in the root directory license.txt.
 * Copyright:    Copyright (c) 2001
 * Company:      adligo
 * @author Scott Morgan
 * @version 1.0
 */

import javax.swing.*;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.TemplateParserEngine;
import org.adligo.examples.xml.parsers.*;
import org.adligo.models.params.client.Param;
import org.adligo.models.params.client.Params;
import java.io.*;
import java.awt.*;

public class TemplateParser extends JApplet {
  public static String sFile = new String("PersonsSQL.xml");
  private String loadDirectory;
  Templates templates = new Templates();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea taComments = new JTextArea();

  /**Construct the applet*/
  public TemplateParser() {}

  /**Initialize the applet*/
  public void init() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception {
    File f = new File(".");
    loadDirectory = f.getAbsolutePath();
    loadDirectory = loadDirectory.substring(0,loadDirectory.length() - 1);
    sFile = loadDirectory + "xml" + System.getProperty("file.separator") + sFile;
    templates.parseFile(sFile);
    System.out.print(sFile + "  <template name=persons> = ");
    System.out.println(templates.getTemplate("persons").toString());

    Params params = new Params();
    params.addParam("maxrows",new String [] {"10"}, null );
    Params whereArgs = new Params();
    whereArgs.addParam("oid",new String [] {"1","2"}, null);
    whereArgs.addParam("fname",new String [] {"'joe'"}, null);
    whereArgs.addParam("fname",new String [] {"'bob'"}, null);
    Param where = new Param("where", new String [] {}, whereArgs);
    params.addParam(where);
    JTextArea ta = new JTextArea();
    String s = TemplateParserEngine.parse(templates.getTemplate("persons"), params);
    taComments.setMinimumSize(new Dimension(68, 103));
    taComments.setPreferredSize(new Dimension(68, 103));
    taComments.setLineWrap(true);
    taComments.setWrapStyleWord(true);
    taComments.setText("Below is the parsed template, the template was printed to the console. \n" +
      "If you are using JBuilder 5 you will want to check out org.adligo.xml.parsers.template.DataSetParams and " +
      " org.adligo.xml.parsers.template.DataSetRowParams (both are in db_xmlt.jar).  Both of these classes are ways to use a DataSet as the object " +
      " that holds the parsing information (I_TemplateParams)."
    );
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(taComments, BorderLayout.NORTH);
    jScrollPane1.getViewport().add(ta, null);
    ta.setText(s);
  }
  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    return null;
  }

}