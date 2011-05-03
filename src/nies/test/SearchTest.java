package nies.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ghirl.graph.CommandLineUtil;
import ghirl.graph.GraphFactory;
import ghirl.util.Config;
import ghirl.util.Distribution;

import nies.actions.search.Search;
import nies.data.ApplicationDataController;
import nies.data.Query;
import nies.metadata.Init;
import nies.ui.ConfigurableResult;
import nies.ui.ConfigurableResult.Link;
import nies.ui.ConfigurableTab;
import nies.ui.Entry;
import nies.ui.Result;
import nies.ui.Tab;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest extends Search {
	private static final Logger logger = Logger.getLogger(SearchTest.class);
	
	@BeforeClass
	public static void setUpAll() throws FileNotFoundException, IOException {
		Config.getProperties().load(new FileReader("ghirl.properties"));
		if (! Logger.getRootLogger().getAllAppenders().hasMoreElements()) BasicConfigurator.configure();
	}
/*
	@Test
	public void testJoin() {
		int[] ivals = {1,2,3,4,5};
		String[] svals = {"a","bcd","ef","ghijkl"};
		assertEquals("1,2,3,4,5",Search.join(",",ivals));
		assertEquals("a-bcd-ef-ghijkl",Search.join("-",svals));
	}
*/
	@Test
	public void testInitialDistributions() {
		ghirl.util.Config.setProperty("ghirl.dbDir", 
				"/Users/katie/Documents/Cohen/QuerendipityAnnotator/nlao-graph");
		this.setGraph(CommandLineUtil.makeGraph("yeast-18-nov-2009"));
		Distribution d = this.getInitialDistribution("ROT1");
		assertTrue("Must have initial distribution", d != null);
	}
	
	public class DummyAppDataController extends ApplicationDataController {
		DummyAppDataController() {}
		public Query getQuery(String t, String p) {return null;}
		public void saveQuery(Query q) {}
	}
	
	@Override
	protected void tabulateResults(Distribution allResults) {
		ConfigurableTab tab = (ConfigurableTab) Tab.makeTab(Tab.CONFIGURABLE, "Papers");
		tab.setGraph(graph);
		fillTab(tab,allResults,0,null);
		this.tabs.add(tab);
	}
	
	@Test
	public void testSearch() throws Exception {
		Config.setProperty("ghirl.dbDir", "/usr0/share/nies/fly/graph/fbrf-1000-nov-08");
		Config.setProperty("ghirl.persistanceClass", "ghirl.graph.PersistantGraphTokyoCabinet");
		this.prepare();
		this.setGraph(GraphFactory.makeGraph("-textgraph","fbrf-1000-nov-08","-r"));
		this.setKeywords("82f1fa-PubmedArticle");
		this.setController(new DummyAppDataController());
		this.setDepth(1);
		this.execute();
		boolean seenit=false;
		HashMap<String,List<String>> answers = new HashMap<String,List<String>>();
		answers.put("Title", Collections.singletonList("The UCSC Genome Browser Database: update 2006."));
		ArrayList<String> authlist = new ArrayList<String>();
		Collections.addAll(authlist, "C W Sugnet", "A Pohl", "A S Hinrichs");
		answers.put("Authors", authlist);
		for (Tab tab : this.tabs) {
			if (tab.title.equals("Papers")) {
				seenit = true;
				assertEquals(54,tab.nresults); // depth 0: 1; depth 1: 54
				ConfigurableResult r = (ConfigurableResult) tab.results.get(0);
				assertEquals(answers.get("Title").get(0), r.getLabel());
				for (Entry<String,List<Link>> resultEntry : r.getAttributes()) {
					System.err.println(resultEntry.getKey()+"...");
					if (answers.containsKey(resultEntry.getKey())) {
						for (Link val : resultEntry.getValue()) System.err.print("{"+val.anchorText+"} ");
						for (String val : answers.get(resultEntry.getKey())) {
							assertTrue("Missing "+val+" from "+resultEntry.getKey(), 
									resultEntry.getValue().contains(val));
							System.err.println(resultEntry.getKey()+": "+val);
						}
					}
				}
				System.err.println("Done with attributes.");
			}
		}
		assertTrue(seenit);
	}
	
	private void setController(ApplicationDataController adc) {
		this.controller = adc;
	}

	@BeforeClass
	public static void setUpLogger() {
		Logger.getRootLogger().removeAllAppenders();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG); 
		Logger.getLogger("ghirl.graph.GraphLoader").setLevel(Level.INFO);
		logger.debug("Set up logger.");
	}
}
