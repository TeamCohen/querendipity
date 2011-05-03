package nies.data;

import org.apache.log4j.Logger;

public class KeyGenerator {
	private static final Logger logger = Logger.getLogger(KeyGenerator.class);
	private int key=0;  
	
	public String newKey() {
		key++;
		return getKey(key);
	}
	
	public String getKey(int k) {
		return String.format("%010d",k);
	}
	public void initializeKeyGenerator(String last) {
		try {
			key = Integer.parseInt(last);
		} catch(NumberFormatException e) { 
			logger.error("Can't initialize primary key generator with found key \""+last+"\"",e); 
		}
	}
}
