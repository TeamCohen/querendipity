package nies.data;

import com.sleepycat.bind.EntityBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.SerialSerialBinding;
import com.sleepycat.collections.StoredMap;

public class QueryView implements DataView {
	private StoredMap<String,Query> queryMap;
	private StoredMap<String,Query> queryByQuerystringMap;
	public StoredMap<String,Query> getQueryMap() {
		return queryMap; 
	}
	
	/**
	 * @return the queryByQuerystringMap
	 */
	public StoredMap<String, Query> getQueryByQuerystringMap() {
		return queryByQuerystringMap;
	}

	public QueryView(RelevanceDatabase db) {
		ClassCatalog catalog = db.getJavaCatalog();
		SerialBinding<String> queryKeyBinding       = new SerialBinding<String>(catalog, String.class);
		SerialBinding<String> querystringKeyBinding = new SerialBinding<String>(catalog, String.class);
		EntityBinding<Query>  queryBinding          = new QueryBinding(catalog, String.class, QueryData.class);
		this.queryMap = new StoredMap<String, Query>(db.getQueryDB(), queryKeyBinding, queryBinding, true);
		this.queryByQuerystringMap = new StoredMap<String,Query>(db.getQueryByQuerystringDB(), querystringKeyBinding,queryBinding,true);
		
	}
	
	private static class QueryBinding extends SerialSerialBinding<String,QueryData,Query> {
		private QueryBinding(ClassCatalog catalog, Class<String> keyclass, Class<QueryData> dataclass) {
			super(catalog, keyclass, dataclass);
		}
		public Query entryToObject(String qid, QueryData data) {
			return new Query(qid, data);
		}
		public String objectToKey(Query query) {
			return new String(query.getQid());
		}
		public QueryData objectToData(Query query) {
			return new QueryData(query); 
		}
	}

	public StoredMap<String,?> getPrimaryKeyMap() { return queryMap; }
}
