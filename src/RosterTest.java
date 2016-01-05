import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class RosterTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
	}
	
	public void testMain() {
		assertNotNull(LectureData.lectures);
		assertTrue(LectureData.mean > 0 && LectureData.mean < 10);
		assertTrue(LectureData.stdDev > 0);
	}
	
	public void testRosterObjectModel() {
		/* TODO: Navneenth, write a series of tests here that walks the roster object model through its paces*/
	}
}
