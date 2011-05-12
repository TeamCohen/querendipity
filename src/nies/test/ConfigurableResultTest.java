package nies.test;

import nies.ui.ConfigurableResult;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurableResultTest {
	public TestableConfigurableResult res;
	@Before
	public void setup() {
		res = new TestableConfigurableResult();
	}
	@Test
	public void testProse() {
		String superprose = "the quick brown fox jumped over the lazy dog";
		String prose = "the quick brown fox";
		String subprose = "the quick brown";
		String word = "the";
		String empty = "";
		
		String example = "Hepatic glutathione S-transferases in mice fed on a diet" +
				" containing the anticarcinogenic antioxidant butylated hydroxyanisole." +
				" Isolation of mouse glutathione S-transferase heterodimers by" +
				" gradient elution of the glutathione-Sepharose affinity matrix.  ";

		String[] proseString = {superprose,prose,example};
		for (String p : proseString)
			assertTrue(p,res.isProse(p));
		
		String[] nonproseString = {subprose,word,empty};
		for (String n : nonproseString)
			assertFalse(n,res.isProse(n));
	}
	public class TestableConfigurableResult extends ConfigurableResult {
		public boolean isProse(String val) { return super.isProse(val); }
	}
}
