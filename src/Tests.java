import junit.framework.TestCase;

/**
* @author BlissChapman
*/
public class Tests extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		CheckInputOutput.setUp();
		super.setUp();
	}
	
	public void testBuildRanCompletely() {
		Main.main(new String[0]);
		CheckInputOutput.checkOutputContains("ANALYSIS COMPLETE");
	}
}