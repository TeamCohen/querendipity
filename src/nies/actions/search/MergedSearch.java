package nies.actions.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import nies.metadata.NiesConfig;

import org.apache.log4j.Logger;

import ghirl.graph.GraphId;
import ghirl.graph.WeightedTextGraph;
import ghirl.util.Distribution;
import ghirl.util.TreeDistribution;

public class MergedSearch extends Search {
	private static final Logger logger = Logger.getLogger(MergedSearch.class);
	protected String file;
	protected File distributionFile;
	
	@Override
	protected boolean hasSearchTerms() {
		boolean orig = super.hasSearchTerms();

		File metadataDir = new File(NiesConfig.getProperty("nies.metadataDirectory"));
		if (!metadataDir.exists()) throw new IllegalArgumentException("Webapp misconfigured: nies.metadataDirectory=" +
				metadataDir.getName()+" must exist.");
		distributionFile = new File(metadataDir,file);
		if (!distributionFile.exists()) {
			logger.info("Kicking back: Couldn't find "+distributionFile.getAbsolutePath());
			this.addFieldError("file", "The file '"+distributionFile.getName()+"' could not be found in the metadata directory ("+metadataDir.getAbsolutePath()+")");
			return false;
		}
		
		return orig;
	}
	
	@Override
	protected void buildQuery() {
		this.queryParams = "file="+this.file+",";
		super.buildQuery();
	}
	
	@Override
	protected Distribution extractResultsDistribution(WeightedTextGraph walkResults) {
		Distribution walk = walkResults.getNodeDist();
		Set<WeightedGraphId> cache = loadResults(distributionFile);
		Distribution results = new TreeDistribution();
		WeightedGraphId current = new WeightedGraphId(null,0);
		for (Iterator it = walk.iterator(); it.hasNext();) { current.graphid = (GraphId) it.next();
			if (cache.contains(current)) results.add(walk.getLastWeight(), current.graphid);
		}
		int w = walk.size(), r = results.size();
		logger.info("Original walk yielded "+w+"; removed "+(w-r)+" not in the cache.");
		return results;
	}
	protected Set<WeightedGraphId> loadResults(File df) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(df));
			Set<WeightedGraphId> storedDistribution = new TreeSet<WeightedGraphId>();
			int n = 0;
			for(String line; (line=reader.readLine())!=null; n++) {
//				logger.debug("Processing '"+line+"'");
				String[] parts = line.split(" ");
				GraphId node = GraphId.fromString(parts[0]);
				double weight=1.0;
				try {
					if (parts.length>1) weight = Double.parseDouble(parts[1]);
				} catch (NumberFormatException e) {
					logger.warn("While on line "+n+" of the distribution file "+df.getAbsolutePath()+",",e);
				}
				storedDistribution.add(new WeightedGraphId(node, weight));
			}
			logger.info("Loaded "+n+" results.");
			reader.close();
			return storedDistribution;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Trying to read the distribution file "+df.getAbsolutePath(),e);
		} catch (IOException e) {
			throw new IllegalStateException("Trying to read the distribution file "+df.getAbsolutePath(),e);
		}
	}
	
	protected Distribution loadDistribution(File df) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(df));
			Distribution storedDistribution = new TreeDistribution();
			int n = 0;
			for(String line; (line=reader.readLine())!=null; n++) {
//				logger.debug("Processing '"+line+"'");
				String[] parts = line.split(" ");
				GraphId node = GraphId.fromString(parts[0]);
				double weight=1.0;
				try {
				if (parts.length>1) weight = Double.parseDouble(parts[1]);
				} catch (NumberFormatException e) {
					logger.warn("While on line "+n+" of the distribution file "+df.getAbsolutePath()+",",e);
				}
				storedDistribution.add(weight, node);
			}
			logger.info("Loaded "+n+" results.");
			reader.close();
			return storedDistribution;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Trying to read the distribution file "+df.getAbsolutePath(),e);
		} catch (IOException e) {
			throw new IllegalStateException("Trying to read the distribution file "+df.getAbsolutePath(),e);
		}
	}
	
	protected class WeightedGraphId implements Comparable {
		GraphId graphid;
		double weight;
		public WeightedGraphId(GraphId id, double w) {
			this.graphid=id;
			this.weight=w;
		}
		public boolean equals(WeightedGraphId id) {
			return id.graphid.equals(graphid);
		}
		@Override
		public int compareTo(Object o) {
			int cmp = 0;
			if (o instanceof WeightedGraphId) {
				cmp = this.graphid.compareTo(((WeightedGraphId)o).graphid);
			}
			return cmp;
		}
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
