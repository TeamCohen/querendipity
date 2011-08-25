package nies.data.apps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import nies.actions.search.Search;
import nies.metadata.Init;
import ghirl.graph.Graph;
import ghirl.graph.GraphId;
import ghirl.graph.WeightedTextGraph;
import ghirl.util.Distribution;

public class TextWalk {
	public static void main(String args[]) throws Exception {
		if (args.length < 4) {
			System.out.println("Usage:\n" +
					"\t"+TextWalk.class.getCanonicalName()+" -auto -depth d -limit N -query word1 word2 word3 ...");
			return;
		}
		int depth=1;
		int limit=-1;
		StringBuilder kw = new StringBuilder();
		boolean shell=true;
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-auto")) shell = false;
			else if (args[i].equals("-depth")) depth = Integer.parseInt(args[++i]);
			else if (args[i].equals("-limit")) limit = Integer.parseInt(args[++i]);
			else if (args[i].equals("-query")) kw.append(args[++i]);
			else kw.append(" ").append(args[i]);
		}
		Graph g = Init.makeGraph();
		Search s = new Search();
		s.prepare("Search");
		s.setGraph(g);
		s.setKeywords(kw.toString());
		s.setDepth(depth);
		if (shell) {
		    System.err.println("Confirm query:\n" +
				"\t"+s.getKeywords()+" [depth="+depth+" steps="+s.getNUMSTEPS()+"]\n" +
				"(hit enter)");
		    new Scanner(System.in).nextLine();
		}
		try {
		    s.execute();
		} catch (Exception e) {
			if (s.getGraphResults() == null) throw (e);
		}
		Distribution results = s.getGraphResults().getNodeDist();
		if (limit >0) results = results.copyTopN(limit);
		int i=0;
		for (Iterator it=results.orderedIterator(); it.hasNext(); i++) {
			GraphId node = (GraphId) it.next();
			double wt = results.getLastWeight();
			System.out.println(node.toString()+" "+wt);
		}
		System.err.println(i+" results printed.");
	}
	
}
