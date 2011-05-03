package nies.data;

import com.sleepycat.collections.StoredMap;

public interface DataView {
	public StoredMap<String,?> getPrimaryKeyMap();
}
