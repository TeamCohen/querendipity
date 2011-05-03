package nies.test;

import nies.metadata.PaperCollection;

import org.junit.Test;

public class TestPRA {
	
	@Test
	public void testInit() {
		bsh.Interpreter interp = new bsh.Interpreter();
		ghirl.util.Config.setProperty("ghirl.dbDir", "test/graphs");
		String dbDir = ghirl.util.Config.getProperty("ghirl.dbDir");
			PaperCollection pc = new PaperCollection();
			pc.init("/usr0/nlao/code_java/ni/run/yeast2/preprocess/"
					,"pmid.sgd.crawl.ex");
			
		for (String pmid : new String[]{
				"17364011",  "18057865", "18496613", "18512161", "18563599"
				, "18665420","18687336" , "18703157","18716881" , "18758490"})
			System.out.println(pmid+"-->"+pc.getStruct(pmid));
		return;
	}
	// 
}
