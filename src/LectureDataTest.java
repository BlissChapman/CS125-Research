import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class LectureDataTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
		PeerInteractionsData.initialize();
		LectureData.initialize();
	}
	
	public void testValidAnalysisResults() {
		assertNotNull(LectureData.lectures);
		assertTrue(LectureData.mean > 0 && LectureData.mean < 10);
		assertTrue(LectureData.stdDev > 0);
	}
}
