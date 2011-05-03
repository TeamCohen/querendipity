package nies.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import nies.ui.vocabulary.Configuration;
import nies.ui.vocabulary.LabelDescription;
import nies.ui.vocabulary.ResourceResult;
import nies.ui.vocabulary.TypeDescription;
import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.NodeFilter;
import ghirl.graph.PathSearcher;

public class VocabularyTab extends Tab {
	private static final Logger logger = Logger.getLogger(VocabularyTab.class);
	private TypeDescription rootType;
	private Graph graph;
	
	public NodeFilter getFilter() { return rootType.getFilter(); }

	protected VocabularyTab(String name) {
		this.displayType = Tab.VOCABULARY;
		ServletContext servletContext = ServletActionContext.getServletContext();
		try {
			graph = (Graph) servletContext.getAttribute("theGraph");
		} catch (NullPointerException e) {
			logger.error("No graph...!");
		}
		this.rootType = Configuration.get(name);
		if(rootType == null) {
			throw new IllegalArgumentException("No tab type available for name '"+name+"'");
		}
		
		this.rootType.setGraph(graph);
		this.title = rootType.getTitle();
	}
	
	protected Result processResult(Object node, Result result) {
		VocabularyResult endresult = new VocabularyResult(result);
		
		ResourceResult r = rootType.compile((GraphId) node);
		
		endresult.setSource(r);
		return endresult;
	}
}
