package nies.test;

import static org.junit.Assert.*;

import edu.cmu.pra.model.PRAModel;
import ghirl.graph.ICompact;
import ghirl.graph.WeightedTextGraph;


import nies.actions.search.ModelBasedSearch;
import nies.data.User;
import nies.metadata.PaperCollection;

import org.junit.Test;
public class ModelBasedSearchTest {
	@Test
	public void test() throws Exception {
		GraphInitializationTest init = new GraphInitializationTest();
		init.testModelBasedGraph();
		TestableMBSearch search = new TestableMBSearch();
		PaperCollection paperCollection = new PaperCollection();
		search.setPaperCollection(paperCollection);
		search.setIGraph((ICompact) init.igraph);
		search.setGraph(init.graph);
		search.setModelCite(init.netCite);
		search.setModelRead(init.netRead);
		search.setStandingUser("Woolford_JL");
		search.setUser(new User(), null);
		
		search.prepare("ModelBasedSearch");
		search.execute();
	}
	
	public class TestableMBSearch extends ModelBasedSearch {
		public WeightedTextGraph doQuery(String query) {
			return super.doQuery(query);
		}
		public void setModelCite(PRAModel mod) {
			this.modelCite = mod;
		}
		public void setModelRead(PRAModel mod) {
			this.modelRead = mod;
		}
		public void setIGraph(ICompact g) {
			this.igraph = g;
		}
		public void setPaperCollection(PaperCollection pc) { this.paperCollection = pc; }
	}
}
