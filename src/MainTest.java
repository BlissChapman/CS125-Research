import junit.framework.TestCase;

/**
* @author BlissChapman
*/
public class MainTest extends TestCase {
	
	public void tearDown() {
		CheckInputOutput.resetInputOutput();
	}
	public void setUp() throws Exception{
		CheckInputOutput.setUp();
		super.setUp();
	}
	
	public void testBuildRanCompletely() {
		CheckInputOutput.resetInputOutput();

		Main.main(new String[0]);

		if (!Main.buildSucceeded)
			fail("Build did not complete.");
	}
}