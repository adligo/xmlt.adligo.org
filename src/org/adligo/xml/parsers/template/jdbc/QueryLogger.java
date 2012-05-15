package org.adligo.xml.parsers.template.jdbc;

import org.adligo.i.log.client.Log;
import org.adligo.i.log.client.LogFactory;
import org.adligo.models.params.client.I_TemplateParams;
import org.adligo.xml.parsers.template.EngineInput;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.TemplateParserEngine;

/**
 * this is just a common class that can be used to log
 * queries coming from xmlt, in a nice way that can be pasted into Squrrel SQL client
 * or Toad or something, with the values generally replaced so no typing is 
 * necessary.
 * 
 * @author scott
 *
 */
public class QueryLogger {
	private static final Log log = LogFactory.getLog(QueryLogger.class);
	
	public static void logQueryInfo(I_TemplateParams params, Template template) {
		if (log.isInfoEnabled()) {
			String replacedSql = getParsedQuery(params, template);
			log.info("Executing query:\n\n" + replacedSql + "\n\n");
		}
	}

	/**
	 * note this is for logging to a console
	 * and should not be used for actual queries
	 * (it is NOT injection safe)
	 * 
	 * @param params
	 * @param template
	 * @return
	 */
	public static String getParsedQuery(I_TemplateParams params,
			Template template) {
		EngineInput engineInput = new EngineInput();
		engineInput.setTemplate(template);
		engineInput.setParams(new PrettyParamDecorator(params));
		String replacedSql = TemplateParserEngine.parse(engineInput);
		return replacedSql;
	}
}
