/**
 * Copyright 2005 Darren L. Spurgeon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nies.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.pra.data.PMAbsInfor;

public class PaperCollection {
	public static final String PAPERCOLLECTION_PROP = "nies.paperCollection";
	public static final String METADATA_DIRECTORY_PROP = "nies.metadataDirectory";
	public HashMap<String, PMAbsInfor> mCollection = new HashMap<String, PMAbsInfor>();
	//static final List authors = new ArrayList();
	private static final Logger log = Logger.getLogger(PaperCollection.class);

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public PMAbsInfor getStruct(String pmid){
		return mCollection.get(pmid);
	}
	// static {
	private void init() {
		String dir = NiesConfig.getProperty(METADATA_DIRECTORY_PROP); // webappRoot + NiesConfig.getProperty("nies.metadataDirectory");
		String fn = NiesConfig.getProperty(PAPERCOLLECTION_PROP);
		init(dir, fn);
	}
	public void init(String dir,String fn) {
		log.info("loading PaperCollection="+dir+fn);
		int row = 0;

		try{
			//for (String line: Seq.enuLines(dir+fn)){
			//PMAbsInfor ab= new PMAbsInfor(line);
			for (PMAbsInfor ab: PMAbsInfor.reader(dir+fn)){
				++row;
				if (ab==null) continue;
				ab.abst="";//remove abstract to save memory
				mCollection.put(ab.pmid, ab);
			}

		} catch (Exception e){//Catch exception if any
			log.info("Error: " + e.getMessage() + ", " + getStackTrace(e));  
			//System.err.println("Error: " + e.getMessage());
		} 
		log.info("done with "+row+" rows, and |mCollection|="+mCollection.size());
	}

	public PaperCollection() {
		this.init();
	}

	public List getAuthors(String pmid) {
		return new ArrayList( mCollection.get(pmid).vAuthors);
	}

	public List getGenes(String pmid) {
		return new ArrayList();// mCollection.get(pmid).v;
	}

	public int getSize() {
		return mCollection.size();
	}

	public String getCitation(String pmid) {
		PMAbsInfor ab= mCollection.get(pmid);
		if (ab==null) return null;
		return ab.getCitation() ;
	}  

}
