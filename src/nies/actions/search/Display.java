package nies.actions.search;

import ghirl.graph.GraphId;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import nies.actions.search.Search;
import nies.ui.Entry;

public class Display extends Search {
	private static final Logger logger = Logger.getLogger(Display.class);
	protected String default_file;
	protected String file=null;
	protected Map<GraphId,List<Entry<String,Float>>> reasons;
	
	protected Distribution generatedResults;
	public String execute() throws IOException {
		if (file == null) file = default_file;
		
		queryTerms = "#file:"+file;
		queryParams= "";
		long startTime = System.currentTimeMillis();
		populateFromFile();
		query = makeQuery();
		tabulateResults(generatedResults);
		long endTime   = System.currentTimeMillis();
		this.runtime_sec = ((double)endTime - (double)startTime)/1000;
		
		return SUCCESS;
	}
	public void prepare() {
		super.prepare();
		// read default file property for results
		default_file="test_results.txt";
		reasons = new HashMap<GraphId,List<Entry<String,Float>>>();
	}
	
	/* utility methods */
	
	protected void populateFromFile() throws IOException {
		InputStream is = Display.class.getClassLoader().getResourceAsStream(file);
		if (is == null) throw new IllegalArgumentException("Could not get results file "+file+" as input stream from class loader!");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		generatedResults = new TreeDistribution();
		for(String line; (line=reader.readLine())!=null;) {
			logger.debug("Processing '"+line+"'");
			String[] parts = line.split(" ");
			GraphId node = GraphId.fromString(parts[0]);
			double weight=1.0;
			if (parts.length>1) weight = Double.parseDouble(parts[1]);
			generatedResults.add(weight, node);
			if (parts.length>2) {
				ArrayList<Entry<String,Float>> theseReasons = new ArrayList<Entry<String,Float>>();
				for (int i=2; i<parts.length; i++) {
					String[] rparts = parts[i].split(",");
					theseReasons.add(new Entry<String,Float>(rparts[0], Float.parseFloat(rparts[1])));
				}
				reasons.put(node, theseReasons);
				if (logger.isDebugEnabled()) logger.debug("Added "+node.toString()+" with weight "+weight+" and "+theseReasons.size()+" reasons.");
			}
		}
		reader.close();
	}
	
	/* getters and setters */
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}