package nies.actions;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import nies.metadata.NiesConfig;

import com.opensymphony.xwork2.Preparable;

public class PropertyManager extends NiesSupport implements Preparable {
	private static final Logger logger = Logger.getLogger(PropertyManager.class);
	private static final Comparator ALPHABETICAL_KEY_ORDER = new Comparator<Entry>() {
		@Override
		public int compare(Entry o1, Entry o2) {
			return o2.getKey().toString().compareTo(o1.getKey().toString());
		}
	};
	private Properties niesProperties;
	private Properties ghirlProperties;
	
	public String save() {
		return SUCCESS;
	}
	
	@Override
	public void prepare() throws Exception {
		niesProperties  = NiesConfig.getProperties();
		ghirlProperties = ghirl.util.Config.getProperties();
	}
	public Properties getNiesProperties() { 
		if (niesProperties == null)
			logger.warn("Null niesProperties object?!");
		return niesProperties; 
	}
	public void setNiesAppend(String append) {
		StringReader reader = new StringReader(append);
		try {
			niesProperties.load(reader);
		} catch (IOException e) {
			logger.warn("Couldn't read NIES string: "+e.getMessage());
			this.addActionError("Couldn't read NIES string: "+e.getMessage());
		}
		reader.close();
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Current nies settings:");
			for (Entry<Object, Object> e : niesProperties.entrySet()) {
				sb.append("\n\t").append(e.getKey()).append(": ").append(e.getValue());
			}
			logger.debug(sb.toString());
		}
	}
	
	public Properties getGhirlProperties() { 
		if (ghirlProperties == null)
			logger.warn("Null ghirlProperties object?!");
		return ghirlProperties; 
	}
	public void setGhirlAppend(String append) {
		StringReader reader = new StringReader(append);
		try {
			ghirlProperties.load(reader);
		} catch (IOException e) {
			this.addActionError("Couldn't read GHIRL string: "+e.getMessage());
		}
		reader.close();
	}
	public Comparator getKeysort() { return ALPHABETICAL_KEY_ORDER; }
}
