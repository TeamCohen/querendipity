package nies.test;


import edu.cmu.lti.util.run.Param;
import edu.cmu.pra.CTag;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.ModelPathRank;
import edu.cmu.pra.model.PRAModel;

import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.TextGraph;
import ghirl.util.Config;
import ghirl.util.Distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nies.metadata.Init;
import nies.metadata.NiesConfig;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import bsh.EvalError;
import static org.junit.Assert.*;

public class GraphInitializationTest {
	private static final Logger log = Logger.getLogger(GraphInitializationTest.class);
	
	
	@Test
	public void testBshSetup() {
		bsh.Interpreter interp = new bsh.Interpreter();
		ghirl.util.Config.setProperty("ghirl.dbDir", "test/graphs");
		String dbDir = ghirl.util.Config.getProperty("ghirl.dbDir");
		try {
			interp.eval("import ghirl.graph.*;");
			interp.eval("import ghirl.learn.*;");
			interp.eval("cd(\""+dbDir+"\");");
			interp.eval("print(bsh.cwd);");
			Graph g = (Graph) interp.source("testgraph.bsh");
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSorting() {
		
		GraphId one = new GraphId("1","aaa");
		GraphId two = new GraphId("2","zzz");
		assertTrue("numbererd",two.compareTo(one)>0);
		
		
		GraphId first = new GraphId("author","'t");
		GraphId blank = new GraphId("",";t");
		GraphId mid = new GraphId("TEXT","16893904-Abstract");
		assertTrue("blank",mid.compareTo(blank)>0);
		assertTrue("author-flavored",mid.compareTo(first)<0);
	}
	
	@Test
	public void testModelBasedGraph() throws FileNotFoundException, IOException {
		Config.getProperties().load(new FileReader("ghirl.properties"));
		NiesConfig.getProperties().load(new FileReader("nies.properties"));
		
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		AppenderSkeleton ap = (AppenderSkeleton) 
			Logger.getRootLogger().getAllAppenders().nextElement();
		ap.setThreshold(Level.DEBUG);
		Logger.getRootLogger().debug("testing logging");
		Logger.getLogger(TextGraph.class).debug("testing some more");
		
		File compactNodeFile = new File(Config.getProperty(Config.DBDIR)
				+File.separatorChar+Config.getProperty(Config.GRAPHNAME)+"_compact"
				+File.separatorChar+"graphNode.pct");
		assertTrue(compactNodeFile.getPath(),compactNodeFile.exists());
		

		BufferedReader reader = new BufferedReader(new FileReader(compactNodeFile));
		reader.readLine(); // drop first node
		String firstnode = reader.readLine();
		GraphId firstnodeId = GraphId.fromString(firstnode);

		Config.setProperty(TextGraph.CONFIG_PERSISTANCE_PROPERTY, "ghirl.graph.PersistantGraphTokyoCabinet");
		graph = new TextGraph("Yeast2.cite-complete");
		assertTrue(graph.contains(firstnodeId));
		assertNotNull(graph.getTextContent(firstnodeId));
		
		Init init = new Init();
		
		graph=null;
		try {
			graph = Init.makeGraph();
			log.info("init: graph built");
			log.info("init: saving the servletContext");
//			servletContext.setAttribute(SERVLETCONTEXT_GRAPH, g);
			log.info("init: saved the servletContext");
			
			String fnModel=  ghirl.util.Config.getProperty("pra.model",""); 
			model:
				if (fnModel.length()>0) {
					igraph=null;
					if (graph instanceof IGraph) igraph = (IGraph) graph;
					else if (graph instanceof TextGraph 
							&& ((TextGraph)graph).getInnerGraph() instanceof IGraph) igraph = (IGraph) ((TextGraph)graph).getInnerGraph();
					else {
						log.error("Couldn't find a valid graph to load the model into. Graph or inner graph must implement IGraph.");
						break model;
					}
//					servletContext.setAttribute(SERVLETCONTEXT_IGRAPH,ig);
					//init.makeModel(fnModel,igraph);
					makeModelCopy(fnModel,igraph);
				}
		} catch (Exception e) {
			log.error("failed to make graph: ",e);
		}
		
		assertNotNull(graph);
		
		assertTrue("Must contain "+firstnode,graph.contains(firstnodeId));
		Distribution qdist = graph.asQueryDistribution(firstnode);
		firstnodeId = (GraphId) qdist.iterator().next();
		assertEquals("Must have nonzero query distribution for "+firstnode,qdist.size(),1);
		Distribution neigh1 = graph.walk1(firstnodeId); 
		assertTrue(firstnode+" must have neighbors",neigh1.size()>0);
		
		assertNotNull(graph.getTextContent(firstnodeId));
		
		
	}
	public Graph graph;
	public IGraph igraph;
	public PRAModel netCite;
	public PRAModel netRead;
	
	public void makeModelCopy(String fnModel, IGraph g) {
		String fd=  ghirl.util.Config.getProperty("pra.dir"); 
		String fnConf=  ghirl.util.Config.getProperty("pra.conf");   
		log.info("loading model="+fd+fnModel);

			try {			


				log.info("pra.dir="+fd);
				log.info("pra.conf="+fnConf);
				log.info("pra.model="+fnModel);


				Param.overwriteFrom(fd+fnConf);
				Param.overwrite("dataFolder="+fd);


				
				netCite=new ModelPathRank();
				log.info(netCite.schema.vRel.getVS(CTag.nameS));

				netCite.loadModel(fd+fnModel+".cite");
				netCite.setGraph(g);
				log.info(netCite.vsPath.join("\n"));

				netRead=new ModelPathRank();
				netRead.loadModel(fd+fnModel+".read");
				netRead.setGraph(g);
				log.info(netRead.vsPath.join("\n"));

			} catch (Exception e){
				log.error("failed to make graph: ",e);
			}
	}
	

}
