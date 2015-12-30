import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class MainTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
	}
	
	public void testBuildRanCompletely() {
		Main.main(new String[0]);

		if (!Main.buildSucceeded)
			fail("Build did not complete.");
	}
}