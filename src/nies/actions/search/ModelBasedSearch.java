package nies.actions.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import nies.actions.ReadingHistory;
import nies.metadata.EntityCollection;
import nies.metadata.Init;
import nies.metadata.NiesConfig;

import org.apache.log4j.Logger;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.TMapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.html.EColorScheme;
import edu.cmu.pra.learner.Query;
import edu.cmu.pra.model.PRAModel;
import ghirl.graph.CompactGraph;
import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.ICompact;
import ghirl.graph.WeightedTextGraph;
import ghirl.util.CompactImmutableArrayDistribution;
import ghirl.util.Distribution;

public class ModelBasedSearch extends Search {
	private static final Logger logger = Logger.getLogger(ModelBasedSearch.class);
	private int year=2009;
	protected EntityCollection entityCollection;
	protected PRAModel modelCite;
	protected PRAModel modelRead;

	public void setYear(int y) { this.year = y; }
	public int getYear() { return this.year; }

	public ModelBasedSearch(){
		sModel=ghirl.util.Config.getProperty("pra.model"); 
	}

	boolean usingHistory=false;
	public void setUsingHistory(boolean usingHistory) {
		this.usingHistory = usingHistory;
	}
	public boolean getUsingHistory() {
		return usingHistory;
	}
	public boolean isUsingHistory(){
		return usingHistory;
	}
	
	String standingUser;
	public void setStandingUser(String standingUser) {
		this.standingUser = standingUser;
	}
	public String getStandingUser() {
		return standingUser;
	}

	String sModel;
	public void setModel(String sModel) {
		this.sModel = sModel;
	}
	public String getModel() {
		return sModel;
	}

	int debug;
	public void setDebug(int debug) {
		this.debug = debug;
	}
	public int getDebug() {
		return debug;
	}

	public void prepare() {
		super.prepare();
	}

	@Override
	public void setServletContext(ServletContext context) {
		super.setServletContext(context);
		this.graph = (Graph) context.getAttribute(Init.SERVLETCONTEXT_IGRAPH);
		if (this.graph == null) return;
		this.entityCollection = (EntityCollection) context.getAttribute("entityCollection");

		String fnModel=  ghirl.util.Config.getProperty("pra.model"); 
		if (fnModel!=null) if (fnModel.length()>0){
			this.modelCite  = (PRAModel)  
				context.getAttribute(Init.MODEL_CITE);
			this.modelRead  = (PRAModel)  
				context.getAttribute(Init.MODEL_READ);
		}
	}

	public List<String> getAvailableModels() {
		return null;
	}


	protected boolean hasSearchTerms() { 
		ICompact g = (ICompact)graph;

		/*
		int idx= g.getNodeIdx(new GraphId("author", standingUser));
    if (idx==-1) {
    	logger.debug("no user found for '"+standingUser+"'");
    	return false;
    }*/
		return true; 
		//return this.isValid(this.queryTerms)

	}

	public static class QueryCache{
		public MapID rlt= null;//new TMapSX<MapID>(MapID.class);
		public MapSS mExplanations= new MapSS();// null;//
		/*public QueryCache(MapID rlt,MapSS mExplanations){
			this.rlt=rlt;
			this.mExplanations=mExplanations;
		}*/
		public QueryCache(MapID rlt){
			this.rlt=rlt;
		}
	}
	public static TMapSX<QueryCache> mCacheCite= new TMapSX<QueryCache>(QueryCache.class);
	public static TMapSX<QueryCache> mCacheRead= new TMapSX<QueryCache>(QueryCache.class);
	// how about we caching Query's?
	//public static TMapSX<MapID> mCacheResult= new TMapSX<MapID>(MapID.class);
	public static MapSS mCacheExplanations=null;// new MapSS();

