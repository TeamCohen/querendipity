/**************************************************************************/
/* Developed by Carnegie-Mellon Machine Learning Department
 * Written by Andrew Arnold (aarnold@cs.cmu.edu)
/**************************************************************************/
package nies.metadata;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;

import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.xml.bind.JAXBException;

import java.awt.*;
import java.awt.event.*;

import ghirl.util.*;
import ghirl.graph.*;
import edu.cmu.minorthird.util.gui.*;
import edu.cmu.minorthird.util.StringUtil;
import edu.cmu.minorthird.util.ProgressCounter;
import edu.cmu.pra.CTag;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.ModelPathRank;
import edu.cmu.pra.model.PRAModel;
import edu.cmu.lti.util.run.Param;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import nies.metadata.*;
import nies.ui.Tab;

public class Init extends HttpServlet {
	public static final String GHIRL_DBSTORE_PROP = "ghirl.dbStore";
	private ServletContext servletContext;
	private static final long serialVersionUID = -6559746026157663192L;
	static final Logger log = Logger.getLogger(Init.class);
	private static final String GRAPHSTORE_DISK = "disk";
	private static final String GRAPHSTORE_MEMORY = "memory";
	private static final String GRAPHSTORE_BSH = "bsh";
	private static final String GRAPHSTORE_CMPT = "compact";
	private static final String GRAPHSTORE_CMPTXT = "compact-text";
	private static final String GHIRL_LOAD_PROP = "ghirl.loadfiles";
	public static final String SERVLETCONTEXT_IGRAPH = "theIGraph";
	public static final String SERVLETCONTEXT_GRAPH = "theGraph";

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) {
	}

	public void init() {
		log.info("ghirl.Init logging properly.");
		servletContext = getServletContext();
		servletContext.setAttribute("initTime", new String("time: " + System.currentTimeMillis()));

		log.info("loading auto complete services...");
		String webappRoot = servletContext.getRealPath("/");
		servletContext.setAttribute("authorService", new NodeService("author",webappRoot));
		servletContext.setAttribute("pmidService",   new NodeService("pmid",  webappRoot));
		servletContext.setAttribute("geneService",   new NodeService("gene",  webappRoot));

		String tabTypes=NiesConfig.getProperty("nies.tabtypes","");
		log.info("tabTypes="+tabTypes);
		if(tabTypes.contains(Tab.PAPER) || tabTypes.contains(Tab.PMPAPER)) {
			log.info("loading paperCollection info...");
			PaperCollection paperCollection = new PaperCollection(webappRoot);
			servletContext.setAttribute("paperCollection", paperCollection);	    
			//log.warn("not found 17364011-->"+paperCollection.getStruct("17364011"));			
		} else 
			servletContext.setAttribute("paperCollection", null);


		log.info("loading external Tab configurations...");
		String[] configFileNames = NiesConfig.getProperty("nies.externalconfig", "").split(",");
		for (String file : configFileNames) {
			if ("".equals(file)) continue;
			try {
				nies.ui.vocabulary.Configuration.configure(servletContext.getRealPath(file));
				log.info("Loaded "+file);
			} catch (FileNotFoundException e) {
				log.error("Couldn't get config file "+file+", was looking in "+servletContext.getRealPath(file));
			} catch (JAXBException e) {
				log.error("Problem parsing config file "+file);
			}
		}

		log.info("Ghirl initializing...");
		try {
			Graph g = makeGraph();
			log.info("init: graph built");
			log.info("init: saving the servletContext");
			servletContext.setAttribute(SERVLETCONTEXT_GRAPH, g);
			log.info("init: saved the servletContext");
			
			String fnModel=  ghirl.util.Config.getProperty("pra.model",""); 
			model:
				if (fnModel.length()>0) {
					IGraph ig=null;
					if (g instanceof IGraph) ig = (IGraph) g;
					else if (g instanceof TextGraph 
							&& ((TextGraph)g).getInnerGraph() instanceof IGraph) 
						ig = (IGraph) ((TextGraph)g).getInnerGraph();
					else {
						log.error("Couldn't find a valid graph to load the model into. Graph or inner graph must implement IGraph.");
						break model;
					}
					servletContext.setAttribute(SERVLETCONTEXT_IGRAPH,ig);
					makeModel(fnModel,ig);
				}
		} catch (Exception e) {
			log.error("failed to make graph: ",e);
		}
		
	}

	public static Graph makeGraph() throws FileNotFoundException, IOException {
		String dbDirname = ghirl.util.Config.getProperty(ghirl.util.Config.DBDIR);
		File dbDir = new File(dbDirname);
		System.setProperty("java.awt.headless", "true");
		String graphName = ghirl.util.Config.getProperty(ghirl.util.Config.GRAPHNAME); 
		// make the graph
		log.info("init: building graph="+dbDirname+":"+graphName);

		// added March 2010 due to strange "Permission Denied" errors when opening
		// the IndexReader (even when graph directory was owned by tomcat...)
		System.setProperty("disableLuceneLocks", "true");

		String loadList;
		boolean useMutable = ((loadList = ghirl.util.Config.getProperty(GHIRL_LOAD_PROP)) != null);
		
		String dbStoreType = ghirl.util.Config.getProperty(GHIRL_DBSTORE_PROP);
		Graph g;
		if(GRAPHSTORE_DISK.equals(dbStoreType)) {
			g = GraphFactory.makeGraph("-textgraph",graphName,useMutable ? "-a" : "-r");//new TextGraph(graphName);
		} 
		else if(GRAPHSTORE_MEMORY.equals(dbStoreType)) {
			g = GraphFactory.makeGraph("-memorygraph","-load","nodes.ghirl,edges.ghirl");
		} 
		else if(GRAPHSTORE_BSH.equals(dbStoreType)) {
			g = GraphFactory.makeGraph("-bshgraph",graphName+".bsh");
		}
		else if(GRAPHSTORE_CMPT.equals(dbStoreType) ||
				 GRAPHSTORE_CMPTXT.equals(dbStoreType)) {
			SparseCompactGraph compactGraph = new SparseCompactGraph();
			

			File compactDir=new File(dbDirname,graphName);
			//log.info("compactDir="+compactDir);
		
			if (!compactDir.exists())//"graphSize.pct"
				compactDir=new File(dbDirname,graphName+ "_compact");

			if (!compactDir.exists())
					log.error("cannot find graph="+compactDir);
				
			compactGraph.load(compactDir.getPath());

			compactGraph.loadMMGraphIdx();
			if (GRAPHSTORE_CMPTXT.equals(dbStoreType)) {
				if (useMutable) {
					if (log.isInfoEnabled()) 
						log.info("Building MutableTextGraph(textindex ["+graphName+"], innergraph nestedgraph(compactgraph ["+compactDir+"], files ["+loadList+"]))");
					MutableGraph innerGraph = new NestedGraph(compactGraph);
					GraphLoader loader = new GraphLoader(innerGraph);
					File ghirlDir = new File(dbDir, graphName+"_ghirl");
					if (!ghirlDir.exists()) {
						log.warn("Went looking for load files in "+ghirlDir.getAbsolutePath()+" but couldn't find the directory!");
					} else for (String loadFileName : loadList.split(",")) {
						loader.load(new File(ghirlDir, loadFileName));
					}
					g = new MutableTextGraph(graphName, 'a', innerGraph);
				} else {
					if (log.isInfoEnabled())
						log.info("Building TextGraph(textindex ["+graphName+"], innergraph compactgraph ["+compactDir+"])");
					g = new TextGraph(graphName,compactGraph);
				}
			} else
				g=compactGraph;
		}else {
			throw new IllegalStateException("'ghirl.dbStore' had unfamiliar value: '"+dbStoreType+"'. You must specify property 'ghirl.dbStore' to be 'memory' or 'disk'");
		}
		
//		if (useMutable) {
//			GraphLoader loader = new GraphLoader((MutableGraph) g);
//			File ghirlDir = new File(dbDir + File.separator + graphName + File.separator + graphName+"_ghirl");
//			if (!ghirlDir.exists()) {
//				Logger.getRootLogger().warn("Went looking for load files in "+ghirlDir.getAbsolutePath()+" but couldn't find the directory!");
//			} else for (String loadFileName : loadList.split(",")) {
//				loader.load(new File(ghirlDir, loadFileName));
//			}
//		}
		
		return g;
	}
	
	public void makeModel(String fnModel, IGraph g) {
		String fd=  ghirl.util.Config.getProperty("pra.dir"); 
		String fnConf=  ghirl.util.Config.getProperty("pra.conf");   
		log.info("loading model="+fd+fnModel);

			try {			


				log.info("pra.dir="+fd);
				log.info("pra.conf="+fnConf);
				log.info("pra.model="+fnModel);


				Param.overwriteFrom(fd+fnConf);
				Param.overwrite("dataFolder="+fd);


				
				PRAModel netCite=new ModelPathRank();
				log.info(netCite.schema.vRel.getVS(CTag.nameS));

				netCite.loadModel(fd+fnModel+".cite");
				netCite.setGraph(g);
				log.info(netCite.vsPath.join("\n"));

				PRAModel netRead=new ModelPathRank();
				netRead.loadModel(fd+fnModel+".read");
				netRead.setGraph(g);
				log.info(netRead.vsPath.join("\n"));


				if (servletContext != null){
					servletContext.setAttribute(MODEL_CITE, netCite);
					servletContext.setAttribute(MODEL_READ, netRead);
				}

			} catch (Exception e){
				log.error("failed to make graph: ",e);
			}
	}
	public static String MODEL_CITE="MODEL_CITE";
	public static String MODEL_READ="MODEL_READ";
	
	public void destroy() {
		Graph g = (Graph) servletContext.getAttribute(SERVLETCONTEXT_GRAPH);
		if (g instanceof Closeable)
			try {
				((Closeable) g).close();
			} catch (IOException e) {
				log.error("Problem closing graph:",e);
			}
	}

	protected void forceUnlock(String dbname) {
		File dbfile = new File(dbname+"_db/je.lck");
		if (dbfile.exists()) {
			log.warn("Lock file exists for graph "+dbname+".  Forcibly deleting...");
			if (!dbfile.delete()) log.error("Could not delete lock file!");
			else                  log.info ("Lock file successfully deleted.");
		}
	}
}
