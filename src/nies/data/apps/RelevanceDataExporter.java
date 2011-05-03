package nies.data.apps;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
//import org.displaytag.export.CsvView;

import nies.data.ApplicationDataController;
import nies.data.Relevance;
import nies.data.RelevanceData;
import nies.data.RelevanceDatabase;
import nies.data.RelevanceView;
//import nies.metadata.Config;

import com.sleepycat.collections.StoredSortedKeySet;
import com.sleepycat.collections.StoredSortedValueSet;
import com.sleepycat.je.DatabaseException;

import edu.cmu.minorthird.util.StringUtil;

public class RelevanceDataExporter {
	private static final Logger logger = Logger.getLogger(RelevanceDataExporter.class);
	private ApplicationDataController datacontroller;
	public static HashMap<String,String> judgementKey = new HashMap<String,String>();
	static {
		judgementKey.put(RelevanceData.PROMOTE, "1");
		judgementKey.put(RelevanceData.DEMOTE, "0");
		judgementKey.put(RelevanceData.CLICK, "click");
	}
	
	public void setup() {
		datacontroller = new ApplicationDataController();
		datacontroller.setupDatabases();
	}
	public StoredSortedValueSet<Relevance> getRelevancies() {
		return this.datacontroller.getRview().getRelevanceSet();
	}
	public String getUserName(String uid) {
		if (datacontroller.getUview().getUserMap().containsKey(uid))
			return datacontroller.getUview().getUserMap().get(uid).getUsername();
		else return uid;
	}
	public static String join(String delim, String ... items ) {
		StringBuilder b = new StringBuilder();
		int i=0;
		for (; i<(items.length-1); i++) {
			b.append(items[i]).append(delim);
		}
		return b.append(items[i]).toString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    if (args.length < 1) {
		System.out.println("Usage:\n\t<exportfile>");
		return;
	    }
		RelevanceDataExporter exporter = new RelevanceDataExporter();
		exporter.setup();
		StoredSortedValueSet<Relevance> relevancies = exporter.getRelevancies();
					
		// open your output file here
		try {
			Iterator<Relevance> rel_iter = relevancies.iterator();
			BufferedWriter out = new BufferedWriter(new FileWriter(args[0]));
	        
			HashMap<String,String> judgements = new HashMap<String,String>(); 
			HashMap<String,String> clicks = new HashMap<String,String>();
			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
			while(rel_iter.hasNext()) {
				Relevance relevance = rel_iter.next();
				String feedback = judgementKey.get(relevance.getType()) 
					+"\t" + format.format(relevance.getTimestamp());
				String instance = join("\t", relevance.getQuery(),
						                     relevance.getQueryParams(),
						                     String.valueOf(relevance.getRank()),
						                     relevance.getDocument(),
						                     exporter.getUserName(relevance.getUser()));
				if (relevance.getType().equals(Relevance.CLICK)) {
				    clicks.put(instance, feedback);
				} else
				    judgements.put(instance, feedback);
			}
			ArrayList<Map.Entry<String,String>> jlist = new ArrayList<Map.Entry<String,String>>(judgements.size());
			jlist.addAll(judgements.entrySet());
			Comparator byKey = new Comparator<Map.Entry<String,String>>() {
				public int compare(Entry<String, String> o1,
						Entry<String, String> o2) {
					// TODO Auto-generated method stub
					return o1.getKey().compareTo(o2.getKey());
				}
	
			};
			Collections.sort(jlist, byKey);
			out.write("# "+ join("\t","query",
						        "queryparams",
						        "rank",
						        "document",
						        "user",
						        "judgement",
						        "timestamp")+"\n");
			for(Map.Entry<String,String> entry : jlist) {
				out.write(entry.getKey()+"\t"+entry.getValue()+"\n");
			}

			out.write("# clickstream\n");
			jlist.clear();
			jlist.addAll(clicks.entrySet());
			Collections.sort(jlist, byKey);
			for(Map.Entry<String,String> entry : jlist) {
				out.write(entry.getKey()+"\t"+entry.getValue()+"\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// write relevancies to the file in whatever format you need (CSV probably smart)
		// you can find the API for StoredSortedValueSet using google, but it
		// mostly just acts like a normal set -- use an iterator
		
		// run using Eclipse by right-clicking the class and doing 
		// 		"Run As... > Java Application"
		// only if your eclipse is on the same machine as the DB!!
		//
		// to run on the machine with the db, finish this class, deploy the app, then goto
		// tomcat manager http://<server>:8080/manager/html
		// and "stop" NIES
		// then cd to <tomcat-home>/webapps/strutsnies/WEB-INF and do
		//  $ java -cp classes:lib nies.data.apps.RelevanceDataExporter
		// with whatever args you've added to this.
		// call me if it doesn't work and we'll do another pair-programming session like on friday.
		
		
	}

}
