package nies.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.cmu.lti.util.text.FString;

import net.sourceforge.ajaxtags.servlets.BaseAjaxServlet;
import net.sourceforge.ajaxtags.xml.AjaxXmlBuilder;

public class AjaxStringServlet extends HttpServlet {
	public static final String AJAXSTRINGS_PROPERTY="nies.ajaxStringsFile";
	private static final Logger log = Logger.getLogger(AjaxStringServlet.class);
	private Map<String,AjaxData> ajaxDataByFile;
	private String modelFile;
	private String defaultDataFile=null;
	private BaseAjaxServlet xmlServlet = new XmlAjaxStringServlet();
	
	Map<String,List<String>> result;
	int nresults;
	
	public class AjaxData {
		public int nvalues=-1;
		public String label=null;
		public String identifier=null;
		public List<String> fieldOrder=new ArrayList<String>();
		public Map<String,List<String>> fieldData=new TreeMap<String,List<String>>();
	}
	
	public void init() {
		log.info("Loading AjaxStringServlet strings..."); 
		ajaxDataByFile = new TreeMap<String,AjaxData>();
		String ajaxStringsFileList = NiesConfig.getProperty(AJAXSTRINGS_PROPERTY);
		if (ajaxStringsFileList == null) return;
		String[] ajaxStringsFiles = ajaxStringsFileList.split(",");
		String metadatadir = NiesConfig.getProperty("nies.metadataDirectory"); 
		BufferedReader reader;
		StringBuilder modelfilebuilder = new StringBuilder();
		for (String ajaxStringsFile : ajaxStringsFiles) {
			AjaxData ajaxData = new AjaxData();
			ajaxDataByFile.put(ajaxStringsFile, ajaxData);
			if (defaultDataFile==null) defaultDataFile=ajaxStringsFile;
			
			try {
				reader = new BufferedReader(new FileReader(new File(
						metadatadir,ajaxStringsFile)));
				
				modelfilebuilder.append("\n["+ajaxStringsFile+"]\n");
				
				int i=0, id=0;
				for (String line; (line = reader.readLine()) != null; i++) {
					if (line.startsWith("#")) {
						if (i>2) continue;
						String[] parts = FString.split(line,' ');
						if (line.startsWith("#label")) {
							ajaxData.label = parts[1];
						} else if (line.startsWith("#identifier")) {
							ajaxData.identifier = parts[1];
						} else if (line.startsWith("#fields")) {
							for (int f=1; f<parts.length; f++) {
								ajaxData.fieldData.put(parts[f], new ArrayList<String>());
								ajaxData.fieldOrder.add(parts[f]);
							}
						}
						continue;
					}
					modelfilebuilder.append(id).append("\t").append(line).append("\n");
					String[] field = FString.split(line, '\t');
					if (ajaxData.fieldOrder.size() == 0) {
						for (int f=0; f<field.length; f++) {
							ajaxData.fieldOrder.add("field"+f);
							ajaxData.fieldData.put("field"+f, new ArrayList<String>());
						}
					}
					if (field.length != ajaxData.fieldOrder.size()) {
						log.warn("Line "+i+" of file "+ajaxStringsFile+" is malformed ("
								+field.length+" fields where I expected "+ajaxData.fieldOrder.size()+"); truncating as appropriate");
					}
					for (int f=0; f<ajaxData.fieldOrder.size(); f++) {
						String value = "";
						if (f<field.length) value = field[f];
						ajaxData.fieldData.get(ajaxData.fieldOrder.get(f)).add(value);
					}
					id++;
				}
				log.info("Read "+id+" string sets from file "+ajaxStringsFile);
				ajaxData.nvalues=id;
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String fieldStr = request.getParameter("field");
		String idStr = request.getParameter("id");
		String fileStr = request.getParameter("file");
		String type = request.getParameter("type");
		String label = request.getParameter("label");
		String identifier = request.getParameter("identifier");
		
		// parameter validation
		if (type == null || type == "") type = "xml";
		if (fileStr == null || fileStr == "") {
			log.info("No or empty file specified; using default '"+defaultDataFile+"'");
			fileStr=defaultDataFile;
		}
		if (idStr == null || idStr == "")       idStr="-1";
		if (fieldStr == null || fieldStr == "") fieldStr = "-1";
		
		result = new TreeMap<String,List<String>>();
		nresults = -1;
		
		try {
			AjaxData ajaxData = ajaxDataByFile.get(fileStr);

			if (ajaxData == null)
				result.put("error",Collections.singletonList("Error: No ajax data stored under file name '"+fileStr+"'"));
			else {

				if (label == null || label == "") label = ajaxData.label;
				if (identifier == null || identifier == "") identifier = ajaxData.identifier;
				try {
					int field = Integer.parseInt(fieldStr);
					if (field<0) fieldStr="";
					else if (field < ajaxData.fieldOrder.size()) fieldStr = ajaxData.fieldOrder.get(field);
				} catch(NumberFormatException e) {}
				int id    = Integer.parseInt(idStr);
				if (fieldStr.equals("")) { // then get all fields
					if (id >= 0) { // get all fields for a particular value
						for (String key : ajaxData.fieldOrder) { 
							List<String> values = ajaxData.fieldData.get(key);
							if (values.size() > id) {
								result.put(key,Collections.singletonList(values.get(id)));
								nresults++;
							}
						}
					} else { // get all fields for all values
						result = ajaxData.fieldData;
						nresults = ajaxData.nvalues;
					}
				} else { // get a particular field
					if (id >=0) { // get a particular field for a particular value
						result.put(fieldStr,Collections.singletonList(ajaxData.fieldData.get(fieldStr).get(id)));
						nresults = 1;
					} else { // get a particular field for all values
						result.put(fieldStr,ajaxData.fieldData.get(fieldStr));
						nresults = result.get(fieldStr).size();
					}
				}
			}
		} catch(NumberFormatException e) {
			result.put("error",Collections.singletonList("Error: Bad format for field('"+fieldStr+"') or id ('"+idStr+"')"));
		}
		
		if (type.equals("xml")) {
			try {
				xmlServlet.service(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (type.equals("json")) {
			StringBuilder jsonbuilder = new StringBuilder("{\"label\":\""+label+"\",\"identifier\":\""+identifier+"\",\"items\":[");
			for (int i=0; i<nresults; i++) {
				if (i>0) jsonbuilder.append(", ");
				jsonbuilder .append("{");
				for (Iterator<String> it = result.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					jsonbuilder.append("\""+key+"\":\"").append(result.get(key).get(i)).append("\"");
					if (it.hasNext()) jsonbuilder.append(", ");
				}
				jsonbuilder.append("}");
			}
			jsonbuilder.append("]}");
			response.setContentType("application/json");
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print(jsonbuilder.toString());
				if (result.size() < 20) log.debug(jsonbuilder.toString());
				out.flush();
			} catch (IOException e) {
				log.error("Couldn't get output writer for Autocomplete:",e);
			}
		}
	}

	private class XmlAjaxStringServlet extends BaseAjaxServlet {
		@Override
		public String getXmlContent(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			if (result != null) {
				try {
					String theXmlString;
					AjaxXmlBuilder b = new AjaxXmlBuilder();
					for (int i=0; i<nresults; i++) {
						for (Iterator<String> it = result.keySet().iterator(); it.hasNext();) {
							String key = it.next();
							b.addItem(key,result.get(key).get(i));
						}
					}
					theXmlString = b.toString();
					log.debug(theXmlString);
					return theXmlString;
				} catch (Exception e) {
					log.error("Problem getting Ajax XML Builder:",e);
				}
			}
			return new AjaxXmlBuilder().toString();
		}
	}
}
