package nies.actions;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;


public class ModelFile extends NiesSupport implements ServletContextAware {
	private static final Logger logger = Logger.getLogger(ModelFile.class);
	public String modelFile;
	public String execute() { return SUCCESS; }
	@Override
	public void setServletContext(ServletContext arg0) {
		modelFile = (String) arg0.getAttribute("modelFile");
		modelFile = "<pre>\n"+modelFile+"\n</pre>";
		logger.info("got model file with "+modelFile.length() +" chars");
	}
	public String getModelFile() { return modelFile; }
}
