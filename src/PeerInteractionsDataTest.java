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
	
	public void testDataNull() {
		assertNotNull(PeerInteractionsData.rawData);
		assertNotNull(PeerInteractionsData.cleanData);
	}
}
