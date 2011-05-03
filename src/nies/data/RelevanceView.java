package nies.data;


import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.collections.StoredSortedValueSet;

public class RelevanceView implements DataView{
	private static final Logger logger = Logger.getLogger(RelevanceView.class);
	private StoredMap<String,Relevance> relevanceMap;
	private StoredMap<RelevanceDatabase.QUDTuple,Relevance> relevanceByQudMap;
	private StoredMap<String,Relevance> relevanceByUMap;
	public StoredMap<String,Relevance> getRelevanceByUMap() {
		return relevanceByUMap;
	}
	public StoredMap<RelevanceDatabase.QUDTuple,Relevance> getRelevanceByQudMap() {
		return relevanceByQudMap;
	}
	public StoredMap<String,Relevance> getRelevanceMap() {
		return relevanceMap;
	}
	public StoredEntrySet<String,Relevance> getRelevanceEntrySet() {
		return (StoredEntrySet<String, Relevance>) relevanceMap.entrySet();
	}
	public RelevanceView (RelevanceDatabase db) {
		ClassCatalog catalog = db.getJavaCatalog();
		SerialBinding<String> relevanceKeyBinding = new SerialBinding<String>(catalog, String.class);
		SerialBinding<RelevanceDatabase.QUDTuple> qudKeyBinding = 
			new SerialBinding<RelevanceDatabase.QUDTuple>(catalog, RelevanceDatabase.QUDTuple.class);
		SerialBinding<String> uKeyBinding = 
			new SerialBinding<String>(catalog, String.class);
		EntityBinding<Relevance> relevanceBinding = new RelevanceBinding(catalog, String.class, RelevanceData.class);
		this.relevanceMap = new StoredMap<String,Relevance>(db.getRelevanceDB(), relevanceKeyBinding, relevanceBinding, true);
		this.relevanceByQudMap = 
			new StoredMap<RelevanceDatabase.QUDTuple,Relevance>(db.getRelevanceByQudDB(), 
																qudKeyBinding, 
																relevanceBinding, 
																true);
		this.relevanceByUMap =
			new StoredMap<String,Relevance>(db.getRelevanceByUDB(),
						                    uKeyBinding,
						                    relevanceBinding,
						                    true);
	}
	
	private static class RelevanceBinding extends SerialSerialBinding<String,RelevanceData,Relevance> {
		private RelevanceBinding(ClassCatalog catalog, Class<String> keyclass, Class<RelevanceData> dataclass) {
			super(catalog, keyclass, dataclass);
		}
		public Relevance entryToObject(String rid, RelevanceData data) {
			return new Relevance(data,rid);
		}
		public String objectToKey(Relevance rel) {
			return new String(rel.getRid());
		}
		public RelevanceData objectToData(Relevance rel) {
			return new RelevanceData(rel);
		}
	}

	public StoredSortedValueSet<Relevance> getRelevanceSet() {
		return (StoredSortedValueSet<Relevance>) this.relevanceMap.values();
	}

	public StoredMap<String,?> getPrimaryKeyMap() { return relevanceMap; }
	
	public String isRelevant(Query query, User user, String document) {
		ArrayList<Relevance> all = new ArrayList<Relevance>();
		all.addAll(relevanceByQudMap.duplicates(new RelevanceDatabase.QUDTuple(query,user,document)));
		if (all.size() == 0) {
			if (logger.isDebugEnabled()) logger.debug("No results matching [query]"+query.toString()+" [user] "+user.toString()+" [document] "+document);
			return Relevance.UNMARK;
		}
		Collections.sort(all, Relevance.MOST_RECENT_FIRST);
		return all.get(0).getType();
	}
}
