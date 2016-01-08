import java.util.ArrayList;
import java.util.Collections;

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

	/**
	 * TODO: Navneenth, please write a series of tests that will put the roster object model through its paces.
	 */
	public void testRosterObject() {
		assertTrue(false); //method is incomplete
	}

	/**
	 * TODO improve to also test 
	 *      Roster.addPeerInteractions(Iterable<PeerInteraction>)
	 * TODO Navneenth, please finish adding asserts instead of/in addition to print statements.
	 */
	public void testRosterIterator() {		
		int CAPACITY = 1000;
		int SIZE = 200;
		int DELTA = 500;
		Roster testRoster = new Roster(CAPACITY);
		ArrayList<Integer> oneToThousand = new ArrayList<>();
		for (int i = 0; i < CAPACITY; ++i)
			oneToThousand.add(i);
		Collections.shuffle(oneToThousand);
		int count = 0;
		for (int i = 0; i < SIZE; ++i){
			int number = oneToThousand.get(i);
			testRoster.addStudent(number);
		}
		
		//Test to see that iteration works as expected
		for (Student person : testRoster){
			++count;
			System.out.printf("ID: %d\n", person.getID());
		}
		
		//Check to see that iterator reached all students
		System.out.printf("Number of iterations: %d\n", count);
		System.out.printf("Size of roster: %d\n\n", testRoster.size());
		//Check that bounds exception is properly thrown
		try{
			//Randomly tries underflow or overflow by up to DELTA
			int wildcard = Math.random() > 0.50 ? CAPACITY : -DELTA;
			testRoster.addStudent(wildcard + (int)(Math.random()*DELTA));
			assertTrue(false); //Bounds check test failed.
		} catch(IndexOutOfBoundsException e){
			System.out.println(e.getMessage());
			assertTrue("Bounds check test successful.", true);
		}
		
		//After this resize, no bounds exceptions should be thrown.
		//But the method should properly handle assignments to IDs
		//that are already taken.
		testRoster.resize(CAPACITY + DELTA);
		for (int i = 0; i < CAPACITY + DELTA; ++i){
			try{
				testRoster.addStudent(i);
				System.out.printf("Added Student %d\n", i);
			}catch(IllegalArgumentException e){
				System.out.println(e.getMessage());
			}
		}
	}
}
