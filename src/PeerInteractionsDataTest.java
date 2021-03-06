import junit.framework.Assert;
import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class PeerInteractionsDataTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
		PeerInteractionsData.initialize();
	}
	
	public void testValidAnalysisResults() {
		assertNotNull(PeerInteractionsData.rawData);
		assertNotNull(PeerInteractionsData.cleanData);
		assertTrue((PeerInteractionsData.percentValid >= 0 && PeerInteractionsData.percentValid <= 100));
		assertTrue((PeerInteractionsData.numberOfValidEntries > 0));
		assertTrue((PeerInteractionsData.numberOfEntries > 0));
		assertTrue((PeerInteractionsData.numberOfEntries >= PeerInteractionsData.numberOfValidEntries));
	}
}
