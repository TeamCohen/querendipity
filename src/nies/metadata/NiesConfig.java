package nies.metadata;

import java.io.*;
import java.util.*;
//import org.apache.log4j.Logger;

import org.apache.log4j.Logger;

/**
 * Access properties specified in System.getProperties() or nies.properties, in that order.
 */
public class NiesConfig
{
//  private static final Logger log = Logger.getLogger(NiesConfig.class);
    private static Properties props;
    static {
    	init();
    }
    public static void init() {
    	props = new Properties();
		try {
		    InputStream in = NiesConfig.class.getClassLoader().getResourceAsStream("nies.properties");
		    if (in != null) {
		    	props.load(in);
		    	Logger.getRootLogger().debug("Loaded nies.properties");
		    } else { Logger.getRootLogger().warn("No nies.properties found in the class loader resource pool."); }
		} catch (IOException e) {
		    throw new IllegalStateException("error getting nies.properties: "+e);
		}
		// override properties with command line, if a flag is present
		for (Enumeration i=System.getProperties().propertyNames(); i.hasMoreElements(); ) {
		    String p = (String)i.nextElement();
		    if (props.containsKey(p))
		    	props.setProperty(p, System.getProperty(p));
		}
    }
    public static Properties getProperties() { return props; }
    public static void       setProperty(String prop, String val) { props.setProperty(prop, val); }
    public static String     getProperty(String prop) { 
    	if (props.containsKey(prop)) return props.getProperty(prop); 
    	else return System.getProperties().getProperty(prop); 
    }
    public static String     getProperty(String prop,String def) { 
    	if (props.containsKey(prop)) return props.getProperty(prop,def); 
    	else return System.getProperties().getProperty(prop,def); 
    }
}
