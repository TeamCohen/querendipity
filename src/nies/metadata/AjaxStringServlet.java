package nies.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.cmu.lti.util.text.FString;

import net.sourceforge.ajaxtags.servlets.BaseAjaxServlet;
import net.sourceforge.ajaxtags.xml.AjaxXmlBuilder;

public class AjaxStringServlet extends BaseAjaxServlet {
	public static final String AJAXSTRINGS_PROPERTY="nies.ajaxStringsFile";
	private static final Logger log = Logger.getLogger(AjaxStringServlet.class);
	private List<List<String>> ajaxData;
	private String modelFile;
	
	public void init() {
		log.info("Loading AjaxStringServlet strings..."); 
		ajaxData = new ArrayList<List<String>>();
		String ajaxStringsFileList = NiesConfig.getProperty(AJAXSTRINGS_PROPERTY);
		if (ajaxStringsFileList == null) return;
		String[] ajaxStringsFiles = ajaxStringsFileList.split(",");
		String metadatadir = NiesConfig.getProperty("nies.metadataDirectory"); 
		BufferedReader reader;
		StringBuilder modelfilebuilder = new StringBuilder();
		for (String ajaxStringsFile : ajaxStringsFiles) {
			try {
				reader = new BufferedReader(new FileReader(new File(
						metadatadir,ajaxStringsFile)));
				
				modelfilebuilder.append("\n["+ajaxStringsFile+"]\n");
				
				int i=0, id=0;;
				for (String line; (line = reader.readLine()) != null; i++) {
					if (line.startsWith("#")) { continue; }
					modelfilebuilder.append(id).append("\t").append(line).append("\n");
					String[] field = FString.split(line, '\t');
					for (int f=0; f<field.length; f++) {
						if (f>=ajaxData.size()) {
							log.info("Added list for new field on line "+i+" of file");
							ajaxData.add(new ArrayList<String>());
						}
						ajaxData.get(f).add(field[f]);
					}
					id++;
				}
				log.info("Read "+id+" string sets from file "+ajaxStringsFile);
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.modelFile = modelfilebuilder.toString();
		getServletContext().setAttribute("modelFile", modelFile);
	}

	@Override
	public String getXmlContent(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String fieldStr = request.getParameter("field");
		String idStr = request.getParameter("id");
		try {
			int field = Integer.parseInt(fieldStr);
			int id    = Integer.parseInt(idStr);
		if (field >= 0) {
        String theXmlString = new AjaxXmlBuilder().addItem(ajaxData.get(field).get(id)).toString();
        return theXmlString;
		} else {
			AjaxXmlBuilder builder = new AjaxXmlBuilder();
			for (List<String> fieldtype : ajaxData) { 
				if (fieldtype.size() > id) builder.addItem(fieldtype.get(id));
			}
			return builder.toString();
		}
		} catch(NumberFormatException e) {
			return new AjaxXmlBuilder().addItem("Error: Bad format for field('"+fieldStr+"') or id ('"+idStr+"')").toString();
		}
	}

}
