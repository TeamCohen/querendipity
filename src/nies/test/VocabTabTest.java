package nies.test;

import ghirl.graph.BasicGraph;
import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.GraphLoader;
import ghirl.graph.NodeFilter;
import ghirl.graph.PathSearcher;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nies.ui.VocabularyResult;
import nies.ui.vocabulary.Attribute;
import nies.ui.vocabulary.Configuration;
import nies.ui.vocabulary.LabelDescription;
import nies.ui.vocabulary.ResourceResult;
import nies.ui.vocabulary.TypeDescription;

import org.junit.Test;
import static org.junit.Assert.*;



public class VocabTabTest {
	@Test
	public void testParsing() throws Exception {
		Configuration c = Configuration.configure("testvocab.xml");
		// Configuration 
		assertEquals(1, c.getTabs().getTab().size());
		assertEquals(4, c.getType().size());
		// TypeDescription
		TypeDescription base = c.getType().get(0);
		assertEquals("organisms", base.getName());
		assertTrue(base.getFilter() != null);
		// LabelDescription
		LabelDescription base_label = base.getLabel();
		assertEquals("hasabbreviation", base_label.getSelectorPath());
		System.err.println("Expect a warning here:");
		assertTrue(base_label.getSelector() != null);
		// Attributes
		Attribute att = base.attributes().get(0);
		assertTrue(att != null);
		assertTrue(att.getType() != null);
		assertEquals(att.getType(), c.get("genus"));
		LabelDescription att_label = att.getType().getLabel();
		assertEquals("hasgenus", att_label.getSelectorPath());
		assertEquals("Genus", att.getType().getTitle());
//		assertEquals("hasspecies", att_label.getSelectorPath());
//		assertEquals("hascommon_name", att_label.getSelectorPath());
		System.err.println("Expect a warning here:");
		assertTrue(att_label.getSelector() != null);
		// Manipulations
		base.setGraph(new BasicGraph());
		System.err.println("Expect nothing:");
		assertTrue(base_label.getSelector().getGraph() != null);
		assertTrue(att.getType().getGraph() != null);
		assertTrue(att_label.getSelector().getGraph() != null);
	}
	
	@Test
	public void testTitles() throws Exception {
		Configuration c = Configuration.configure("testvocab.xml");
		TypeDescription td = c.get("genus");
		assertTrue(td != null);
		assertEquals((Integer) 1,td.getMax());
		assertEquals("Genus", td.getTitle());
	}
	
	@Test
	public void testLabelDescriptionSelector() {
		LabelDescription ld = new LabelDescription();
		ld.setSelectorPath("foo");
		assertEquals("foo", ld.getSelectorPath());
		assertTrue(ld.getSelector() != null);
	}
	
	@Test
	public void testCompiling() throws Exception {
		TypeDescription td = Configuration.configure("testvocab.xml").getType().get(0);
		GraphLoader loader = new GraphLoader(new BasicGraph());
		td.setGraph(loader.getGraph());
		loader.loadLine("edge isa foo organism");
		loader.loadLine("edge hasabbreviation foo UUnob");
		loader.loadLine("edge hasgenus foo unobservi");
		loader.loadLine("edge hasspecies foo unobservabus");
		ResourceResult r = td.compile(GraphId.fromString("$foo"));
		assertTrue(r != null);
		assertEquals("UUnob", r.getLabel());
		
		VocabularyResult v = new VocabularyResult();
		v.setSource(r);
		String s = v.getHtml();
		assertTrue(s != null);
		assertTrue(s.contains("Genus"));
	}
	
	public TypeDescription createDefaultDescription(Graph graph) {
		TypeDescription rootType = new TypeDescription("root organisms", graph);
		rootType.setFilter(new NodeFilter("isa=$organism"));
		LabelDescription orglabel = new LabelDescription(graph);
		orglabel.setDefault("(unnamed organism)");
		orglabel.setSelector(new PathSearcher("hasabbreviation"));
		rootType.setLabel(orglabel);
		
		TypeDescription latin_name = new TypeDescription("latin_name", graph);
		latin_name.setTitle("Genus");
		LabelDescription latin_label = new LabelDescription(graph);
		latin_label.setDefault("(genus not listed)");
		latin_label.setSelector(new PathSearcher("hasgenus"));
		latin_name.setLabel(latin_label);
		
		TypeDescription latin_name2 = new TypeDescription("latin_name2", graph);
		latin_name2.setTitle("Species");
		LabelDescription species_label = new LabelDescription(graph);
		species_label.setDefault("(species not listed)");
		species_label.setSelector(new PathSearcher("hasspecies"));
		latin_name2.setLabel(species_label);
		
		TypeDescription common_name = new TypeDescription("common_name", graph);
		common_name.setTitle("Common Name");
		LabelDescription clabel = new LabelDescription(graph);
		clabel.setDefault("(none listed)");
		clabel.setSelector(new PathSearcher("hascommon_name"));
		common_name.setLabel(clabel);
		
		rootType.addAttribute(latin_name);
		rootType.addAttribute(latin_name2);
		rootType.addAttribute(common_name);
		
		return rootType;
	}
}