	public static Comparator<Map.Entry<Integer,Double>> SORT_BY_SCORE = new Comparator<Map.Entry<Integer,Double>>() {
		@Override public int compare(Entry<Integer, Double> o1,
				Entry<Integer, Double> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	public static String printReasons(int paperId, MapID mScores){
		StringBuffer sb= new StringBuffer();
		List<Map.Entry<Integer,Double>> reasonsList = new ArrayList<Map.Entry<Integer,Double>>();
		reasonsList.addAll(mScores.entrySet());
		Collections.sort(reasonsList, SORT_BY_SCORE);
		for (Map.Entry<Integer, Double> e: reasonsList){
			int i=e.getKey();
			double d=e.getValue();
			if (Math.abs(d)<0.1) continue;

			String txt=String.format("<span class=\"rid\">%d</span><span class=\"score\">(%.1f)</span>",i,d);

			sb.append(String.format(
					"\n\t\t<a class=\"reason r%d\" href=\"ajaxstrings.view?field=-1&amp;id=%d\">%s</a>"
					,EColorScheme.Spectral11.getID(d),i+1,txt) ); // make reason IDs 1-indexed to skip bias term
		}
		return sb.toString();
	}

	protected void buildQuery() {
		logger.debug("  <<< usingHistory >>>  ="+usingHistory);

		this.queryParams = "usingHistory="+usingHistory+",";
		super.buildQuery();
	}
	protected WeightedTextGraph doQuery(String query) {

		if (graph==null) {
			this.addActionError("The current graph doesn't support Model-Based Search. Talk to Katie or Ni about that.");
			return null;
		}
		
		//logger.debug("  <<< usingHistory >>>  ="+usingHistory);
		
		
		logger.debug("Extracting initial distributions...");
		ICompact g = (ICompact)graph;	

		int idx= g.getNodeIdx(new GraphId("author", standingUser));
		if (idx==-1) {
			logger.debug("no user found for '"+standingUser+"'");
			return null;
		}

		String yearQ=""+(year+1);		
		String qLine=yearQ+","+year+","+standingUser+",";
		logger.debug("qLine="+qLine);
		//String qLine=query;		logger.debug("query="+qLine);

		//MapID rlt= mCacheResult.get(qLine);
		QueryCache qc= usingHistory?mCacheRead.get(qLine):mCacheCite.get(qLine);
		if (qc!=null){
			logger.debug("use cached result: |rlt|="+qc.rlt.size());			
		}
		else{
			if (this.usingHistory){
				String fd=NiesConfig.getProperty(ReadingHistory.READING_HISTORY_DIRECTORY_PROP);
				File fp = new File(fd,user.getUsername());
				VectorS vsHistory =VectorS.fromFile(fp.getPath());
				logger.debug("usingHistory of "+user.getUsername()
						+" #pmids="+vsHistory.size());
				//logger.debug(vsHistoryPMID);
				CompactGraph cg= (CompactGraph)graph;
				//cg.AddExtraLinks("Read"	,"author", standingUser
					//,"paper", vsHistory.toArray());
			}
			
			Query q=usingHistory?
					modelRead.parseQuery(qLine):
					modelCite.parseQuery(qLine);
					
			if(	usingHistory)
				modelRead.predict(q);
			else
				modelCite.predict(q);	   
			
			logger.debug("|q.mResult|="+q.mResult.size());

			qc= new QueryCache(q.mResult.subLargerThan(0.0));
			logger.debug("|rlt|="+qc.rlt.size() +" after removing negative results");

			double maxScore=qc.rlt.max();
			//rlt.devideOn();
			logger.debug("maxScore="+maxScore);
			//logger.debug("doQuery(): rlt="+qc.rlt.firstEntry());

			for (Map.Entry<Integer, Double> e: qc.rlt.entrySet()){
				MapID m=q.A.getRowM(e.getKey()).multiply(
					(	usingHistory)?	modelRead.vwFeature:modelCite.vwFeature);
				m.devideOn(e.getValue());

				String exp= String.format("<div class=\"totalScore\">Total score: %.1f</div>\n\t<div class=\"reasonsContainer\">Reasons: %s</div>"
						,e.getValue()/maxScore
						,printReasons(e.getKey(), m));

				String pmid=getPMIDfromNodeName( 
					((CompactGraph)graph).getNodeName(e.getKey()));

				qc.mExplanations.put(pmid, exp);
				//logger.debug("explanation of "+pmid +": "+exp);

			}

			//mCacheResult.put(qLine, rlt);
			//mCacheExplanations.clear();
			if(	usingHistory)
				mCacheRead.put(qLine, qc);
			else
				mCacheCite.put(qLine, qc);
		}

		mCacheExplanations=qc.mExplanations;//point to the Exp cache of the current query

		Distribution d=new CompactImmutableArrayDistribution(qc.rlt, (ICompact)graph);

		//logger.debug("doQuery(): d="+d.toMapID());
		//SetI miWord= g.getNodeIdx("word", query.split(" "));


		return new WeightedTextGraph(d,graph);

		/*
		Distribution initDist = getInitialDistribution(query);

        logger.debug("Using initial distribution of "+initDist.size()+" elements.");
        walker.setGraph(graph);
        walker.setInitialDistribution( initDist );
        walker.setUniformEdgeWeights(); // TODO: ?? should edge weights really start at uniform?
        walker.setSamplingPolicy(false);
        walker.reset();
        walker.walk();
        logger.debug("returning WeightedTextGraph...");
        return new WeightedTextGraph(initDist,walker.getNodeSample(),graph);
		 */
	}
	public static String getPMIDfromNodeName(String name){
		if (name.length()>6)
			return name.substring(6);//paper$ .split("$")[1];

		logger.warn("weird pmid="+name);
		return name;
	}
}
