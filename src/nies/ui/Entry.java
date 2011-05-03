/**
 * 
 */
package nies.ui;

/**
 * Utility class holding any pair of matched data.
 * @see ConfigurableTab
 * @see ConfigurableResult
 * @author katie
 *
 * @param <K> Generally a String but needn't be.
 * @param <V> 
 */
public class Entry<K,V> {
	protected K key;
	protected V value;
	public Entry(K k, V val) {
		key=k; value=val;
	}
	public K getKey()   { return key; }
	public V getValue() { return value; }
}