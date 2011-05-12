package nies.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import nies.metadata.NiesConfig;
import nies.ui.Tab;

import org.junit.Test;

public class ConfigurableTabTest {
	@Test
	public void test() throws FileNotFoundException, IOException {
		NiesConfig.getProperties().load(new FileReader("nies.properties"));
		Tab t = Tab.makeTab(Tab.CONFIGURABLE, "Papers");
	}
	
	@Test
	public void propertiesTest() {
		Properties prop = new Properties();
		prop.setProperty("foo", "bar");
		String[] quux = {"quux"};
		prop.put("baz", quux);
		assertEquals("bar",prop.get("foo"));
		assertEquals("quux",prop.getProperty("baz"));
	}
}
